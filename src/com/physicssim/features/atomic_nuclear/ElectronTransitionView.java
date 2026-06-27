package com.physicssim.features.atomic_nuclear;

import com.physicssim.model.atomic_nuclear.ElectronTransitionModel;
import com.physicssim.theme.AppTheme;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ElectronTransitionView extends BorderPane {

    private final ElectronTransitionModel model = new ElectronTransitionModel();
    private final Canvas canvas = new Canvas(600, 400);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    private final ComboBox<Integer> initialLevelCombo = new ComboBox<>();
    private final ComboBox<Integer> finalLevelCombo = new ComboBox<>();

    private final Label initialEnergyLabel = new Label("Initial E: -13.60 eV");
    private final Label finalEnergyLabel = new Label("Final E: -3.40 eV");
    private final Label energyDiffLabel = new Label("ΔE: 10.20 eV (Absorbed)");
    private final Label transitionLabel = new Label("Ready");

    private final Button startBtn = new Button("Start Transition");
    private final Button resetBtn = new Button("Reset");
    private final Button replayBtn = new Button("Replay");

    private AnimationTimer animationTimer;

    public ElectronTransitionView() {
        setPadding(new Insets(18));
        setStyle("-fx-background-color: transparent;");

        Pane canvasContainer = new Pane(canvas);
        canvasContainer.setStyle("-fx-background-color: #ffffff; -fx-border-color: #d9e2ee; -fx-border-radius: 8; -fx-background-radius: 8;");

        for (int i = 1; i <= 4; i++) {
            initialLevelCombo.getItems().add(i);
            finalLevelCombo.getItems().add(i);
        }
        initialLevelCombo.setValue(1);
        finalLevelCombo.setValue(2);

        initialLevelCombo.setOnAction(event -> {
            model.setInitialLevel(initialLevelCombo.getValue());
            updateEnergyLabels();
            if (!model.isTransitioning()) {
                drawCanvas();
            }
        });

        finalLevelCombo.setOnAction(event -> {
            model.setFinalLevel(finalLevelCombo.getValue());
            updateEnergyLabels();
            if (!model.isTransitioning()) {
                drawCanvas();
            }
        });

        styleButton(startBtn, "#22c55e");
        styleButton(resetBtn, "#ef4444");
        styleButton(replayBtn, "#3157d5");

        startBtn.setOnAction(event -> startTransition());
        resetBtn.setOnAction(event -> resetSimulation());
        replayBtn.setOnAction(event -> replayTransition());

        HBox buttons = new HBox(8, startBtn, resetBtn, replayBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Electron Transition");
        title.setFont(AppTheme.cardTitleFont());
        title.setTextFill(AppTheme.TEXT_PRIMARY);

        Label explanation = new Label(
                "Energy Absorption & Emission:\n" +
                "• When an electron moves to a higher orbit, it absorbs a photon (energy in).\n" +
                "• When an electron falls to a lower orbit, it emits a photon (energy out).\n" +
                "• The photon energy equals the difference between energy levels: ΔE = hf.\n" +
                "• This explains atomic spectra and the quantized nature of energy.");
        explanation.setFont(Font.font(12));
        explanation.setStyle("-fx-text-fill: #6b7280; -fx-line-spacing: 4px;");
        explanation.setWrapText(true);

        VBox controls = new VBox(12,
                title,
                explanation,
                comboRow("Initial Energy Level", initialLevelCombo),
                comboRow("Final Energy Level", finalLevelCombo),
                initialEnergyLabel,
                finalEnergyLabel,
                energyDiffLabel,
                transitionLabel,
                buttons);
        controls.setAlignment(Pos.TOP_LEFT);
        controls.setPadding(new Insets(8));
        controls.setPrefWidth(280);

        HBox body = new HBox(24, canvasContainer, controls);
        body.setAlignment(Pos.TOP_LEFT);

        setCenter(body);
        setupAnimation();
        updateEnergyLabels();
        drawCanvas();
    }

    private void setupAnimation() {
        animationTimer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1e9;
                lastUpdate = now;

                deltaTime = Math.min(deltaTime, 0.05);

                model.advanceElectronAngle(deltaTime);
                model.updateTransition(deltaTime);
                updateTransitionLabel();
                drawCanvas();
            }
        };
        animationTimer.start();
    }

    private void startTransition() {
        if (!model.isTransitioning() && model.getInitialLevel() != model.getFinalLevel()) {
            model.startTransition();
            startBtn.setDisable(true);
            initialLevelCombo.setDisable(true);
            finalLevelCombo.setDisable(true);
        }
    }

    private void resetSimulation() {
        model.reset();
        initialLevelCombo.setValue(1);
        finalLevelCombo.setValue(2);
        startBtn.setDisable(false);
        initialLevelCombo.setDisable(false);
        finalLevelCombo.setDisable(false);
        updateEnergyLabels();
        updateTransitionLabel();
        drawCanvas();
    }

    private void replayTransition() {
        resetSimulation();
        startTransition();
    }

    private void updateEnergyLabels() {
        double initialEnergy = model.getEnergyLevel(model.getInitialLevel());
        double finalEnergy = model.getEnergyLevel(model.getFinalLevel());
        double energyDiff = model.getEnergyDifference();

        initialEnergyLabel.setText(String.format("Initial E: %.2f eV", initialEnergy));
        finalEnergyLabel.setText(String.format("Final E: %.2f eV", finalEnergy));

        if (model.isAbsorbing()) {
            energyDiffLabel.setText(String.format("ΔE: %.2f eV (Absorbed)", energyDiff));
            energyDiffLabel.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: 700;");
        } else if (model.isEmitting()) {
            energyDiffLabel.setText(String.format("ΔE: %.2f eV (Emitted)", energyDiff));
            energyDiffLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: 700;");
        } else {
            energyDiffLabel.setText(String.format("ΔE: 0.00 eV", energyDiff));
            energyDiffLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: 700;");
        }
    }

    private void updateTransitionLabel() {
        if (model.isTransitioning()) {
            if (model.isAbsorbing()) {
                transitionLabel.setText("Absorbing photon...");
                transitionLabel.setStyle("-fx-text-fill: #3b82f6; -fx-font-weight: 700;");
            } else {
                transitionLabel.setText("Emitting photon...");
                transitionLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: 700;");
            }
        } else if (model.isShowingPhoton()) {
            if (model.isAbsorbing()) {
                transitionLabel.setText("Photon absorbed!");
                transitionLabel.setStyle("-fx-text-fill: #22c55e; -fx-font-weight: 700;");
            } else {
                transitionLabel.setText("Photon emitted!");
                transitionLabel.setStyle("-fx-text-fill: #f59e0b; -fx-font-weight: 700;");
            }
        } else {
            transitionLabel.setText("Ready");
            transitionLabel.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: 700;");
        }
    }

    private HBox comboRow(String label, ComboBox<Integer> combo) {
        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-text-fill: #1e293b;");
        titleLabel.setPrefWidth(150);
        titleLabel.setTextFill(AppTheme.TEXT_PRIMARY);

        combo.setPrefWidth(120);

        HBox row = new HBox(8, titleLabel, combo);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
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

        for (int i = 1; i <= 4; i++) {
            double radius = model.getShellRadius(i);
            gc.setStroke(Color.web("#94a3b8"));
            gc.setLineWidth(2);
            gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);

            gc.setFill(Color.web("#475569"));
            gc.setFont(Font.font(11));
            gc.fillText(model.getShellName(i), centerX + radius + 5, centerY - 5);
        }

        double electronRadius = model.getCurrentRadius();
        double electronAngle = model.getElectronAngle();
        double electronX = centerX + electronRadius * Math.cos(electronAngle);
        double electronY = centerY + electronRadius * Math.sin(electronAngle);

        gc.setFill(Color.web("#3b82f6"));
        gc.fillOval(electronX - 6, electronY - 6, 12, 12);

        if (model.isShowingPhoton()) {
            double photonAngle = model.getPhotonAngle();
            double photonDistance = model.getPhotonDistance();
            double photonX = electronX + photonDistance * Math.cos(photonAngle);
            double photonY = electronY + photonDistance * Math.sin(photonAngle);

            if (model.isAbsorbing()) {
                Color pathColor = Color.web("#3b82f6");
                gc.setStroke(Color.color(pathColor.getRed(), pathColor.getGreen(), pathColor.getBlue(), 0.3));
                gc.setLineWidth(2);
                gc.strokeLine(electronX + 200 * Math.cos(photonAngle), electronY + 200 * Math.sin(photonAngle),
                        photonX, photonY);

                Color photonColor = Color.web("#3b82f6");
                for (int i = 0; i < 3; i++) {
                    double alpha = 0.6 - i * 0.15;
                    Color glowColor = Color.color(0.23, 0.51, 0.96, alpha);
                    gc.setStroke(glowColor);
                    gc.setLineWidth(4 - i);
                    gc.strokeOval(photonX - 8 - i * 4, photonY - 8 - i * 4, 16 + i * 8, 16 + i * 8);
                }
                gc.setFill(photonColor);
                gc.fillOval(photonX - 5, photonY - 5, 10, 10);

                gc.setStroke(Color.web("#3b82f6"));
                gc.setLineWidth(2);
                double arrowX = electronX + (photonDistance - 15) * Math.cos(photonAngle);
                double arrowY = electronY + (photonDistance - 15) * Math.sin(photonAngle);
                gc.strokeLine(photonX, photonY, arrowX, arrowY);
            } else {
                Color pathColor = Color.web("#f59e0b");
                gc.setStroke(Color.color(pathColor.getRed(), pathColor.getGreen(), pathColor.getBlue(), 0.3));
                gc.strokeLine(photonX, photonY,
                        electronX + 250 * Math.cos(photonAngle), electronY + 250 * Math.sin(photonAngle));

                Color photonColor = Color.web("#f59e0b");
                for (int i = 0; i < 3; i++) {
                    double alpha = 0.7 - i * 0.2;
                    Color glowColor = Color.color(0.96, 0.62, 0.04, alpha);
                    gc.setStroke(glowColor);
                    gc.setLineWidth(5 - i);
                    gc.strokeOval(photonX - 10 - i * 5, photonY - 10 - i * 5, 20 + i * 10, 20 + i * 10);
                }
                gc.setFill(photonColor);
                gc.fillOval(photonX - 6, photonY - 6, 12, 12);

                gc.setStroke(Color.web("#f59e0b"));
                gc.setLineWidth(2);
                double arrowX = electronX + (photonDistance + 15) * Math.cos(photonAngle);
                double arrowY = electronY + (photonDistance + 15) * Math.sin(photonAngle);
                gc.strokeLine(photonX, photonY, arrowX, arrowY);
            }
        }

        gc.setFill(Color.web("#6b7280"));
        gc.setFont(Font.font(11));
        gc.fillText("Red: Nucleus | Blue: Electron | Blue: Absorbed Photon | Orange: Emitted Photon", 10, canvas.getHeight() - 10);
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-font-size: 13px; -fx-font-weight: 700; -fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 16; -fx-min-width: 100px;");
    }
}
