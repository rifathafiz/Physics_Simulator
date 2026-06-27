package com.physicssim.features.atomic_nuclear;

import com.physicssim.model.atomic_nuclear.HalfLifeModel;
import com.physicssim.theme.AppTheme;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

public class HalfLifeView extends BorderPane {

    private final HalfLifeModel model = new HalfLifeModel();
    private final Canvas canvas = new Canvas(400, 400);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    private final TextField initialAtomsField = new TextField("100");
    private final TextField halfLifeField = new TextField("10.0");
    private final Slider timeSlider = new Slider(0, 60, 0);
    private final Label timeLabel = new Label("0.0 s");

    private final Label remainingAtomsLabel = new Label("Remaining: 100");
    private final Label decayedAtomsLabel = new Label("Decayed: 0");
    private final Label elapsedTimeLabel = new Label("0.0 s");
    private final Label halfLivesLabel = new Label("Half-Lives: 0.0");
    private final Label percentageLabel = new Label("Remaining: 100%");

    private final Button startBtn = new Button("Start");
    private final Button pauseBtn = new Button("Pause");
    private final Button resetBtn = new Button("Reset");

    private enum RunState {
        IDLE, RUNNING, PAUSED
    }

    private RunState runState = RunState.IDLE;
    private AnimationTimer animationTimer;
    private boolean sliderDragging = false;

    public HalfLifeView() {
        setPadding(new Insets(18));
        setStyle("-fx-background-color: transparent;");

        setupInputValidation();
        Pane canvasContainer = setupCanvas();
        setupControls(canvasContainer);
        setupAnimation();
        updateStats();
        drawCanvas();
    }

    /**
     * Setup input validation for text fields
     */
    private void setupInputValidation() {
        initialAtomsField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter()));
        halfLifeField.setTextFormatter(new TextFormatter<>(new DoubleStringConverter()));

        initialAtomsField.textProperty().addListener((obs, old, val) -> {
            try {
                int atoms = Integer.parseInt(val);
                if (atoms > 0) {
                    model.setInitialAtoms(atoms);
                    updateStats();
                    drawCanvas();
                }
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        });

        halfLifeField.textProperty().addListener((obs, old, val) -> {
            try {
                double hl = Double.parseDouble(val);
                if (hl > 0) {
                    model.setHalfLife(hl);
                    updateStats();
                }
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        });
    }

    private Pane setupCanvas() {
        Pane canvasContainer = new Pane(canvas);
        canvasContainer.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d9e2ee; -fx-border-radius: 8; -fx-background-radius: 8;");
        return canvasContainer;
    }

    private void setupControls(Pane canvasContainer) {
        styleButton(startBtn, "#22c55e");
        styleButton(pauseBtn, "#f59e0b");
        styleButton(resetBtn, "#ef4444");

        startBtn.setOnAction(event -> startSimulation());
        pauseBtn.setOnAction(event -> pauseSimulation());
        resetBtn.setOnAction(event -> resetSimulation());

        HBox buttons = new HBox(8, startBtn, pauseBtn, resetBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        // Time slider setup
        timeSlider.setShowTickMarks(true);
        timeSlider.setShowTickLabels(true);
        timeSlider.setMajorTickUnit(10);
        timeSlider.valueProperty().addListener((obs, old, val) -> {
            if (sliderDragging) {
                model.setElapsedTime(val.doubleValue());
                timeLabel.setText(String.format("%.1f s", val.doubleValue()));
                updateStats();
                drawCanvas();
            }
        });
        timeSlider.setOnMousePressed(e -> sliderDragging = true);
        timeSlider.setOnMouseReleased(e -> sliderDragging = false);

        // Left panel - Controls
        VBox leftPanel = new VBox(12,
                createLabel("Initial Atoms:"),
                initialAtomsField,
                createLabel("Half-Life (s):"),
                halfLifeField,
                createLabel("Elapsed Time:"),
                timeSlider,
                timeLabel,
                buttons
        );
        leftPanel.setAlignment(Pos.TOP_LEFT);
        leftPanel.setPadding(new Insets(8));
        leftPanel.setPrefWidth(200);

        // Right panel - Statistics
        VBox rightPanel = new VBox(16,
                createStatLabel("Remaining Atoms", remainingAtomsLabel, "#22c55e"),
                createStatLabel("Decayed Atoms", decayedAtomsLabel, "#ef4444"),
                createStatLabel("Elapsed Time", elapsedTimeLabel, "#3b82f6"),
                createStatLabel("Half-Lives Passed", halfLivesLabel, "#f59e0b"),
                createStatLabel("Percentage Remaining", percentageLabel, "#8b5cf6")
        );
        rightPanel.setAlignment(Pos.TOP_LEFT);
        rightPanel.setPadding(new Insets(8));
        rightPanel.setPrefWidth(180);

        HBox body = new HBox(24, leftPanel, canvasContainer, rightPanel);
        body.setAlignment(Pos.TOP_LEFT);

        setCenter(body);
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        label.setTextFill(AppTheme.TEXT_PRIMARY);
        return label;
    }

    private VBox createStatLabel(String title, Label valueLabel, String color) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        titleLabel.setTextFill(AppTheme.TEXT_PRIMARY);

        valueLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: " + color + ";");

        return new VBox(4, titleLabel, valueLabel);
    }

    private void setupAnimation() {
        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (runState != RunState.RUNNING) {
                    lastUpdate = 0;
                    return;
                }

                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1e9;
                lastUpdate = now;

                deltaTime = Math.min(deltaTime, 0.05);

                model.addElapsedTime(deltaTime);
                updateStats();
                drawCanvas();
            }
        };
        animationTimer.start();
    }

    private void startSimulation() {
        if (runState == RunState.IDLE || runState == RunState.PAUSED) {
            runState = RunState.RUNNING;
            updateButtonStates();
        }
    }

    private void pauseSimulation() {
        if (runState == RunState.RUNNING) {
            runState = RunState.PAUSED;
            updateButtonStates();
        }
    }

    private void resetSimulation() {
        runState = RunState.IDLE;
        model.reset();
        timeSlider.setValue(0);
        timeLabel.setText("0.0 s");
        updateButtonStates();
        updateStats();
        drawCanvas();
    }

    private void updateButtonStates() {
        startBtn.setDisable(runState == RunState.RUNNING);
        pauseBtn.setDisable(runState != RunState.RUNNING);
        resetBtn.setDisable(false);
    }

    private void updateStats() {
        remainingAtomsLabel.setText("Remaining: " + model.calculateRemainingAtoms());
        decayedAtomsLabel.setText("Decayed: " + model.calculateDecayedAtoms());
        elapsedTimeLabel.setText(String.format("%.1f s", model.getElapsedTime()));
        halfLivesLabel.setText(String.format("Half-Lives: %.2f", model.calculateHalfLivesPassed()));
        percentageLabel.setText(String.format("Remaining: %.1f%%", model.calculatePercentageRemaining()));

        // Update elapsed time label in left panel
        timeLabel.setText(String.format("%.1f s", model.getElapsedTime()));

        // Update slider if not being dragged
        if (!sliderDragging) {
            timeSlider.setValue(model.getElapsedTime());
        }
    }

    private void drawCanvas() {
        // Clear canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw container border
        gc.setStroke(Color.web("#d9e2ee"));
        gc.setLineWidth(2);
        gc.strokeRect(1, 1, canvas.getWidth() - 2, canvas.getHeight() - 2);

        // Draw atoms
        for (HalfLifeModel.Atom atom : model.getAtoms()) {
            double alpha = atom.getAlpha();
            if (alpha > 0) {
                Color atomColor = Color.color(0.2, 0.6, 0.9, alpha);
                gc.setFill(atomColor);
                gc.fillOval(atom.getX() - 6, atom.getY() - 6, 12, 12);

                // Draw atom border
                gc.setStroke(Color.color(0.1, 0.4, 0.7, alpha));
                gc.setLineWidth(1);
                gc.strokeOval(atom.getX() - 6, atom.getY() - 6, 12, 12);
            }
        }

        // Draw legend
        gc.setFill(Color.web("#6b7280"));
        gc.setFont(Font.font(11));
        gc.fillText("Blue: Radioactive Atoms", 10, canvas.getHeight() - 10);
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 16; -fx-min-width: 80px;");
    }
}
