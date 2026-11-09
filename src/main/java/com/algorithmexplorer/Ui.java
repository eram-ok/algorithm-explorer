package com.algorithmexplorer;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/** Shared presentation helpers. No algorithm or data-structure logic lives here. */
public final class Ui {
    private Ui() {}

    public static Label label(String text, String style) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.getStyleClass().add(style);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    public static VBox field(String name, Control control) {
        Label label = label(name, "field-label");
        label.setLabelFor(control);
        control.setAccessibleText(name);
        control.setMaxWidth(Double.MAX_VALUE);
        return new VBox(7, label, control);
    }

    public static VBox card(String heading, Node... children) {
        VBox card = new VBox(14);
        card.getStyleClass().add("card");
        card.setMinWidth(0);
        if (!heading.isBlank()) card.getChildren().add(label(heading, "section-title"));
        card.getChildren().addAll(children);
        return card;
    }

    public static VBox heading(String eyebrow, String title, String subtitle) {
        VBox box = new VBox(7, label(eyebrow, "eyebrow"),
            label(title, "page-title"), label(subtitle, "subtitle"));
        box.getStyleClass().add("page-heading");
        return box;
    }

    public static ScrollPane scroll(Node content) {
        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setMinHeight(0);
        scroll.getStyleClass().add("page-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        return scroll;
    }

    public static TitledPane details(String title, Node... content) {
        VBox body = new VBox(12, content);
        body.getStyleClass().add("details-body");
        TitledPane pane = new TitledPane(title, body);
        pane.setExpanded(false);
        pane.setAnimated(false);
        return pane;
    }

    public static void status(Label label, String state) {
        if (!label.getStyleClass().contains("status")) label.getStyleClass().add("status");
        label.setMaxWidth(Double.MAX_VALUE);
        label.pseudoClassStateChanged(PseudoClass.getPseudoClass("error"), "error".equals(state));
        label.pseudoClassStateChanged(PseudoClass.getPseudoClass("success"), "success".equals(state));
    }

    public static void primary(Button button) { button.getStyleClass().add("primary"); }
    public static void danger(Button button) { button.getStyleClass().add("danger"); }

    public static boolean confirmReset(Node owner, String name) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
            "Clear the " + name + " and its operation history?", ButtonType.CANCEL, ButtonType.OK);
        alert.setTitle("Reset " + name);
        alert.setHeaderText("Start with an empty " + name + "?");
        if (owner.getScene() != null) alert.initOwner(owner.getScene().getWindow());
        alert.getDialogPane().getStylesheets().add(Ui.class.getResource("theme.css").toExternalForm());
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    public static String time(long ns) {
        if (ns < 1_000) return ns + " ns";
        if (ns < 1_000_000) return String.format(java.util.Locale.ROOT, "%.2f µs", ns / 1_000.0);
        return String.format(java.util.Locale.ROOT, "%.2f ms", ns / 1_000_000.0);
    }

    /** Metric tiles wrap rather than shrinking text on narrow windows. */
    public static class Stats extends FlowPane {
        private final Label[] values;
        public Stats(String... names) {
            super(12, 12);
            values = new Label[names.length];
            for (int i = 0; i < names.length; i++) {
                values[i] = label("—", "stat-value");
                VBox tile = new VBox(8, label(names[i], "stat-label"), values[i]);
                tile.getStyleClass().add("stat-tile");
                tile.setPrefWidth(165);
                getChildren().add(tile);
            }
        }
        public void set(String... data) {
            for (int i = 0; i < values.length; i++) values[i].setText(i < data.length ? data[i] : "—");
        }
        public void clear() { set(); }
    }
}
