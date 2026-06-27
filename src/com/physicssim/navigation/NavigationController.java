package com.physicssim.navigation;

import com.physicssim.features.kinematics.KinematicsView;
import com.physicssim.features.atomic_nuclear.AtomicNuclearView;
import com.physicssim.views.AboutView;
import com.physicssim.views.HelpView;
import com.physicssim.views.HomeView;
import com.physicssim.views.SimulationsView;
import java.util.EnumMap;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class NavigationController {

    private final StackPane contentHost = new StackPane();
    private final Map<ViewType, Node> views = new EnumMap<>(ViewType.class);
    private ViewType currentView = ViewType.HOME;

    public NavigationController() {
        contentHost.getChildren().add(getView(ViewType.HOME));
    }

    public StackPane getContentHost() {
        return contentHost;
    }

    public ViewType getCurrentView() {
        return currentView;
    }

    public void navigateTo(ViewType viewType) {
        currentView = viewType;
        contentHost.getChildren().setAll(getView(viewType));
    }

    private Node getView(ViewType viewType) {
        return views.computeIfAbsent(viewType, this::createView);
    }

    private Node createView(ViewType viewType) {
        return switch (viewType) {
            case HOME -> new HomeView();
            case SIMULATIONS -> new SimulationsView();
            case KINEMATICS -> new KinematicsView();
            case ATOMIC_NUCLEAR -> new AtomicNuclearView();
            case ABOUT -> new AboutView();
            case HELP -> new HelpView();
        };
    }
}
