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
    private final Ui.Stats stats = new Ui.Stats("COUNTED OPS", "RUN TIME", "ITEMS", "ATTEMPT");

    public StackView(Runnable onBack) {
        setSpacing(0);
        setPadding(new Insets(0));


        Button back = new Button("Back to Home");
        back.setOnAction(event -> onBack.run());

        Label title = new Label("Stack Explorer");


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
        reset.setOnAction(event -> {
            if (stack.isEmpty() && historyArea.getText().isEmpty() || Ui.confirmReset(this, "stack")) resetStack();
        });

        FlowPane controls = new FlowPane(10, 10);
        controls.getChildren().addAll(push, pop, peek, reset);

        status.setWrapText(true);
        state.setWrapText(true);
        metrics.setWrapText(true);


        stackDisplay.setAlignment(Pos.TOP_CENTER);
        stackDisplay.setPadding(new Insets(15));


        stepsArea.setEditable(false);
        stepsArea.setWrapText(true);
        stepsArea.setPrefRowCount(5);

        historyArea.setEditable(false);
        historyArea.setWrapText(true);
        historyArea.setPrefRowCount(5);


        Ui.primary(push); Ui.danger(reset);
        push.setId("push"); pop.setId("pop"); peek.setId("peek"); reset.setId("reset");
        state.getStyleClass().add("state-badge");
        stackDisplay.getStyleClass().add("visual-canvas");
        stackDisplay.setMaxWidth(520);
        stackDisplay.setMinHeight(100);
        stepsArea.setAccessibleText("Latest stack operation steps");
        VBox content = new VBox(18,
            Ui.heading("DATA STRUCTURES / STACK", "Stack Explorer", "Last in, first out. Add to the top, remove from the top."),
            Ui.card("Try an operation", Ui.field("Value to push", inputField), controls, status),
            Ui.card("Your stack", state, stackDisplay,
                Ui.label("Amber marks TOP. Empty/full operations remain available so you can explore boundary cases.", "legend")),
            Ui.card("Latest operation", stats, stepsArea),
            Ui.details("Operation history", historyArea),
            Ui.details("Complexity & measurement notes", metrics,
                text("Push, Pop and Peek: O(1). Storage: O(capacity). Rendering is separate from operation time."),
                text("Count = boundary checks + array reads + array writes + top-index updates. Local assignments, return handling and display work are excluded."),
                text("Time includes the boundary check and core action; excludes parsing, explanation creation and drawing. Tiny timings vary."),
                text("Your stack is kept when switching labs. Reset clears the stack and history; closing the application ends the session."))
        );

        getStyleClass().add("lab-page");
        inputField.setId("input-field");
        status.setId("status");
        Ui.status(status, "neutral");
        title.getStyleClass().add("page-title");

        content.getStyleClass().add("page-content");
        ScrollPane scroll = Ui.scroll(content);
        getChildren().add(scroll);


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
            Ui.status(status, "error");
            status.setText(
                "Enter a whole number between -2147483648 and 2147483647."
            );

            stepsArea.setText(
                "Input validation failed. No stack operation was attempted."
            );
            stats.clear();
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
        stats.set(Integer.toString(operation.getCountedOperations()), Ui.time(operation.elapsedNanoseconds),
            Integer.toString(stack.size()), Integer.toString(attemptNumber));

        Ui.status(status, operation.successful ? "success" : "error");

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

            item.getStyleClass().addAll("value-box", isTop ? "active-value" : "ordinary");

            stackDisplay.getChildren().add(item);
        }
    }

    private void resetStack() {
        stack = new CustomStack(CAPACITY);
        attemptNumber = 0;

        inputField.clear();
        Ui.status(status, "neutral");
        status.setText("Stack ready. Enter a number and click Push.");
        stats.clear();
        metrics.setText("No operation measured.");
        stepsArea.setText("Operation steps will appear here.");
        historyArea.clear();

        drawStack();
    }
}