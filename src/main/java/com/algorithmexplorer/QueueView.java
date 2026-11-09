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
    private final Ui.Stats stats = new Ui.Stats("COUNTED OPS", "RUN TIME", "ITEMS", "ATTEMPT");

    public QueueView(Runnable onBack) {
        setSpacing(0);
        setPadding(new Insets(0));


        Button back = new Button("Back to Home");
        back.setOnAction(event -> onBack.run());

        Label title = new Label("Circular Queue Explorer");


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
        reset.setOnAction(event -> {
            if (queue.isEmpty() && historyArea.getText().isEmpty() || Ui.confirmReset(this, "queue")) resetQueue();
        });

        FlowPane controls = new FlowPane(10, 10);
        controls.getChildren().addAll(
            enqueue, dequeue, front, rear, reset
        );

        status.setWrapText(true);
        state.setWrapText(true);
        logicalOrder.setWrapText(true);
        metrics.setWrapText(true);



        arrayDisplay.setPadding(new Insets(15));


        stepsArea.setEditable(false);
        stepsArea.setWrapText(true);
        stepsArea.setPrefRowCount(6);

        historyArea.setEditable(false);
        historyArea.setWrapText(true);
        historyArea.setPrefRowCount(5);


        Ui.primary(enqueue); Ui.danger(reset);
        enqueue.setId("enqueue"); dequeue.setId("dequeue"); reset.setId("reset");
        state.getStyleClass().add("state-badge");
        logicalOrder.getStyleClass().add("logical-order");
        arrayDisplay.getStyleClass().add("visual-canvas");
        VBox content = new VBox(18,
            Ui.heading("DATA STRUCTURES / QUEUE", "Circular Queue Explorer", "First in, first out. Watch the indexes wrap around the array."),
            Ui.card("Try an operation", Ui.field("Value to enqueue", inputField), controls, status),
            Ui.card("Physical array", state, arrayDisplay,
                Ui.label("Amber · front    Blue · active    Grey · empty. NEXT WRITE is blocked when full.", "legend"), logicalOrder),
            Ui.card("Latest operation", stats, stepsArea),
            Ui.details("Operation history", historyArea),
            Ui.details("Complexity & measurement notes", metrics,
                text("Enqueue, Dequeue, Front and Rear: O(1). Storage: O(capacity). Physical rendering takes O(capacity); logical rendering takes O(n)."),
                text("Count = boundary checks + array reads + writes + stored-index updates + size updates. Arithmetic, local assignments and display work are excluded."),
                text("Time includes the boundary check and core action; excludes parsing, explanations and drawing. Size distinguishes full from empty when front equals nextInsert."),
                text("Your queue is kept when switching labs. Reset clears the queue and history; closing the application ends the session."))
        );

        getStyleClass().add("lab-page");
        inputField.setId("input-field");
        status.setId("status");
        Ui.status(status, "neutral");
        title.getStyleClass().add("page-title");

        content.getStyleClass().add("page-content");
        ScrollPane scroll = Ui.scroll(content);
        getChildren().add(scroll);


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
            Ui.status(status, "error");
            status.setText(
                "Enter a whole number between -2147483648 and 2147483647."
            );

            stepsArea.setText(
                "Input validation failed. No queue operation was attempted."
            );
            stats.clear();
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
        stats.set(Integer.toString(operation.getCountedOperations()), Ui.time(operation.elapsedNanoseconds),
            Integer.toString(queue.size()), Integer.toString(attemptNumber));

        Ui.status(status, operation.successful ? "success" : "error");

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
            value.getStyleClass().addAll("value-box", !active ? "empty-value" : index == queue.getFrontIndex() ? "active-value" : "ordinary");

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
        Ui.status(status, "neutral");
        status.setText("Queue ready. Enter a number and click Enqueue.");
        stats.clear();
        metrics.setText("No operation measured.");
        stepsArea.setText("Operation steps will appear here.");
        historyArea.clear();

        drawQueue();
    }
}