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

    public LinkedListView(Runnable onBack) {
        setPadding(new Insets(20));
        setSpacing(12);
        setStyle("-fx-background-color: #f1f5f9;");

        Button back = new Button("Back to Home");
        back.setOnAction(event -> onBack.run());

        Label title = new Label("Singly Linked List Explorer");
        title.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;"
        );

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
        reset.setOnAction(event -> resetList());

        javafx.scene.layout.FlowPane controls =
            new javafx.scene.layout.FlowPane(10, 10);

        controls.getChildren().addAll(
            insertFirst, insertLast, search, delete, reset
        );

        status.setWrapText(true);
        state.setWrapText(true);
        metrics.setWrapText(true);
        state.setStyle("-fx-font-weight: bold;");

        nodeDisplay.setAlignment(Pos.CENTER_LEFT);
        nodeDisplay.setPadding(new Insets(15));
        nodeDisplay.setStyle("-fx-background-color: #e2e8f0;");

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

        VBox content = new VBox(
            14,
            text(
                "Each node stores a value and a next reference. " +
                "Head points to the first node; the last node points to null."
            ),
            inputField,
            controls,
            status,
            state,
            horizontalScroll,
            text(
                "Scroll horizontally to see longer lists. Indexes show " +
                "traversal positions, not memory addresses."
            ),
            text("Latest operation steps"),
            stepsArea,
            metrics,
            text("Operation history"),
            historyArea,
            text(
                "Complexity for this implementation:\n" +
                "Insert First: O(1).\n" +
                "Insert Last: O(n), because we traverse from head.\n" +
                "Search and Delete First Match: best O(1), worst O(n).\n" +
                "List storage: O(n). Display snapshot: O(n) extra space."
            ),
            text(
                "Counting model: nodes visited + structural link writes. " +
                "A structural link write changes head or a node's next. " +
                "Local pointer movements, size updates, boundary checks " +
                "and default node initialization are excluded."
            ),
            text(
                "Timing includes the operation and explanation recording. " +
                "It excludes input parsing, the display snapshot and rendering. " +
                "Do not compare this directly with the stack/queue timings, " +
                "which exclude explanation recording."
            ),
            text(
                "Duplicates are allowed. Search returns the first match. " +
                "Delete removes only the first match.\n" +
                "The 50-node limit is a visualization choice, not an " +
                "inherent linked-list limit.\n" +
                "Resetting or reopening this screen starts an empty list."
            )
        );

        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setMinHeight(0);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(back, title, scroll);

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
            status.setStyle("-fx-text-fill: #b91c1c;");
            status.setText(
                "Enter a whole number between -2147483648 and 2147483647."
            );

            stepsArea.setText(
                "Input validation failed. No list operation was attempted."
            );
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

        status.setStyle(
            operation.successful
                ? "-fx-text-fill: #15803d;"
                : "-fx-text-fill: #b91c1c;"
        );
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
            node.setStyle(
                "-fx-background-color: " +
                (index == 0 ? "#c2410c" : "#1d4ed8") + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;"
            );

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
        status.setStyle("-fx-text-fill: #475569;");
        status.setText("List ready. Enter a number and choose an operation.");
        stepsArea.setText("Operation steps will appear here.");
        metrics.setText("No operation measured.");
        historyArea.clear();

        drawList();
    }
}