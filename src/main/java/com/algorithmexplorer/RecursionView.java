package com.algorithmexplorer;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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

    private final TextField inputField = new TextField();

    private final Label status = new Label();
    private final Label output = new Label();
    private final Label totals = new Label();
    private final Label stepPosition = new Label();
    private final Label description = new Label();
    private final Label stepMetrics = new Label();
    private final Label speedLabel = new Label();

    private final TextArea stackArea = new TextArea();

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

        Label title = new Label("Recursion Lab — Factorial");
        title.setStyle(
            "-fx-font-size: 26px; -fx-font-weight: bold;"
        );

        inputField.setPromptText("Enter an integer from 0 to 20");
        inputField.setOnAction(event -> runFactorial());

        Button run = new Button("Run Factorial");
        run.setStyle(
            "-fx-background-color: #1d4ed8;" +
            "-fx-text-fill: white; -fx-padding: 10 18;"
        );
        run.setOnAction(event -> runFactorial());

        Button example = new Button("Load Example");
        example.setOnAction(event -> {
            inputField.setText("5");
            resetResults();
        });

        Button clear = new Button("Clear");
        clear.setOnAction(event -> {
            inputField.clear();
            resetResults();
            inputField.requestFocus();
        });

        FlowPane inputControls = new FlowPane(10, 10);
        inputControls.getChildren().addAll(run, example, clear);

        Label[] labels = {
            status, output, totals, stepPosition,
            description, stepMetrics, speedLabel
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
            text(
                "Factorial: n! = n × (n − 1)!.\n" +
                "Base cases: 0! = 1 and 1! = 1."
            ),
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
            text(
                "CALL adds a frame. RETURN removes that frame and passes " +
                "its answer to the caller. The displayed stack shows the " +
                "state after the recorded action."
            ),
            text(
                "Theoretical algorithm complexity: O(n) time and O(n) " +
                "call-stack space, under a constant-cost arithmetic model.\n" +
                "Recording every stack snapshot adds O(n²) trace storage."
            ),
            text(
                "Counted operations = function calls + base-case checks + " +
                "multiplications. This is a teaching model, not a count of " +
                "every Java instruction. Calls include the initial invocation."
            ),
            text(
                "Maximum depth counts simultaneously active calls, including " +
                "the initial call. Time includes recursion, counters and " +
                "trace recording; it excludes initial trace allocation " +
                "and interface playback."
            ),
            text(
                "Input is limited to 0–20 because 21! exceeds the range " +
                "of Java's long type."
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

        resetResults();
    }

    private Label text(String value) {
        Label label = new Label(value);
        label.setWrapText(true);
        return label;
    }

    private String statistics(
            long calls,
            long checks,
            long multiplications) {

        return "Calls: " + calls +
            " | Base-case checks: " + checks +
            " | Multiplications: " + multiplications +
            "\nCounted operations: " + (calls + checks + multiplications);
    }

    private void runFactorial() {
        resetResults();

        final int input;

        try {
            input = Integer.parseInt(inputField.getText().trim());
        } catch (NumberFormatException exception) {
            showError("Enter a whole number from 0 to 20.");
            return;
        }

        try {
            currentResult = RecursionAlgorithms.factorial(input);

            output.setText(input + "! = " + currentResult.getValue());

            totals.setText(
                "FULL RUN — " +
                statistics(
                    currentResult.getCalls(),
                    currentResult.getBaseChecks(),
                    currentResult.getMultiplications()
                ) +
                "\nMaximum active depth: " +
                currentResult.getMaximumDepth() +
                " | Recorded execution time: " +
                currentResult.getElapsedNanoseconds() + " ns"
            );

            showStep();

        } catch (IllegalArgumentException exception) {
            showError(exception.getMessage());
        }
    }

    private void showError(String message) {
        status.setStyle("-fx-text-fill: #b91c1c;");
        status.setText(message);
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
                step.multiplications
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
        status.setText("Enter a number and click Run Factorial.");

        output.setText("Result: —");
        totals.setText("Full-run statistics: —");
        stepPosition.setText("No steps loaded");
        description.setText("Calls and returns will appear here.");
        stackArea.clear();
        stepMetrics.setText("Statistics at this step: —");

        updateControls();
    }
}