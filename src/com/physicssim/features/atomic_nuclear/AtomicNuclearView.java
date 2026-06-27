package com.physicssim.features.atomic_nuclear;

import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class AtomicNuclearView extends BorderPane {

    private final BohrModelView bohrView = new BohrModelView();
    private final RutherfordView rutherfordView = new RutherfordView();
    private final ElectronTransitionView electronTransitionView = new ElectronTransitionView();
    private final HalfLifeView halfLifeView = new HalfLifeView();
    private final NuclearFissionView fissionView = new NuclearFissionView();

    private final Pane contentHost = new Pane();

    public AtomicNuclearView() {
        setPadding(new Insets(18));
        setStyle("-fx-background-color: transparent;");

        Label title = new Label("⚛️  Atomic & Nuclear Physics  ⚛️");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: 900; -fx-text-fill: linear-gradient(from 0% 0% to 100% 100%, #7dd3fc, #8b5cf6); -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 2);");
        title.setAlignment(Pos.CENTER);

        Label description = new Label("Explore fundamental concepts of atomic and nuclear physics through interactive simulations.");
        description.setStyle("-fx-font-size: 16px; -fx-text-fill: #64748b; -fx-wrap-text: true;");
        description.setPrefWidth(800);
        description.setAlignment(Pos.CENTER);

        Button bohrBtn = new Button("Bohr's Atomic Model");
        bohrBtn.setOnAction(event -> showSimulation(bohrView));

        Button rutherfordBtn = new Button("Rutherford Experiment");
        rutherfordBtn.setOnAction(event -> showSimulation(rutherfordView));

        Button electronTransitionBtn = new Button("Electron Transition");
        electronTransitionBtn.setOnAction(event -> showSimulation(electronTransitionView));

        Button halfLifeBtn = new Button("Half-Life Simulator");
        halfLifeBtn.setOnAction(event -> showSimulation(halfLifeView));

        Button fissionBtn = new Button("Nuclear Fission");
        fissionBtn.setOnAction(event -> showSimulation(fissionView));

        Button backBtn = new Button("Back to Module List");
        backBtn.setStyle("-fx-font-size: 14px; -fx-font-weight: 700; -fx-background-color: #e2e8f0; -fx-text-fill: #1e293b; -fx-border-color: #cbd5e1; -fx-border-radius: 8; -fx-padding: 8 16;");

        HBox buttons = new HBox(12, backBtn, bohrBtn, rutherfordBtn, electronTransitionBtn, halfLifeBtn, fissionBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox header = new VBox(12, title, description, buttons);
        header.setPadding(new Insets(16, 0, 24, 0));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 100% 0%, #0f172a, #020617); -fx-background-radius: 12; -fx-padding: 20;");

        setTop(header);
        setCenter(contentHost);
        showSimulation(bohrView);
    }

    private void showSimulation(Pane view) {
        contentHost.getChildren().clear();
        contentHost.getChildren().add(view);
    }
}
