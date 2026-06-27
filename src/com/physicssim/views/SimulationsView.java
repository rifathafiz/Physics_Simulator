package com.physicssim.views;

import com.physicssim.features.mechanics.MechanicsElasticityView;
import com.physicssim.features.kinematics.KinematicsView;
import com.physicssim.components.PhysicsButton;
import com.physicssim.features.pendulum.PendulumSimulationView;
import com.physicssim.features.electricity.CurrentElectricityView;
import com.physicssim.features.atomic_nuclear.AtomicNuclearView;
import com.physicssim.features.simulations.SimulationFeatureCard;
import com.physicssim.model.SimulationCatalog;
import com.physicssim.model.SimulationItem;
import com.physicssim.model.SimulationType;
import com.physicssim.theme.AppTheme;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SimulationsView extends BorderPane {

    private final BorderPane contentHost = new BorderPane();
    private final VBox layout = new VBox(26);
    private final VBox header = new VBox(12);

    public SimulationsView() {
        setBackground(AppTheme.pageBackground());
        setCenter(buildLayout());
        showCatalog();
    }

    private Node buildLayout() {
        Label title = new Label("Simulations");
        title.setFont(AppTheme.heroFont());
        title.setTextFill(AppTheme.TEXT_PRIMARY);

        Label subtitle = new Label("Open a simulation module from here. The home page stays clean while the actual labs live in this section.");
        subtitle.setFont(AppTheme.subtitleFont());
        subtitle.setTextFill(AppTheme.TEXT_SECONDARY);
        subtitle.setWrapText(true);

        header.getChildren().setAll(title, subtitle);

        contentHost.setBackground(new Background(new BackgroundFill(AppTheme.SURFACE, CornerRadii.EMPTY, Insets.EMPTY)));
        VBox.setVgrow(contentHost, Priority.ALWAYS);

        layout.getChildren().setAll(header, contentHost);
        layout.setPadding(new Insets(32, 28, 32, 28));
        layout.setFillWidth(true);
        return layout;
    }

    private void showCatalog() {
        HBox cards = new HBox(24);
        cards.setAlignment(Pos.CENTER_LEFT);

        for (SimulationItem item : SimulationCatalog.homeItems()) {
            cards.getChildren().add(new SimulationFeatureCard(item, () -> openSimulation(item)));
        }

        VBox catalog = new VBox(24, cards);
        catalog.setPadding(new Insets(12));
        contentHost.setCenter(catalog);
    }

    private void openSimulation(SimulationItem item) {
        if (item.getType() == SimulationType.PENDULUM) {
            hideSectionHeader();
            contentHost.setTop(null);
            contentHost.setCenter(buildSimulationPage(new PendulumSimulationView()));
            return;
        }

        if (item.getType() == SimulationType.MECHANICS) {
            hideSectionHeader();
            contentHost.setTop(null);
            contentHost.setCenter(buildSimulationPage(new MechanicsElasticityView()));
            return;
        }

        if (item.getType() == SimulationType.KINEMATICS) {
            hideSectionHeader();
            contentHost.setTop(null);
            contentHost.setCenter(buildSimulationPage(new KinematicsView()));
            return;
        }

        if (item.getType() == SimulationType.ELECTRICITY) {
            hideSectionHeader();
            contentHost.setTop(null);
            contentHost.setCenter(buildSimulationPage(new CurrentElectricityView()));
            return;
        }

        if (item.getType() == SimulationType.ATOMIC_NUCLEAR) {
            hideSectionHeader();
            contentHost.setTop(null);
            contentHost.setCenter(buildSimulationPage(new AtomicNuclearView()));
            return;
        }

        showSectionHeader();
        Label placeholder = new Label(item.getTitle().replace("\n", " ") + " module is coming next.");
        placeholder.setFont(AppTheme.subtitleFont());
        placeholder.setTextFill(AppTheme.TEXT_SECONDARY);

        BorderPane placeholderPane = new BorderPane(placeholder);
        placeholderPane.setPadding(new Insets(50));
        contentHost.setTop(createBackBar());
        contentHost.setCenter(placeholderPane);
    }

    private ScrollPane buildSimulationPage(Node content) {
        VBox pageContent = new VBox(18, createBackBar(), content);
        pageContent.setFillWidth(true);
        pageContent.setPadding(new Insets(0, 12, 12, 12));

        ScrollPane scrollPane = new ScrollPane(pageContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");
        return scrollPane;
    }

    private HBox createBackBar() {
        PhysicsButton backButton = new PhysicsButton("Back to all simulations", PhysicsButton.Style.TEXT_ONLY);
        backButton.setFont(AppTheme.cardNumberFont());
        backButton.setTextFill(AppTheme.SURFACE);
        backButton.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.web("#3157d5"), new CornerRadii(12), Insets.EMPTY)));
        backButton.setPadding(new Insets(10, 16, 10, 16));
        backButton.setOnAction(event -> {
            contentHost.setTop(null);
            showSectionHeader();
            showCatalog();
        });

        HBox bar = new HBox(backButton);
        bar.setPadding(new Insets(0, 0, 18, 0));
        return bar;
    }

    private void hideSectionHeader() {
        layout.getChildren().setAll(contentHost);
    }

    private void showSectionHeader() {
        layout.getChildren().setAll(header, contentHost);
    }
}
