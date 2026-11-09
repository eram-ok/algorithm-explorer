package com.algorithmexplorer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class LinkedListView extends VBox {

    private CustomLinkedList list = new CustomLinkedList();

    private final TextField inputField = new TextField();
    private final Label status = new Label();
    private final Label state = new Label();
    private final Label metrics = new Label();

    private final HBox nodeDisplay = new HBox(10);
    private final TextArea stepsArea = new TextArea();
    private final TextArea historyArea = new TextArea();

    private int attemptNumber;
    private final Ui.Stats stats = new Ui.Stats("COUNTED OPS", "RUN TIME", "ITEMS", "ATTEMPT");

    public LinkedListView(Runnable onBack) {
        setPadding(new Insets(0));
        setSpacing(0);


        Button back = new Button("Back to Home");
        back.setOnAction(event -> onBack.run());

        Label title = new Label("Singly Linked List Explorer");


        inputField.setPromptText(
            "Enter a whole number, then choose an operation"
        );

        Button insertFirst = new Button("Insert First");
        insertFirst.setOnAction(event -> perform("Insert First"));

        Button insertLast = new Button("Insert Last");
        insertLast.setOnAction(event -> perform("Insert Last"));

        Button search = new Button("Search");
        search.setOnAction(event -> perform("Search"));

        Button delete = new Button("Delete First Match");
        delete.setOnAction(event -> perform("Delete First Match"));

        Button reset = new Button("Reset List");
        reset.setOnAction(event -> {
            if (list.isEmpty() && historyArea.getText().isEmpty() || Ui.confirmReset(this, "list")) resetList();
        });

        javafx.scene.layout.FlowPane controls =
            new javafx.scene.layout.FlowPane(10, 10);

        controls.getChildren().addAll(
            insertFirst, insertLast, search, delete, reset
        );

        status.setWrapText(true);
        state.setWrapText(true);
        metrics.setWrapText(true);


        nodeDisplay.setAlignment(Pos.CENTER_LEFT);
        nodeDisplay.setPadding(new Insets(15));


        ScrollPane horizontalScroll = new ScrollPane(nodeDisplay);
        horizontalScroll.setFitToHeight(true);
        horizontalScroll.setPrefViewportHeight(120);
        horizontalScroll.setMinHeight(150);
        horizontalScroll.setVbarPolicy(
            ScrollPane.ScrollBarPolicy.NEVER
        );

        stepsArea.setEditable(false);
        stepsArea.setWrapText(true);
        stepsArea.setPrefRowCount(7);

        historyArea.setEditable(false);
        historyArea.setWrapText(true);
        historyArea.setPrefRowCount(5);


        Ui.primary(insertFirst); Ui.danger(reset);
        insertFirst.setId("insert-first"); insertLast.setId("insert-last");
        search.setId("search"); delete.setId("delete"); reset.setId("reset");
        state.getStyleClass().add("state-badge");
        nodeDisplay.getStyleClass().add("visual-canvas");
        horizontalScroll.getStyleClass().add("node-scroll");
        VBox content = new VBox(18,
            Ui.heading("DATA STRUCTURES / LINKED LIST", "Linked List Explorer", "Follow the references. See how nodes connect, change and disappear."),
            Ui.card("Try an operation", Ui.field("Value to insert, search or delete", inputField), controls, status),
            Ui.card("Your linked nodes", state, horizontalScroll,
                Ui.label("HEAD points to the first node. The last node points to null. Scroll sideways for longer lists.", "legend")),
            Ui.card("Latest operation", stats, stepsArea),
            Ui.details("Operation history", historyArea),
            Ui.details("Complexity & measurement notes", metrics,
                text("Insert First: O(1). Insert Last: O(n), with no tail reference. Search/Delete: best O(1), worst O(n). Nodes and display snapshot each use O(n) space."),
                text("Count = nodes visited + structural link writes (head or next). Local pointer movements, size updates, boundary checks and default node initialization are excluded."),
                text("Time includes the operation and explanation recording; excludes parsing, snapshot creation and rendering. It differs from stack/queue timing."),
                text("Duplicates are allowed. Search/Delete use the first match. Limit: 50 nodes for visualization. Switching labs preserves the list; reset or closing the app clears it."))
        );

        getStyleClass().add("lab-page");
        inputField.setId("input-field");
        status.setId("status");
        Ui.status(status, "neutral");
        title.getStyleClass().add("page-title");

        content.getStyleClass().add("page-content");
        ScrollPane scroll = Ui.scroll(content);
        getChildren().add(scroll);


        resetList();
    }

    private Label text(String value) {
        Label label = new Label(value);
        label.setWrapText(true);
        return label;
    }

    private void perform(String action) {
        final int value;

        try {
            value = Integer.parseInt(inputField.getText().trim());
        } catch (NumberFormatException exception) {
            Ui.status(status, "error");
            status.setText(
                "Enter a whole number between -2147483648 and 2147483647."
            );

            stepsArea.setText(
                "Input validation failed. No list operation was attempted."
            );
            stats.clear();
        metrics.setText("No operation measured.");
            return;
        }

        CustomLinkedList.Operation operation;

        switch (action) {
            case "Insert First":
                operation = list.insertFirst(value);
                break;

            case "Insert Last":
                operation = list.insertLast(value);
                break;

            case "Search":
                operation = list.search(value);
                break;

            case "Delete First Match":
                operation = list.deleteFirstMatch(value);
                break;

            default:
                throw new IllegalArgumentException("Unknown operation.");
        }

        attemptNumber++;
        stats.set(Integer.toString(operation.getCountedOperations()), Ui.time(operation.elapsedNanoseconds),
            Integer.toString(list.size()), Integer.toString(attemptNumber));

        Ui.status(status, operation.successful ? "success" : "error");
        status.setText(operation.message);

        stepsArea.setText(operation.steps);
        stepsArea.positionCaret(0);

        metrics.setText(
            "Nodes visited: " + operation.nodesVisited +
            " | Structural link writes: " + operation.linkWrites +
            "\nCounted operations: " + operation.getCountedOperations() +
            " | Recorded execution time: " +
            operation.elapsedNanoseconds + " ns"
        );

        historyArea.appendText(
            attemptNumber + ". " + action + " " + value +
            " -> " + operation.message + "\n"
        );

        drawList();
        inputField.requestFocus();
        inputField.selectAll();
    }

    private void drawList() {
        nodeDisplay.getChildren().clear();

        state.setText(
            "Size: " + list.size() + "/50" +
            " | Empty: " + list.isEmpty()
        );

        nodeDisplay.getChildren().add(new Label("HEAD →"));

        int[] values = list.snapshot();

        for (int index = 0; index < values.length; index++) {
            Label node = new Label(
                values[index] + "  |  next"
            );
            node.setAlignment(Pos.CENTER);
            node.setMinSize(110, 50);
            node.setPadding(new Insets(10));
            node.getStyleClass().addAll("value-box", index == 0 ? "active-value" : "ordinary");

            VBox item = new VBox(
                5, node, new Label("index " + index)
            );
            item.setAlignment(Pos.CENTER);

            nodeDisplay.getChildren().addAll(
                item, new Label("→")
            );
        }

        nodeDisplay.getChildren().add(new Label("null"));
    }

    private void resetList() {
        list = new CustomLinkedList();
        attemptNumber = 0;

        inputField.clear();
        Ui.status(status, "neutral");
        status.setText("List ready. Enter a number and choose an operation.");
        stepsArea.setText("Operation steps will appear here.");
        stats.clear();
        metrics.setText("No operation measured.");
        historyArea.clear();

        drawList();
    }
}