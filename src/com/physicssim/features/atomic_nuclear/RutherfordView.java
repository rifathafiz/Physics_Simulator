package com.physicssim.features.atomic_nuclear;

import com.physicssim.model.atomic_nuclear.RutherfordModel;
import com.physicssim.theme.AppTheme;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class RutherfordView extends BorderPane {

    private final RutherfordModel model = new RutherfordModel();
    private final Canvas canvas = new Canvas(600, 400);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    private final Slider speedSlider = new Slider(0.5, 3.0, 1.0);
    private final Label speedLabel = new Label("1.0x speed");

    private final Label transmittedLabel = new Label("Transmitted: 0");
    private final Label deflectedLabel = new Label("Deflected: 0");
    private final Label reflectedLabel = new Label("Reflected: 0");

    private final Button startBtn = new Button("Start");
    private final Button pauseBtn = new Button("Pause");
    private final Button resumeBtn = new Button("Resume");
    private final Button resetBtn = new Button("Reset");

    private enum RunState {
        IDLE, RUNNING, PAUSED
    }

    private RunState runState = RunState.IDLE;
    private AnimationTimer animationTimer;
    private double spawnTimer = 0;
    private static final double SPAWN_INTERVAL = 0.3;

    public RutherfordView() {
        setPadding(new Insets(18));
        setStyle("-fx-background-color: transparent;");

        Pane canvasContainer = new Pane(canvas);
        canvasContainer.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d9e2ee; -fx-border-radius: 8; -fx-background-radius: 8;");

        styleButton(startBtn, "#22c55e");
        styleButton(pauseBtn, "#f59e0b");
        styleButton(resumeBtn, "#3157d5");
        styleButton(resetBtn, "#ef4444");

        startBtn.setOnAction(event -> startSimulation());
        pauseBtn.setOnAction(event -> pauseSimulation());
        resumeBtn.setOnAction(event -> resumeSimulation());
        resetBtn.setOnAction(event -> resetSimulation());

        HBox buttons = new HBox(8, startBtn, pauseBtn, resumeBtn, resetBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Rutherford Gold Foil Experiment");
        title.setFont(AppTheme.cardTitleFont());
        title.setTextFill(AppTheme.TEXT_PRIMARY);

        Label explanation = new Label(
                "Discovery of the Nucleus:\n" +
                "• Most alpha particles passed straight through (atom is mostly empty space).\n" +
                "• Some were deflected at small angles (positive charge concentrated).\n" +
                "• Very few bounced back (dense, positively charged nucleus at center).\n" +
                "• This led to the nuclear model of the atom.");
        explanation.setFont(Font.font(12));
        explanation.setStyle("-fx-text-fill: #6b7280; -fx-line-spacing: 4px;");
        explanation.setWrapText(true);

        VBox controls = new VBox(12,
                title,
                explanation,
                sliderRow("Particle Speed", speedSlider, speedLabel, "x"),
                statRow("Transmitted", transmittedLabel, "#22c55e"),
                statRow("Deflected", deflectedLabel, "#f59e0b"),
                statRow("Reflected", reflectedLabel, "#ef4444"),
                buttons);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPadding(new Insets(8));
        controls.setPrefWidth(280);

        HBox body = new HBox(24, canvasContainer, controls);
        body.setAlignment(Pos.TOP_LEFT);

        setCenter(body);
        setupAnimation();
        resetSimulation();
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

                spawnTimer += deltaTime;
                if (spawnTimer >= SPAWN_INTERVAL) {
                    model.spawnParticle();
                    spawnTimer = 0;
                }

                model.updateParticles(deltaTime);
                updateStats();
                drawCanvas();
            }
        };
        animationTimer.start();
    }

    private void startSimulation() {
        runState = RunState.RUNNING;
        updateButtonStates();
        drawCanvas();
    }

    private void pauseSimulation() {
        if (runState == RunState.RUNNING) {
            runState = RunState.PAUSED;
            updateButtonStates();
        }
    }

    private void resumeSimulation() {
        if (runState == RunState.PAUSED) {
            runState = RunState.RUNNING;
            updateButtonStates();
        }
    }

    private void resetSimulation() {
        runState = RunState.IDLE;
        model.reset();
        spawnTimer = 0;
        updateButtonStates();
        updateStats();
        drawCanvas();
    }

    private void updateButtonStates() {
        startBtn.setDisable(runState != RunState.IDLE);
        pauseBtn.setDisable(runState != RunState.RUNNING);
        resumeBtn.setDisable(runState != RunState.PAUSED);
        resetBtn.setDisable(false);
    }

    private void updateStats() {
        transmittedLabel.setText("Transmitted: " + model.getTransmittedCount());
        deflectedLabel.setText("Deflected: " + model.getDeflectedCount());
        reflectedLabel.setText("Reflected: " + model.getReflectedCount());
    }

    private HBox sliderRow(String label, Slider slider, Label valueLabel, String unit) {
        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        titleLabel.setPrefWidth(150);
        titleLabel.setTextFill(AppTheme.TEXT_PRIMARY);

        slider.setPrefWidth(120);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(0.5);

        valueLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1e293b;");
        valueLabel.setPrefWidth(80);

        slider.valueProperty().addListener((obs, old, val) -> {
            model.setSpeed(val.doubleValue());
            speedLabel.setText(String.format("%.1fx speed", val.doubleValue()));
        });

        Label unitLabel = new Label(unit);
        unitLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280;");

        HBox row = new HBox(8, titleLabel, slider, valueLabel, unitLabel);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private VBox statRow(String label, Label valueLabel, String color) {
        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        titleLabel.setTextFill(AppTheme.TEXT_PRIMARY);

        valueLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 700; -fx-text-fill: " + color + ";");

        return new VBox(4, titleLabel, valueLabel);
    }

    private void drawCanvas() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        double centerX = canvas.getWidth() / 2;
        double centerY = canvas.getHeight() / 2;

        gc.setFill(Color.web("#ffd700"));
        gc.fillRect(RutherfordModel.FOIL_X, RutherfordModel.FOIL_Y - RutherfordModel.FOIL_HEIGHT / 2,
                RutherfordModel.FOIL_WIDTH, RutherfordModel.FOIL_HEIGHT);
        gc.setFill(Color.web("#b8860b"));
        gc.setFont(Font.font(11));
        gc.fillText("Gold Foil", RutherfordModel.FOIL_X - 15, RutherfordModel.FOIL_Y - RutherfordModel.FOIL_HEIGHT / 2 - 10);

        gc.setFill(Color.web("#6b7280"));
        gc.fillOval(30, RutherfordModel.FOIL_Y - 20, 40, 40);
        gc.setFill(Color.WHITE);
        gc.fillText("α Source", 25, RutherfordModel.FOIL_Y + 35);

        for (RutherfordModel.AlphaParticle particle : model.getParticles()) {
            Color particleColor;
            String explanation = "";
            Color textColor = Color.BLACK;

            switch (particle.getInteractionType()) {
                case TRANSMITTED:
                    particleColor = Color.web("#22c55e");
                    explanation = "Passed through empty space";
                    textColor = Color.web("#22c55e");
                    break;
                case DEFLECTED:
                    particleColor = Color.web("#f59e0b");
                    explanation = "Repulsion of alpha particle";
                    textColor = Color.web("#f59e0b");
                    break;
                case REFLECTED:
                    particleColor = Color.web("#ef4444");
                    explanation = "Collided with nucleus!";
                    textColor = Color.web("#ef4444");
                    break;
                default:
                    particleColor = Color.web("#3b82f6");
            }

            gc.setFill(particleColor);
            gc.fillOval(particle.getX() - 4, particle.getY() - 4, 8, 8);

            // Draw explanation label near the particle
            if (particle.hasInteracted() && !explanation.isEmpty()) {
                gc.setFill(textColor);
                gc.setFont(Font.font(10));
                gc.fillText(explanation, particle.getX() + 10, particle.getY() - 5);
            }
        }

        gc.setFill(Color.web("#6b7280"));
        gc.setFont(Font.font(11));
        gc.fillText("Green: Transmitted | Orange: Deflected | Red: Reflected", 10, canvas.getHeight() - 10);
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 16; -fx-min-width: 80px;");
    }
}
