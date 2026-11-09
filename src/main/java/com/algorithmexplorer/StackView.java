package com.algorithmexplorer;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class StackView extends VBox {

    private static final int CAPACITY = 5;

    private CustomStack stack = new CustomStack(CAPACITY);

    private final TextField inputField = new TextField();
    private final Label status = new Label();
    private final Label state = new Label();
    private final Label metrics = new Label();

    private final VBox stackDisplay = new VBox(8);
    private final TextArea stepsArea = new TextArea();
    private final TextArea historyArea = new TextArea();

    private int attemptNumber;

    public StackView(Runnable onBack) {
        setSpacing(12);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f1f5f9;");

        Button back = new Button("Back to Home");
        back.setOnAction(event -> onBack.run());

        Label title = new Label("Stack Explorer");
        title.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;"
        );

        Label introduction = text(
            "Last In, First Out: add and remove items at the top.\n" +
            "This custom stack uses an integer array with capacity 5."
        );

        inputField.setPromptText("Enter a whole number to push");
        inputField.setOnAction(event -> pushValue());

        Button push = new Button("Push");
        push.setOnAction(event -> pushValue());

        Button pop = new Button("Pop");
        pop.setOnAction(event -> displayOperation("Pop", stack.pop()));

        Button peek = new Button("Peek");
        peek.setOnAction(event -> displayOperation("Peek", stack.peek()));

        Button reset = new Button("Reset Stack");
        reset.setOnAction(event -> resetStack());

        FlowPane controls = new FlowPane(10, 10);
        controls.getChildren().addAll(push, pop, peek, reset);

        status.setWrapText(true);
        state.setWrapText(true);
        metrics.setWrapText(true);

        state.setStyle("-fx-font-weight: bold;");

        stackDisplay.setAlignment(Pos.TOP_CENTER);
        stackDisplay.setPadding(new Insets(15));
        stackDisplay.setStyle(
            "-fx-background-color: #e2e8f0;" +
            "-fx-background-radius: 10;"
        );

        stepsArea.setEditable(false);
        stepsArea.setWrapText(true);
        stepsArea.setPrefRowCount(5);

        historyArea.setEditable(false);
        historyArea.setWrapText(true);
        historyArea.setPrefRowCount(5);

        VBox content = new VBox(
            14,
            introduction,
            inputField,
            controls,
            status,
            state,
            stackDisplay,
            text("Latest operation steps"),
            stepsArea,
            metrics,
            text("Operation history"),
            historyArea,
            text(
                "Complexity: Push O(1), Pop O(1), Peek O(1).\n" +
                "Storage: O(capacity). Rendering the active stack takes " +
                "O(n) and is separate from the stack operation."
            ),
            text(
                "Counting model: boundary checks + array reads + " +
                "array writes + top-index updates. Local assignments, " +
                "return handling and display work are excluded."
            ),
            text(
                "Time measures the boundary check and core stack action. " +
                "Input parsing, explanation creation and drawing are excluded. " +
                "These tiny timings vary and are not reliable benchmarks."
            ),
            text(
                "Leaving this screen and reopening it starts a new empty " +
                "stack. Reset Stack also clears the stack and history."
            )
        );

        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setMinHeight(0);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(back, title, scroll);

        resetStack();
    }

    private Label text(String value) {
        Label label = new Label(value);
        label.setWrapText(true);
        return label;
    }

    private void pushValue() {
        final int value;

        try {
            value = Integer.parseInt(inputField.getText().trim());
        } catch (NumberFormatException exception) {
            status.setStyle("-fx-text-fill: #b91c1c;");
            status.setText(
                "Enter a whole number between -2147483648 and 2147483647."
            );

            stepsArea.setText(
                "Input validation failed. No stack operation was attempted."
            );
            metrics.setText("No operation measured.");
            return;
        }

        CustomStack.Operation operation = stack.push(value);
        displayOperation("Push " + value, operation);

        if (operation.successful) {
            inputField.clear();
        }

        inputField.requestFocus();
    }

    private void displayOperation(
            String action,
            CustomStack.Operation operation) {

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
            "Checks: " + operation.checks +
            " | Array reads: " + operation.reads +
            " | Array writes: " + operation.writes +
            " | Top updates: " + operation.indexUpdates +
            "\nCounted operations: " + operation.getCountedOperations() +
            " | Execution time: " + operation.elapsedNanoseconds + " ns"
        );

        historyArea.appendText(
            attemptNumber + ". " + action + " -> " +
            operation.message + "\n"
        );

        drawStack();
    }

    private void drawStack() {
        stackDisplay.getChildren().clear();

        state.setText(
            "Size: " + stack.size() + "/" + stack.capacity() +
            " | Top index: " + stack.getTopIndex() +
            " | Empty: " + stack.isEmpty() +
            " | Full: " + stack.isFull()
        );

        if (stack.isEmpty()) {
            stackDisplay.getChildren().add(
                text("Empty stack — top = -1")
            );
            return;
        }

        // Draw from top to bottom, without changing the stack.
        for (int index = stack.getTopIndex(); index >= 0; index--) {
            boolean isTop = index == stack.getTopIndex();

            Label item = new Label(
                stack.valueAt(index) +
                "   [index " + index + "]" +
                (isTop ? "   ← TOP" : "")
            );

            item.setAlignment(Pos.CENTER);
            item.setMaxWidth(Double.MAX_VALUE);
            item.setMinHeight(45);

            item.setStyle(
                "-fx-background-color: " +
                (isTop ? "#c2410c" : "#1d4ed8") + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 16px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;"
            );

            stackDisplay.getChildren().add(item);
        }
    }

    private void resetStack() {
        stack = new CustomStack(CAPACITY);
        attemptNumber = 0;

        inputField.clear();
        status.setStyle("-fx-text-fill: #475569;");
        status.setText("Stack ready. Enter a number and click Push.");
        metrics.setText("No operation measured.");
        stepsArea.setText("Operation steps will appear here.");
        historyArea.clear();

        drawStack();
    }
}