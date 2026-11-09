package com.algorithmexplorer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class RecursionView extends VBox {

    private final ComboBox<String> algorithmChoice = new ComboBox<>();

    private final TextField inputField = new TextField();
    private final TextField arrayField = new TextField();

    private final Label inputLabel = new Label();
    private final Label instructions = new Label();
    private final Label status = new Label();
    private final Label output = new Label();
    private final Label totals = new Label();
    private final Label stepPosition = new Label();
    private final Label description = new Label();
    private final Label stepMetrics = new Label();
    private final Label speedLabel = new Label();
    private final Label complexity = new Label();

    private final VBox arrayInput = new VBox(6);
    private final TextArea stackArea = new TextArea();

    private final Button runButton = new Button();
    private final Button playButton = new Button("Play");
    private final Button pauseButton = new Button("Pause");
    private final Button previousButton = new Button("Previous");
    private final Button nextButton = new Button("Next");
    private final Button restartButton = new Button("Restart Steps");
    private final Button lastButton = new Button("Last Step");

    private final Slider speedSlider = new Slider(0.25, 4, 1);
    private final Timeline playback;

    private RecursionAlgorithms.Result currentResult;
    private int currentStep;
    private boolean playing;

    public RecursionView(Runnable onBack) {
        setPadding(new Insets(20));
        setSpacing(12);
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

        Label title = new Label("Recursion Lab");
        title.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;"
        );

        algorithmChoice.getItems().addAll(
            "Factorial", "Fibonacci", "Recursive Binary Search"
        );
        algorithmChoice.setValue("Factorial");
        algorithmChoice.setMaxWidth(Double.MAX_VALUE);

        arrayField.setPromptText("Example: 1, 3, 5, 8, 9");
        arrayInput.getChildren().addAll(
            text("Sorted array — ascending order"), arrayField
        );

        inputField.setOnAction(event -> runAlgorithm());
        arrayField.setOnAction(event -> runAlgorithm());

        runButton.setStyle(
            "-fx-background-color: #1d4ed8;" +
            "-fx-text-fill: white; -fx-padding: 10 18;"
        );
        runButton.setOnAction(event -> runAlgorithm());

        Button example = new Button("Load Example");
        example.setOnAction(event -> {
            if (isBinarySearch()) {
                arrayField.setText("1, 3, 5, 8, 9");
                inputField.setText("8");
            } else {
                inputField.setText("5");
            }

            resetResults();
        });

        Button clear = new Button("Clear");
        clear.setOnAction(event -> {
            inputField.clear();
            arrayField.clear();
            resetResults();
            inputField.requestFocus();
        });

        FlowPane inputControls = new FlowPane(10, 10);
        inputControls.getChildren().addAll(
            runButton, example, clear
        );

        Label[] labels = {
            inputLabel, instructions, status, output, totals,
            stepPosition, description, stepMetrics, speedLabel, complexity
        };

        for (Label label : labels) {
            label.setWrapText(true);
        }

        output.setStyle(
            "-fx-font-size: 19px; -fx-font-weight: bold;"
        );
        stepPosition.setStyle("-fx-font-weight: bold;");
        description.setStyle("-fx-font-size: 16px;");

        stackArea.setEditable(false);
        stackArea.setWrapText(false);
        stackArea.setPrefRowCount(9);
        stackArea.setStyle("-fx-font-family: monospace;");

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

            if (currentResult != null && currentStep > 0) {
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

            if (currentResult != null) {
                currentStep = 0;
                showStep();
            }
        });

        lastButton.setOnAction(event -> {
            stopPlayback();

            if (currentResult != null) {
                currentStep = currentResult.getStepCount() - 1;
                showStep();
            }
        });

        FlowPane playbackControls = new FlowPane(10, 10);
        playbackControls.getChildren().addAll(
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
            algorithmChoice,
            instructions,
            arrayInput,
            inputLabel,
            inputField,
            inputControls,
            status,
            output,
            totals,
            stepPosition,
            description,
            text("Active call stack — most recent call at the top"),
            stackArea,
            stepMetrics,
            playbackControls,
            speedLabel,
            speedSlider,
            complexity,
            text(
                "CALL adds a frame. RETURN removes it. The displayed " +
                "stack shows the state after each recorded action."
            ),
            text(
                "Counted operations = calls + base-case checks + " +
                "multiplications + additions + value comparisons. " +
                "Argument/index arithmetic, array reads, local assignments, " +
                "returns and trace operations are excluded."
            ),
            text(
                "For binary search, equality and less-than tests each " +
                "count as one value comparison. Sortedness validation " +
                "is excluded from the displayed search counters."
            ),
            text(
                "Time includes recursion, counters and trace recording. " +
                "It excludes input validation, initial allocation/copying " +
                "and playback. It is not a pure algorithm benchmark."
            ),
            text(
                "Maximum depth counts simultaneously active calls, " +
                "including the initial invocation."
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

        arrayField.textProperty().addListener(
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

    private boolean isBinarySearch() {
        return "Recursive Binary Search".equals(
            algorithmChoice.getValue()
        );
    }

    private void updateAlgorithmDetails() {
        String algorithm = algorithmChoice.getValue();

        runButton.setText("Run " + algorithm);
        arrayInput.setVisible(isBinarySearch());
        arrayInput.setManaged(isBinarySearch());

        switch (algorithm) {
            case "Fibonacci":
                inputLabel.setText("Input n");
                inputField.setPromptText("Integer from 0 to 10");

                instructions.setText(
                    "F(0) = 0, F(1) = 1.\n" +
                    "F(n) = F(n - 1) + F(n - 2).\n" +
                    "The input limit keeps naive recursive playback manageable."
                );

                complexity.setText(
                    "Naive Fibonacci: O(2ⁿ) time upper bound and O(n) " +
                    "call-stack space. No memoization is used.\n" +
                    "If input limits were expanded, stack-snapshot storage " +
                    "has an O(n × 2ⁿ) upper bound."
                );
                break;

            case "Recursive Binary Search":
                inputLabel.setText("Target value");
                inputField.setPromptText("Whole number to find");

                instructions.setText(
                    "Enter 1–50 numbers in ascending order and a target.\n" +
                    "Search ranges include both low and high indexes. " +
                    "Indexes start at 0.\n" +
                    "Duplicates are allowed; the result is a matching " +
                    "index, not necessarily the first match."
                );

                complexity.setText(
                    "Binary search: best O(1), worst O(log n) time. " +
                    "Worst-case recursive call-stack space: O(log n).\n" +
                    "Input validation/copying takes O(n) separately. " +
                    "Recorded stack snapshots contain O((log n)²) " +
                    "frame entries in the worst case."
                );
                break;

            default:
                inputLabel.setText("Input n");
                inputField.setPromptText("Integer from 0 to 20");

                instructions.setText(
                    "n! = n × (n - 1)!; base cases: 0! = 1 and 1! = 1.\n" +
                    "21! exceeds the range of Java's long type."
                );

                complexity.setText(
                    "Factorial: O(n) time and O(n) call-stack space " +
                    "under a constant-cost arithmetic model.\n" +
                    "Stack-snapshot recording adds O(n²) trace storage."
                );
        }
    }

    private String statistics(
            long calls,
            long checks,
            long multiplications,
            long additions,
            long comparisons) {

        return "Calls: " + calls +
            " | Base-case checks: " + checks +
            "\nMultiplications: " + multiplications +
            " | Additions: " + additions +
            " | Value comparisons: " + comparisons +
            "\nCounted operations: " +
            (calls + checks + multiplications + additions + comparisons);
    }

    private void runAlgorithm() {
        resetResults();

        try {
            int input = parseInteger(
                inputField.getText(),
                isBinarySearch() ? "Target" : "Input"
            );

            if (isBinarySearch()) {
                int[] numbers = parseArray(arrayField.getText());

                currentResult = RecursionAlgorithms.binarySearch(
                    numbers, input
                );

                long index = currentResult.getValue();

                output.setText(
                    index == -1
                        ? input + " was not found."
                        : input + " was found at index " + index + "."
                );
            } else {
                String algorithm = algorithmChoice.getValue();
                currentResult = RecursionAlgorithms.run(algorithm, input);

                String expression = "Factorial".equals(algorithm)
                    ? input + "!"
                    : "F(" + input + ")";

                output.setText(
                    expression + " = " + currentResult.getValue()
                );
            }

            totals.setText(
                "FULL RUN — " +
                statistics(
                    currentResult.getCalls(),
                    currentResult.getBaseChecks(),
                    currentResult.getMultiplications(),
                    currentResult.getAdditions(),
                    currentResult.getValueComparisons()
                ) +
                "\nMaximum active depth: " +
                currentResult.getMaximumDepth() +
                " | Recorded execution time: " +
                currentResult.getElapsedNanoseconds() + " ns"
            );

            showStep();

        } catch (IllegalArgumentException exception) {
            status.setStyle("-fx-text-fill: #b91c1c;");
            status.setText(exception.getMessage());
        }
    }

    private int parseInteger(String value, String label) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                label + " must be a whole number between " +
                "-2147483648 and 2147483647."
            );
        }
    }

    private int[] parseArray(String input) {
        if (input.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Enter at least one sorted number."
            );
        }

        String[] parts = input.split(",", -1);

        if (parts.length > 50) {
            throw new IllegalArgumentException(
                "Enter no more than 50 numbers."
            );
        }

        int[] numbers = new int[parts.length];

        for (int i = 0; i < parts.length; i++) {
            numbers[i] = parseInteger(
                parts[i], "Array item " + (i + 1)
            );
        }

        return numbers;
    }

    private boolean hasNext() {
        return currentResult != null &&
            currentStep < currentResult.getStepCount() - 1;
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
                speedSlider.getValue(),
                1.0 / speedSlider.getValue()
            )
        );
    }

    private void updateControls() {
        boolean loaded = currentResult != null;
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
                atEnd ? "Complete. All recursive calls have returned."
                : playing ? "Playing calls and returns..."
                : "Press Play or inspect individual steps."
            );
        }
    }

    private void showStep() {
        RecursionAlgorithms.Step step =
            currentResult.getStep(currentStep);

        stepPosition.setText(
            "Step " + (currentStep + 1) +
            " of " + currentResult.getStepCount()
        );

        description.setText(step.description);
        stackArea.setText(step.activeStack);
        stackArea.positionCaret(0);

        stepMetrics.setText(
            "AT THIS STEP — " +
            statistics(
                step.calls,
                step.baseChecks,
                step.multiplications,
                step.additions,
                step.valueComparisons
            ) +
            "\nActive depth: " + step.activeDepth
        );

        updateControls();
    }

    private void resetResults() {
        stopPlayback();
        currentResult = null;
        currentStep = 0;

        status.setStyle("-fx-text-fill: #475569;");
        status.setText("Choose an algorithm, enter input and run.");

        output.setText("Result: —");
        totals.setText("Full-run statistics: —");
        stepPosition.setText("No steps loaded");
        description.setText("Calls and returns will appear here.");
        stackArea.clear();
        stepMetrics.setText("Statistics at this step: —");

        updateControls();
    }
}