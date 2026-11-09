package com.algorithmexplorer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SortingView extends VBox {

    private final ComboBox<String> algorithmChoice = new ComboBox<>();
    private final TextField inputField = new TextField();

    private final Label status = new Label();
    private final Label output = new Label();
    private final Label totals = new Label();
    private final Label stepPosition = new Label();
    private final Label description = new Label();
    private final Label stepMetrics = new Label();
    private final Label keyLabel = new Label();
    private final Label speedLabel = new Label();
    private final Label complexity = new Label();

    private final FlowPane boxes = new FlowPane(10, 10);

    private final Button runButton = new Button();
    private final Button playButton = new Button("Play");
    private final Button pauseButton = new Button("Pause");
    private final Button previousButton = new Button("Previous");
    private final Button nextButton = new Button("Next");
    private final Button restartButton = new Button("Restart Steps");
    private final Button lastButton = new Button("Last Step");

    private final Slider speedSlider = new Slider(0.25, 4, 1);
    private final Timeline playback;

    private SortTrace currentTrace;
    private int currentStep;
    private boolean playing;

    public SortingView(Runnable onBack) {
        setSpacing(12);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f1f5f9;");

        playback = new Timeline(
            new KeyFrame(Duration.seconds(1), event -> advance())
        );
        playback.setCycleCount(Timeline.INDEFINITE);

        Button back = new Button("Back to Home");
        back.setOnAction(event -> {
            stopPlayback();
            onBack.run();
        });

        sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene == null) {
                stopPlayback();
            }
        });

        Label title = new Label("Sorting Lab");
        title.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;"
        );

        algorithmChoice.getItems().addAll(
            "Bubble Sort",
            "Selection Sort",
            "Insertion Sort",
            "Merge Sort"
        );
        algorithmChoice.setValue("Bubble Sort");
        algorithmChoice.setMaxWidth(Double.MAX_VALUE);

        inputField.setPromptText("Example: 8, 3, 5, 1, 9");
        inputField.setOnAction(event -> runSort());

        runButton.setStyle(
            "-fx-background-color: #1d4ed8;" +
            "-fx-text-fill: white; -fx-padding: 10 18;"
        );
        runButton.setOnAction(event -> runSort());

        Button example = new Button("Load Example");
        example.setOnAction(event -> {
            inputField.setText("8, 3, 5, 1, 9");
            resetResults();
        });

        Button clear = new Button("Clear");
        clear.setOnAction(event -> {
            inputField.clear();
            resetResults();
            inputField.requestFocus();
        });

        FlowPane inputControls = new FlowPane(10, 10);
        inputControls.getChildren().addAll(runButton, example, clear);

        Label[] wrappingLabels = {
            status, output, totals, stepPosition, description,
            stepMetrics, keyLabel, speedLabel, complexity
        };

        for (Label label : wrappingLabels) {
            label.setWrapText(true);
        }

        output.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        stepPosition.setStyle("-fx-font-weight: bold;");
        keyLabel.setStyle(
            "-fx-font-weight: bold; -fx-text-fill: #7e22ce;"
        );
        boxes.setPadding(new Insets(10, 0, 10, 0));

        playButton.setOnAction(event -> {
            if (!playing && hasNext()) {
                playing = true;
                playback.setRate(speedSlider.getValue());
                playback.playFromStart();
                updateControls();
            }
        });

        pauseButton.setOnAction(event -> stopPlayback());

        previousButton.setOnAction(event -> {
            stopPlayback();

            if (currentTrace != null && currentStep > 0) {
                currentStep--;
                showStep();
            }
        });

        nextButton.setOnAction(event -> {
            stopPlayback();

            if (hasNext()) {
                currentStep++;
                showStep();
            }
        });

        restartButton.setOnAction(event -> {
            stopPlayback();

            if (currentTrace != null) {
                currentStep = 0;
                showStep();
            }
        });

        lastButton.setOnAction(event -> {
            stopPlayback();

            if (currentTrace != null) {
                currentStep = currentTrace.getStepCount() - 1;
                showStep();
            }
        });

        FlowPane controls = new FlowPane(10, 10);
        controls.getChildren().addAll(
            playButton, pauseButton, previousButton,
            nextButton, restartButton, lastButton
        );

        speedSlider.setShowTickLabels(true);
        speedSlider.setShowTickMarks(true);
        speedSlider.setMajorTickUnit(0.75);
        speedSlider.setMinorTickCount(2);
        speedSlider.setBlockIncrement(0.25);
        speedSlider.setMaxWidth(400);

        speedSlider.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
                playback.setRate(newValue.doubleValue());
                updateSpeed();
            }
        );
        updateSpeed();

        VBox content = new VBox(
            14,
            text("Choose an algorithm and enter 1–50 comma-separated integers."),
            algorithmChoice,
            inputField,
            inputControls,
            status,
            output,
            totals,
            stepPosition,
            text(
                "Boxes show the MAIN ARRAY after each action.\n" +
                "Blue: ordinary position | Orange: active position or " +
                "range endpoint | Green: completed sort."
            ),
            boxes,
            keyLabel,
            description,
            stepMetrics,
            controls,
            speedLabel,
            speedSlider,
            complexity,
            text(
                "Counting model: value comparisons + array writes. " +
                "Writes include Merge Sort's temporary array. Swaps are " +
                "shown separately and already contribute two writes. " +
                "Loop checks, index updates, key assignments and trace " +
                "copying are excluded."
            ),
            text(
                "Time includes the sorting routine, counters and internal " +
                "step recording. It excludes initial setup, the final " +
                "snapshot and playback. These are instrumented timings, " +
                "not pure algorithm benchmarks."
            ),
            text(
                "Trace storage is separate from algorithm space: " +
                "Bubble, Selection and Insertion traces use O(n³) " +
                "worst-case snapshot storage; Merge Sort uses " +
                "O(n² log n) snapshot storage. The trace array itself " +
                "reserves O(n²) reference slots."
            )
        );

        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color: white;");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setFitToWidth(true);
        scroll.setMinHeight(0);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        getChildren().addAll(back, title, scroll);

        inputField.textProperty().addListener(
            (observable, oldValue, newValue) -> resetResults()
        );

        algorithmChoice.valueProperty().addListener(
            (observable, oldValue, newValue) -> {
                resetResults();
                updateAlgorithmDetails();
            }
        );

        updateAlgorithmDetails();
        resetResults();
    }

    private Label text(String value) {
        Label label = new Label(value);
        label.setWrapText(true);
        return label;
    }

    private void updateAlgorithmDetails() {
        String algorithm = algorithmChoice.getValue();
        runButton.setText("Run " + algorithm);

        switch (algorithm) {
            case "Selection Sort":
                complexity.setText(
                    "Selection Sort — best, average and worst: O(n²).\n" +
                    "Find the smallest remaining value and place it next.\n" +
                    "Algorithm auxiliary space: O(1)."
                );
                break;

            case "Insertion Sort":
                complexity.setText(
                    "Insertion Sort — best: O(n); average and worst: O(n²).\n" +
                    "Hold a key, shift larger values right, then insert it.\n" +
                    "Algorithm auxiliary space: O(1)."
                );
                break;

            case "Merge Sort":
                complexity.setText(
                    "Merge Sort — best, average and worst: O(n log n).\n" +
                    "Recursively split the array, then merge sorted halves.\n" +
                    "Algorithm auxiliary space: O(n) for the temporary " +
                    "array, plus O(log n) recursion depth."
                );
                break;

            default:
                complexity.setText(
                    "Bubble Sort — best: O(n); average and worst: O(n²).\n" +
                    "Swap out-of-order neighbours; stop after a pass " +
                    "with no swaps.\nAlgorithm auxiliary space: O(1)."
                );
        }
    }

    private void runSort() {
        resetResults();

        try {
            int[] numbers = parseInput(inputField.getText());

            currentTrace = SortingAlgorithms.run(
                algorithmChoice.getValue(), numbers
            );

            SortTrace.Step finalStep = currentTrace.getStep(
                currentTrace.getStepCount() - 1
            );

            output.setText(
                algorithmChoice.getValue() + " result: " +
                SortTrace.format(finalStep.values)
            );

            totals.setText(
                "FULL RUN — " +
                statistics(
                    currentTrace.getComparisons(),
                    currentTrace.getSwaps(),
                    currentTrace.getWrites()
                ) +
                "\nRecorded execution time: " +
                currentTrace.getElapsedNanoseconds() + " ns"
            );

            showStep();

        } catch (IllegalArgumentException exception) {
            status.setStyle("-fx-text-fill: #b91c1c;");
            status.setText(exception.getMessage());
        }
    }

    private String statistics(long comparisons, long swaps, long writes) {
        return "Comparisons: " + comparisons +
            " | Swaps: " + swaps +
            " | Array writes: " + writes +
            "\nCounted operations: " + (comparisons + writes);
    }

    private boolean hasNext() {
        return currentTrace != null &&
            currentStep < currentTrace.getStepCount() - 1;
    }

    private void stopPlayback() {
        playback.stop();
        playing = false;
        updateControls();
    }

    private void advance() {
        if (hasNext()) {
            currentStep++;
            showStep();
        }

        if (!hasNext()) {
            stopPlayback();
        }
    }

    private void updateSpeed() {
        speedLabel.setText(
            String.format(
                "Playback speed: %.2fx — approximately %.2f seconds per step",
                speedSlider.getValue(), 1.0 / speedSlider.getValue()
            )
        );
    }

    private void updateControls() {
        boolean loaded = currentTrace != null;
        boolean atStart = !loaded || currentStep == 0;
        boolean atEnd = !hasNext();

        playButton.setDisable(!loaded || playing || atEnd);
        pauseButton.setDisable(!playing);
        previousButton.setDisable(atStart);
        nextButton.setDisable(atEnd);
        restartButton.setDisable(atStart && !playing);
        lastButton.setDisable(atEnd);

        if (loaded) {
            status.setStyle("-fx-text-fill: #15803d;");
            status.setText(
                atEnd ? "Playback complete. Restart to watch again."
                : playing ? "Playing recorded steps..."
                : "Press Play or inspect individual steps."
            );
        }
    }

    private void showStep() {
        SortTrace.Step step = currentTrace.getStep(currentStep);

        stepPosition.setText(
            "Step " + (currentStep + 1) +
            " of " + currentTrace.getStepCount()
        );

        description.setText(step.explanation);
        stepMetrics.setText(
            "AT THIS STEP — " +
            statistics(step.comparisons, step.swaps, step.writes)
        );

        keyLabel.setText(
            step.heldKey == null
                ? "Held insertion key: —"
                : "Held insertion key: " + step.heldKey +
                  " (stored separately while values shift)"
        );

        boxes.getChildren().clear();

        for (int i = 0; i < step.values.length; i++) {
            String colour = "#1d4ed8";

            if (!hasNext()) {
                colour = "#15803d";
            } else if (i == step.firstIndex || i == step.secondIndex) {
                colour = "#c2410c";
            }

            Label value = new Label(Integer.toString(step.values[i]));
            value.setAlignment(Pos.CENTER);
            value.setMinSize(65, 50);
            value.setPadding(new Insets(10));
            value.setStyle(
                "-fx-background-color: " + colour + ";" +
                "-fx-text-fill: white; -fx-font-size: 18px;" +
                "-fx-font-weight: bold; -fx-background-radius: 8;"
            );

            VBox item = new VBox(5, value, new Label("index " + i));
            item.setAlignment(Pos.CENTER);
            boxes.getChildren().add(item);
        }

        updateControls();
    }

    private void resetResults() {
        stopPlayback();
        currentTrace = null;
        currentStep = 0;

        status.setStyle("-fx-text-fill: #475569;");
        status.setText("Choose an algorithm, enter numbers and run.");

        output.setText("Final result: —");
        totals.setText("Full-run statistics: —");
        stepPosition.setText("No steps loaded");
        description.setText("Recorded steps will appear here.");
        stepMetrics.setText("Statistics at this step: —");
        keyLabel.setText("Held insertion key: —");
        boxes.getChildren().clear();

        updateControls();
    }

    private int[] parseInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Please enter at least one whole number."
            );
        }

        String[] parts = input.split(",", -1);

        if (parts.length > 50) {
            throw new IllegalArgumentException(
                "Please enter no more than 50 numbers."
            );
        }

        int[] numbers = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            try {
                numbers[i] = Integer.parseInt(parts[i].trim());
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException(
                    "Item " + (i + 1) +
                    " must be a whole number between " +
                    "-2147483648 and 2147483647."
                );
            }
        }

        return numbers;
    }
}