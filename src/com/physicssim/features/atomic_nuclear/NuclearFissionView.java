package com.physicssim.features.atomic_nuclear;

import com.physicssim.model.atomic_nuclear.NuclearFissionModel;
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

public class NuclearFissionView extends BorderPane {

    private final NuclearFissionModel model = new NuclearFissionModel();
    private final Canvas canvas = new Canvas(600, 400);
    private final GraphicsContext gc = canvas.getGraphicsContext2D();

    private final Slider neutronSpeedSlider = new Slider(0.5, 3.0, 1.0);
    private final Label neutronSpeedLabel = new Label("1.0x");
    private final Slider uraniumCountSlider = new Slider(10, 50, 30);
    private final Label uraniumCountLabel = new Label("30");

    private final Label uraniumRemainingLabel = new Label("U-235 Remaining: 30");
    private final Label fissionEventsLabel = new Label("Fission Events: 0");
    private final Label totalNeutronsLabel = new Label("Total Neutrons: 0");
    private final Label activeNeutronsLabel = new Label("Active Neutrons: 0");
    private final Label energyLabel = new Label("Energy Released: 0 MeV");
    private final Label chainStatusLabel = new Label("Chain Reaction: Finished");

    private final Button fireBtn = new Button("Fire Neutron");
    private final Button pauseBtn = new Button("Pause");
    private final Button resumeBtn = new Button("Resume");
    private final Button resetBtn = new Button("Reset");

    private enum RunState {
        IDLE, RUNNING, PAUSED
    }

    private RunState runState = RunState.IDLE;
    private AnimationTimer animationTimer;

    public NuclearFissionView() {
        setPadding(new Insets(18));
        setStyle("-fx-background-color: transparent;");

        setupCanvas();
        setupControls();
        setupAnimation();
        updateStats();
        drawCanvas();
    }

    private void setupCanvas() {
        Pane canvasContainer = new Pane(canvas);
        canvasContainer.setStyle("-fx-background-color: #0a0a15; -fx-border-color: #3a3a5a; -fx-border-radius: 8; -fx-background-radius: 8;");
    }

    private void setupControls() {
        styleButton(fireBtn, "#22c55e");
        styleButton(pauseBtn, "#f59e0b");
        styleButton(resumeBtn, "#3157d5");
        styleButton(resetBtn, "#ef4444");

        fireBtn.setOnAction(event -> fireNeutron());
        pauseBtn.setOnAction(event -> pauseSimulation());
        resumeBtn.setOnAction(event -> resumeSimulation());
        resetBtn.setOnAction(event -> resetSimulation());

        HBox buttons = new HBox(8, fireBtn, pauseBtn, resumeBtn, resetBtn);
        buttons.setAlignment(Pos.CENTER_LEFT);

        // Neutron speed slider
        neutronSpeedSlider.setShowTickMarks(true);
        neutronSpeedSlider.setShowTickLabels(true);
        neutronSpeedSlider.setMajorTickUnit(0.5);
        neutronSpeedSlider.valueProperty().addListener((obs, old, val) -> {
            model.setNeutronSpeedMultiplier(val.doubleValue());
            neutronSpeedLabel.setText(String.format("%.1fx", val.doubleValue()));
        });

        // Uranium count slider
        uraniumCountSlider.setShowTickMarks(true);
        uraniumCountSlider.setShowTickLabels(true);
        uraniumCountSlider.setMajorTickUnit(10);
        uraniumCountSlider.valueProperty().addListener((obs, old, val) -> {
            model.setUraniumCount((int) val.doubleValue());
            uraniumCountLabel.setText(String.valueOf((int) val.doubleValue()));
            updateStats();
            drawCanvas();
        });

        // Make value labels more visible!
        neutronSpeedLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #7dd3fc; -fx-background-color: #0f172a; -fx-padding: 4 10; -fx-background-radius: 6;");
        uraniumCountLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #fbbf24; -fx-background-color: #0f172a; -fx-padding: 4 10; -fx-background-radius: 6;");

        // Style sliders for more visibility!
        neutronSpeedSlider.setStyle("-fx-control-inner-background: #1e293b; -fx-background-color: #1e293b; -fx-track-color: #334155; -fx-thumb-color: #7dd3fc;");
        uraniumCountSlider.setStyle("-fx-control-inner-background: #1e293b; -fx-background-color: #1e293b; -fx-track-color: #334155; -fx-thumb-color: #fbbf24;");

        // Create larger header for "Controls"
        Label controlsHeader = new Label("Controls");
        controlsHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #3b82f6; -fx-padding: 8 16; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(59,130,246,0.4), 10, 0, 0, 2);");

        // Create bigger, clearer slider labels
        Label speedSliderLabel = new Label("Neutron Speed");
        speedSliderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #7dd3fc;");
        Label countSliderLabel = new Label("U-235 Count (10-50)");
        countSliderLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #fbbf24;");

        // Left panel
        VBox leftPanel = new VBox(16, // More spacing between elements
                controlsHeader,
                buttons,
                speedSliderLabel,
                neutronSpeedSlider,
                neutronSpeedLabel,
                countSliderLabel,
                uraniumCountSlider,
                uraniumCountLabel
        );
        leftPanel.setAlignment(Pos.TOP_LEFT);
        leftPanel.setPadding(new Insets(12)); // More padding
        leftPanel.setPrefWidth(240); // Wider panel

        // Create larger header for "Statistics"
        Label statsHeader = new Label("Statistics");
        statsHeader.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #ffffff; -fx-background-color: #10b981; -fx-padding: 8 16; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(16,185,129,0.4), 10, 0, 0, 2);");

        // Also make statistics labels more visible!
        uraniumRemainingLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #86efac; -fx-font-weight: bold;");
        fissionEventsLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #fbbf24; -fx-font-weight: bold;");
        totalNeutronsLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #7dd3fc; -fx-font-weight: bold;");
        activeNeutronsLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #a78bfa; -fx-font-weight: bold;");
        energyLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #f87171; -fx-font-weight: bold;");

        // Right panel
        VBox rightPanel = new VBox(16, // More spacing
                statsHeader,
                uraniumRemainingLabel,
                fissionEventsLabel,
                totalNeutronsLabel,
                activeNeutronsLabel,
                energyLabel,
                chainStatusLabel
        );
        rightPanel.setAlignment(Pos.TOP_LEFT);
        rightPanel.setPadding(new Insets(12)); // More padding
        rightPanel.setPrefWidth(240); // Wider panel

        HBox body = new HBox(24, leftPanel, new Pane(canvas), rightPanel);
        body.setAlignment(Pos.TOP_LEFT);

        setCenter(body);
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 15px; -fx-font-weight: 700; -fx-text-fill: #e2e8f0;");
        return label;
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
                updateStats();
                drawCanvas();
            }
        };
        animationTimer.start();
    }

    private void fireNeutron() {
        if (runState == RunState.IDLE || runState == RunState.PAUSED) {
            runState = RunState.RUNNING;
            model.fireNeutron();
            updateButtonStates();
        }
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
        updateButtonStates();
        updateStats();
        drawCanvas();
    }

    private void updateButtonStates() {
        fireBtn.setDisable(runState == RunState.RUNNING);
        pauseBtn.setDisable(runState != RunState.RUNNING);
        resumeBtn.setDisable(runState != RunState.PAUSED);
        resetBtn.setDisable(false);
    }

    private void updateStats() {
        uraniumRemainingLabel.setText("U-235 Remaining: " + model.getUraniumRemaining());
        fissionEventsLabel.setText("Fission Events: " + model.getFissionEvents());
        totalNeutronsLabel.setText("Total Neutrons: " + model.getTotalNeutronsProduced());
        activeNeutronsLabel.setText("Active Neutrons: " + model.getActiveNeutrons());
        energyLabel.setText(String.format("Energy Released: %.0f MeV", model.getTotalEnergyReleased()));
        
        String status = model.isChainReactionActive() ? "Running" : "Finished";
        chainStatusLabel.setText("Chain Reaction: " + status);
        chainStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-text-fill: " + 
            (model.isChainReactionActive() ? "#22c55e" : "#ef4444") + ";");
    }

    private void drawCanvas() {
        // Draw 3D laboratory background
        draw3DLaboratory();

        // Draw reactor chamber
        drawReactorChamber();

        // Draw energy bursts (behind everything)
        for (NuclearFissionModel.EnergyBurst energy : model.getEnergyBursts()) {
            drawEnergyBurst(energy);
        }

        // Draw uranium nuclei
        for (NuclearFissionModel.UraniumNucleus nucleus : model.getUraniumNuclei()) {
            drawUraniumNucleus(nucleus);
        }

        // Draw neutrons
        for (NuclearFissionModel.Neutron neutron : model.getNeutrons()) {
            drawNeutron(neutron);
        }

        // Draw fission fragments
        for (NuclearFissionModel.FissionFragment fragment : model.getFissionFragments()) {
            drawFissionFragment(fragment);
        }

        // Draw room blasts
        for (NuclearFissionModel.RoomBlast blast : model.getRoomBlasts()) {
            drawRoomBlast(blast);
        }

        // Draw legend
        drawLegend();
    }

    private void draw3DLaboratory() {
        // Dark background
        gc.setFill(Color.web("#0a0a15"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // 3D perspective grid floor
        gc.setStroke(Color.web("#1a1a2e"));
        gc.setLineWidth(1);
        
        double horizonY = canvas.getHeight() * 0.3;
        for (int i = 0; i < 12; i++) {
            double y = horizonY + Math.pow(i / 11.0, 2) * (canvas.getHeight() - horizonY);
            gc.strokeLine(0, y, canvas.getWidth(), y);
        }

        double vanishingX = canvas.getWidth() / 2;
        for (int i = -8; i <= 8; i++) {
            double x = vanishingX + i * 70;
            gc.strokeLine(x, canvas.getHeight(), vanishingX + i * 12, horizonY);
        }

        // 3D walls
        gc.setFill(Color.web("#12121f"));
        gc.fillRect(0, 0, canvas.getWidth(), horizonY);

        // Wall grid lines
        gc.setStroke(Color.web("#1e1e35"));
        for (int i = 0; i < canvas.getWidth(); i += 50) {
            gc.strokeLine(i, 0, i, horizonY);
        }
        for (int i = 0; i < horizonY; i += 50) {
            gc.strokeLine(0, i, canvas.getWidth(), i);
        }

        // Glowing panels on walls
        gc.setFill(Color.web("#2a2a4a", 0.3));
        gc.fillRect(50, 30, 80, 60);
        gc.fillRect(470, 30, 80, 60);
        gc.fillRect(50, 100, 80, 40);
        gc.fillRect(470, 100, 80, 40);

        // Panel glow
        gc.setStroke(Color.web("#4a4a7a", 0.5));
        gc.setLineWidth(2);
        gc.strokeRect(50, 30, 80, 60);
        gc.strokeRect(470, 30, 80, 60);
    }

    private void drawReactorChamber() {
        // Transparent reactor chamber outline
        gc.setStroke(Color.web("#3a3a5a", 0.6));
        gc.setLineWidth(3);
        gc.strokeRect(80, 60, 440, 280);

        // Chamber glow
        gc.setFill(Color.web("#2a2a4a", 0.2));
        gc.fillRect(80, 60, 440, 280);

        // Chamber corners
        gc.setFill(Color.web("#4a4a6a"));
        gc.fillOval(75, 55, 10, 10);
        gc.fillOval(515, 55, 10, 10);
        gc.fillOval(75, 335, 10, 10);
        gc.fillOval(515, 335, 10, 10);
    }

    private void drawUraniumNucleus(NuclearFissionModel.UraniumNucleus nucleus) {
        if (nucleus.isFissioned()) {
            return; // Don't draw fissioned nuclei
        }

        double vibration = nucleus.getVibrationOffset();
        double x = nucleus.getX() + vibration;
        double y = nucleus.getY() + vibration * 0.5;

        // Blue sphere with gradient
        gc.setFill(Color.web("#3b82f6"));
        gc.fillOval(x - 12, y - 12, 24, 24);

        // Highlight
        gc.setFill(Color.web("#60a5fa"));
        gc.fillOval(x - 8, y - 8, 8, 8);

        // Label
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(9));
        gc.fillText("U-235", x - 14, y + 4);

        // Glow if vibrating
        if (nucleus.isVibrating()) {
            gc.setStroke(Color.web("#fbbf24", 0.6));
            gc.setLineWidth(2);
            gc.strokeOval(x - 15, y - 15, 30, 30);
        }
    }

    private void drawNeutron(NuclearFissionModel.Neutron neutron) {
        // Draw trail
        gc.setStroke(Color.web("#9ca3af", 0.4));
        gc.setLineWidth(2);
        for (int i = 0; i < neutron.getTrail().size() - 1; i++) {
            double[] p1 = neutron.getTrail().get(i);
            double[] p2 = neutron.getTrail().get(i + 1);
            gc.strokeLine(p1[0], p1[1], p2[0], p2[1]);
        }

        // Gray/white sphere
        gc.setFill(Color.web("#e5e7eb"));
        gc.fillOval(neutron.getX() - 4, neutron.getY() - 4, 8, 8);

        // Glow
        gc.setStroke(Color.web("#d1d5db", 0.5));
        gc.setLineWidth(1);
        gc.strokeOval(neutron.getX() - 5, neutron.getY() - 5, 10, 10);

        // Draw neutron name - more visible!
        gc.setFill(Color.web("#60d0ff")); // Brighter cyan
        gc.setFont(Font.font(11)); // Larger font
        gc.fillText(neutron.getName(), neutron.getX() + 8, neutron.getY() - 6);
    }

    private void drawEnergyBurst(NuclearFissionModel.EnergyBurst energy) {
        double alpha = energy.getAlpha();
        double radius = energy.getRadius() * 0.6; // 40% smaller glow!

        // Soft, less intense yellow glow
        Color glowColor = Color.color(1.0, 0.8, 0.4, alpha * 0.4); // Less alpha
        gc.setFill(glowColor);
        gc.fillOval(energy.getX() - radius, energy.getY() - radius, radius * 2, radius * 2);

        // Smaller inner core
        Color coreColor = Color.color(1.0, 1.0, 0.9, alpha * 0.6); // Less bright
        gc.setFill(coreColor);
        gc.fillOval(energy.getX() - radius * 0.3, energy.getY() - radius * 0.3, radius * 0.6, radius * 0.6);

        // Very small "energy" label
        gc.setFill(Color.web("#ffdd88", alpha * 0.8));
        gc.setFont(Font.font(7));
        gc.fillText("energy", energy.getX() - 10, energy.getY() + 3);
    }

    private void drawFissionFragment(NuclearFissionModel.FissionFragment fragment) {
        gc.setFill(Color.web("#f59e0b"));
        gc.fillOval(fragment.getX() - 5, fragment.getY() - 5, 10, 10);
    }

    private void drawRoomBlast(NuclearFissionModel.RoomBlast blast) {
        double progress = blast.getProgress();
        double maxRadius = Math.max(canvas.getWidth(), canvas.getHeight()) * 1.5; // 50% bigger blast!
        double radius = progress * maxRadius;
        double alpha = 1.0 - progress;

        // Multiple layers of explosion for more power!

        // Outermost: Red shockwave
        gc.setFill(Color.color(1.0, 0.1, 0.0, alpha * 0.4));
        gc.fillOval(
                canvas.getWidth() / 2 - radius,
                canvas.getHeight() / 2 - radius,
                radius * 2,
                radius * 2
        );

        // Middle layer: Orange fireball
        gc.setFill(Color.color(1.0, 0.6, 0.0, alpha * 0.6));
        gc.fillOval(
                canvas.getWidth() / 2 - radius * 0.7,
                canvas.getHeight() / 2 - radius * 0.7,
                radius * 1.4,
                radius * 1.4
        );

        // Inner bright white/yellow core
        gc.setFill(Color.color(1.0, 1.0, 0.6, alpha * 0.8));
        gc.fillOval(
                canvas.getWidth() / 2 - radius * 0.4,
                canvas.getHeight() / 2 - radius * 0.4,
                radius * 0.8,
                radius * 0.8
        );

        // Very center bright white dot
        gc.setFill(Color.color(1.0, 1.0, 1.0, alpha));
        gc.fillOval(
                canvas.getWidth() / 2 - radius * 0.1,
                canvas.getHeight() / 2 - radius * 0.1,
                radius * 0.2,
                radius * 0.2
        );
    }

    private void drawLegend() {
        gc.setFill(Color.web("#a0a0c0"));
        gc.setFont(Font.font(10));
        
        double y = canvas.getHeight() - 15;
        gc.fillText("Blue Sphere = U-235 | Gray Sphere = Neutron | Yellow Glow = Energy | Orange = Fragments", 10, y);
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-background-color: " + color
                + "; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 12 18; -fx-min-width: 100px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 1);");
    }
}
