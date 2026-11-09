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

public class QueueView extends VBox {

    private static final int CAPACITY = 5;

    private CustomQueue queue = new CustomQueue(CAPACITY);

    private final TextField inputField = new TextField();
    private final Label status = new Label();
    private final Label state = new Label();
    private final Label logicalOrder = new Label();
    private final Label metrics = new Label();

    private final FlowPane arrayDisplay = new FlowPane(10, 10);
    private final TextArea stepsArea = new TextArea();
    private final TextArea historyArea = new TextArea();

    private int attemptNumber;

    public QueueView(Runnable onBack) {
        setSpacing(12);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f1f5f9;");

        Button back = new Button("Back to Home");
        back.setOnAction(event -> onBack.run());

        Label title = new Label("Circular Queue Explorer");
        title.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;"
        );

        inputField.setPromptText("Enter a whole number to enqueue");
        inputField.setOnAction(event -> enqueueValue());

        Button enqueue = new Button("Enqueue");
        enqueue.setOnAction(event -> enqueueValue());

        Button dequeue = new Button("Dequeue");
        dequeue.setOnAction(event ->
            displayOperation("Dequeue", queue.dequeue())
        );

        Button front = new Button("Front");
        front.setOnAction(event ->
            displayOperation("Front", queue.front())
        );

        Button rear = new Button("Rear");
        rear.setOnAction(event ->
            displayOperation("Rear", queue.rear())
        );

        Button reset = new Button("Reset Queue");
        reset.setOnAction(event -> resetQueue());

        FlowPane controls = new FlowPane(10, 10);
        controls.getChildren().addAll(
            enqueue, dequeue, front, rear, reset
        );

        status.setWrapText(true);
        state.setWrapText(true);
        logicalOrder.setWrapText(true);
        metrics.setWrapText(true);

        state.setStyle("-fx-font-weight: bold;");
        logicalOrder.setStyle(
            "-fx-font-weight: bold; -fx-font-size: 16px;"
        );

        arrayDisplay.setPadding(new Insets(15));
        arrayDisplay.setStyle("-fx-background-color: #e2e8f0;");

        stepsArea.setEditable(false);
        stepsArea.setWrapText(true);
        stepsArea.setPrefRowCount(6);

        historyArea.setEditable(false);
        historyArea.setWrapText(true);
        historyArea.setPrefRowCount(5);

        VBox content = new VBox(
            14,
            text(
                "First In, First Out: enqueue at the rear, dequeue at " +
                "the front. Array capacity: 5."
            ),
            inputField,
            controls,
            status,
            state,
            text("Physical array slots"),
            arrayDisplay,
            text(
                "Orange: front item | Blue: other active items | " +
                "Grey: inactive slot.\n" +
                "NEXT WRITE identifies the enqueue position. " +
                "When full, enqueue is blocked even though this index exists."
            ),
            logicalOrder,
            text("Latest operation steps"),
            stepsArea,
            metrics,
            text("Operation history"),
            historyArea,
            text(
                "Core operations: Enqueue, Dequeue, Front and Rear " +
                "are O(1). Storage is O(capacity).\n" +
                "Drawing the physical array takes O(capacity), while " +
                "displaying logical queue order takes O(n)."
            ),
            text(
                "Counting model: boundary checks + array reads + array " +
                "writes + stored-index updates + size updates. " +
                "Arithmetic, local assignments and display work are excluded."
            ),
            text(
                "Time measures the boundary check and core queue action. " +
                "Input parsing, explanation creation and drawing are excluded. " +
                "Small timings vary and are not reliable benchmarks."
            ),
            text(
                "Empty and full queues can both have front == nextInsert. " +
                "The size field distinguishes them.\n" +
                "Resetting or reopening this screen starts a new empty queue."
            )
        );

        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setMinHeight(0);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(back, title, scroll);

        resetQueue();
    }

    private Label text(String value) {
        Label label = new Label(value);
        label.setWrapText(true);
        return label;
    }

    private void enqueueValue() {
        final int value;

        try {
            value = Integer.parseInt(inputField.getText().trim());
        } catch (NumberFormatException exception) {
            status.setStyle("-fx-text-fill: #b91c1c;");
            status.setText(
                "Enter a whole number between -2147483648 and 2147483647."
            );

            stepsArea.setText(
                "Input validation failed. No queue operation was attempted."
            );
            metrics.setText("No operation measured.");
            return;
        }

        CustomQueue.Operation operation = queue.enqueue(value);
        displayOperation("Enqueue " + value, operation);

        if (operation.successful) {
            inputField.clear();
        }

        inputField.requestFocus();
    }

    private void displayOperation(
            String action,
            CustomQueue.Operation operation) {

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
            " | Reads: " + operation.reads +
            " | Writes: " + operation.writes +
            "\nIndex updates: " + operation.indexUpdates +
            " | Size updates: " + operation.sizeUpdates +
            " | Counted operations: " + operation.getCountedOperations() +
            "\nExecution time: " + operation.elapsedNanoseconds + " ns"
        );

        historyArea.appendText(
            attemptNumber + ". " + action + " -> " +
            operation.message + "\n"
        );

        drawQueue();
    }

    private void drawQueue() {
        arrayDisplay.getChildren().clear();

        String rearIndex = queue.isEmpty()
            ? "none"
            : Integer.toString(queue.getRearIndex());

        state.setText(
            "Size: " + queue.size() + "/" + queue.capacity() +
            " | Front index: " + queue.getFrontIndex() +
            " | Rear item index: " + rearIndex +
            "\nNext insertion index: " + queue.getNextInsertIndex() +
            " | Empty: " + queue.isEmpty() +
            " | Full: " + queue.isFull()
        );

        for (int index = 0; index < queue.capacity(); index++) {
            boolean active = queue.isActiveIndex(index);

            String colour = "#64748b";

            if (active) {
                colour = index == queue.getFrontIndex()
                    ? "#c2410c"
                    : "#1d4ed8";
            }

            Label value = new Label(
                active ? Integer.toString(queue.valueAt(index)) : "—"
            );
            value.setAlignment(Pos.CENTER);
            value.setMinSize(90, 50);
            value.setPadding(new Insets(10));
            value.setStyle(
                "-fx-background-color: " + colour + ";" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 18px;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 8;"
            );

            String markers = "";

            if (active && index == queue.getFrontIndex()) {
                markers += "FRONT\n";
            }

            if (active && index == queue.getRearIndex()) {
                markers += "REAR\n";
            }

            if (index == queue.getNextInsertIndex()) {
                markers += queue.isFull()
                    ? "NEXT WRITE\n(blocked)"
                    : "NEXT WRITE";
            }

            VBox slot = new VBox(
                5,
                value,
                new Label("index " + index),
                new Label(markers)
            );
            slot.setAlignment(Pos.TOP_CENTER);

            arrayDisplay.getChildren().add(slot);
        }

        if (queue.isEmpty()) {
            logicalOrder.setText("Queue order: empty");
            return;
        }

        StringBuilder order = new StringBuilder("FRONT → ");

        for (int position = 0; position < queue.size(); position++) {
            if (position > 0) {
                order.append(" → ");
            }

            order.append(queue.valueAtPosition(position));
        }

        order.append(" ← REAR");
        logicalOrder.setText(order.toString());
    }

    private void resetQueue() {
        queue = new CustomQueue(CAPACITY);
        attemptNumber = 0;

        inputField.clear();
        status.setStyle("-fx-text-fill: #475569;");
        status.setText("Queue ready. Enter a number and click Enqueue.");
        metrics.setText("No operation measured.");
        stepsArea.setText("Operation steps will appear here.");
        historyArea.clear();

        drawQueue();
    }
}