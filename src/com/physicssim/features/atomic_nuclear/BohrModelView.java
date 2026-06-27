package com.physicssim.features.atomic_nuclear;

import com.physicssim.model.atomic_nuclear.BohrModel;
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

public class BohrModelView extends BorderPane {

    private final BohrModel model = new BohrModel();
    private final Canvas canvas = new Canvas(600, 400);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    private final Slider shellSlider = new Slider(1, 4, 4);
    private final Slider electronSlider = new Slider(1, 54, 10);
    private final Slider speedSlider = new Slider(0.1, 5.0, 1.0);

    private final Label shellLabel = new Label("4 shells");
    private final Label electronLabel = new Label("10 electrons");
    private final Label speedLabel = new Label("1.0x speed");

    private final Button startBtn = new Button("Start");
    private final Button pauseBtn = new Button("Pause");
    private final Button resumeBtn = new Button("Resume");
    private final Button resetBtn = new Button("Reset");

    private enum RunState {
        IDLE, RUNNING, PAUSED
    }

    private RunState runState = RunState.IDLE;
    private AnimationTimer animationTimer;

    public BohrModelView() {
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

        Label title = new Label("Bohr's Atomic Model");
        title.setFont(AppTheme.cardTitleFont());
        title.setTextFill(AppTheme.TEXT_PRIMARY);

        Label explanation = new Label(
                "Bohr's Postulates:\n" +
                "• Electrons orbit the nucleus in fixed, quantized energy levels (shells).\n" +
                "• Electrons do not radiate energy while in these stable orbits.\n" +
                "• Energy is absorbed/emitted only when electrons jump between orbits.\n" +
                "• Angular momentum is quantized: mvr = nh/2π");
        explanation.setFont(Font.font(12));
        explanation.setStyle("-fx-text-fill: #6b7280; -fx-line-spacing: 4px;");
        explanation.setWrapText(true);

        VBox controls = new VBox(12,
                title,
                explanation,
                sliderRow("Energy Levels (Shells)", shellSlider, shellLabel, ""),
                sliderRow("Total Electrons", electronSlider, electronLabel, ""),
                sliderRow("Animation Speed", speedSlider, speedLabel, "x"),
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

                model.update(deltaTime);
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
        model.resetTime();
        updateButtonStates();
        drawCanvas();
    }

    private void updateButtonStates() {
        startBtn.setDisable(runState != RunState.IDLE);
        pauseBtn.setDisable(runState != RunState.RUNNING);
        resumeBtn.setDisable(runState != RunState.PAUSED);
        resetBtn.setDisable(false);
    }

    private HBox sliderRow(String label, Slider slider, Label valueLabel, String unit) {
        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        titleLabel.setPrefWidth(150);
        titleLabel.setTextFill(AppTheme.TEXT_PRIMARY);

        slider.setPrefWidth(120);
        slider.setShowTickMarks(true);
        slider.setShowTickLabels(true);
        slider.setMajorTickUnit(1);

        valueLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #1e293b;");
        valueLabel.setPrefWidth(80);

        slider.valueProperty().addListener((obs, old, val) -> {
            if (label.contains("Energy Levels")) {
                model.setNumShells((int) val.doubleValue());
                shellLabel.setText((int) val.doubleValue() + " shells");
            } else if (label.contains("Total Electrons")) {
                model.setTotalElectrons((int) val.doubleValue());
                electronLabel.setText((int) val.doubleValue() + " electrons");
            } else if (label.contains("Animation Speed")) {
                model.setSpeedMultiplier(val.doubleValue());
                speedLabel.setText(String.format("%.1fx speed", val.doubleValue()));
            }
            if (runState == RunState.IDLE) {
                drawCanvas();
            }
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

        gc.setFill(Color.web("#dc2626"));
        gc.fillOval(centerX - 15, centerY - 15, 30, 30);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(12));
        gc.fillText("Nucleus", centerX - 22, centerY + 4);

        int numShells = model.getNumShells();
        for (int i = 0; i < numShells; i++) {
            double radius = model.getShellRadius(i);
            gc.setStroke(Color.web("#94a3b8"));
            gc.setLineWidth(2);
            gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            gc.setFill(Color.web("#475569"));
            gc.setFont(Font.font(11));
            gc.fillText(model.getShellName(i), centerX + radius + 5, centerY - 5);
        }

        for (BohrModel.ShellElectron electron : model.getElectrons()) {
            double radius = model.getShellRadius(electron.getShellIndex());
            double angle = electron.getAngle();
            double x = centerX + radius * Math.cos(angle);
            double y = centerY + radius * Math.sin(angle);

            gc.setFill(Color.web("#3b82f6"));
            gc.fillOval(x - 6, y - 6, 12, 12);
        }

        gc.setFill(Color.web("#6b7280"));
        gc.setFont(Font.font(11));
        gc.fillText("Red: Nucleus | Blue: Electrons | Gray: Energy Levels", 10, canvas.getHeight() - 10);
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 16; -fx-min-width: 80px;");
    }
}
