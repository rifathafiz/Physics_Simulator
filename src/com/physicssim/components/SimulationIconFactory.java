package com.physicssim.components;

import com.physicssim.model.SimulationType;
import com.physicssim.theme.AppTheme;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public final class SimulationIconFactory {

    private SimulationIconFactory() {
    }

    public static StackPane create(SimulationType type) {
        return switch (type) {
            case PENDULUM -> createPendulumIcon();
            case MECHANICS -> createMechanicsIcon();
            case KINEMATICS -> createKinematicsIcon();
            case ORBIT -> createOrbitIcon();
            case ANALYTICS -> createChartIcon();
            case ELECTRICITY -> createElectricityIcon();
            case ATOMIC_NUCLEAR -> createAtomicNuclearIcon();
            default -> createPendulumIcon();
        };
    }

    private static StackPane createPendulumIcon() {
        Line topBar = new Line(25, 18, 95, 18);
        topBar.setStroke(Color.web("#2b3440"));
        topBar.setStrokeWidth(5);

        Line centerString = new Line(60, 18, 60, 72);
        centerString.setStroke(Color.web("#2b3440"));
        centerString.setStrokeWidth(3.5);

        Line leftString = new Line(60, 18, 36, 64);
        leftString.setStroke(Color.web("#2b3440"));
        leftString.setStrokeWidth(3.5);

        Line rightString = new Line(60, 18, 84, 64);
        rightString.setStroke(Color.web("#bfc7cf"));
        rightString.setStrokeWidth(3.5);

        Circle leftBall = new Circle(36, 64, 8, Color.web("#38424d"));
        Circle centerBall = new Circle(60, 72, 8, Color.web("#38424d"));
        Circle rightBall = new Circle(84, 64, 8, AppTheme.ICON_LIGHT);

        return new StackPane(new Group(topBar, centerString, leftString, rightString, leftBall, centerBall, rightBall));
    }

    private static StackPane createMechanicsIcon() {
        Circle ball = new Circle(68, 48, 22, AppTheme.ICON_MID);
        Line ground = new Line(28, 74, 96, 74);
        ground.setStroke(Color.web("#2b3440"));
        ground.setStrokeWidth(4);

        Arc motion1 = new Arc(50, 48, 20, 20, 112, 46);
        motion1.setFill(Color.TRANSPARENT);
        motion1.setStroke(AppTheme.ICON_MID);
        motion1.setStrokeWidth(4);

        Arc motion2 = new Arc(42, 48, 30, 30, 115, 34);
        motion2.setFill(Color.TRANSPARENT);
        motion2.setStroke(AppTheme.ICON_MID);
        motion2.setStrokeWidth(3);

        Line trail1 = new Line(20, 42, 30, 42);
        trail1.setStroke(AppTheme.ICON_MID);
        trail1.setStrokeWidth(4);

        Line trail2 = new Line(18, 54, 26, 54);
        trail2.setStroke(AppTheme.ICON_MID);
        trail2.setStrokeWidth(4);

        return new StackPane(new Group(ball, ground, motion1, motion2, trail1, trail2));
    }

    private static StackPane createOrbitIcon() {
        Circle planet = new Circle(60, 52, 22, AppTheme.ICON_MID);
        Arc ring = new Arc(60, 52, 40, 22, 0, 360);
        ring.setFill(Color.TRANSPARENT);
        ring.setStroke(AppTheme.ICON_MID);
        ring.setStrokeWidth(4);
        ring.setRotate(-24);

        Circle moon = new Circle(92, 42, 5.5, AppTheme.ICON_MID);
        moon.setTranslateY(-2);

        return new StackPane(new Group(ring, planet, moon));
    }

    private static StackPane createChartIcon() {
        Line yAxis = new Line(28, 20, 28, 78);
        yAxis.setStroke(AppTheme.ICON_DARK);
        yAxis.setStrokeWidth(4);

        Line xAxis = new Line(28, 78, 98, 78);
        xAxis.setStroke(AppTheme.ICON_DARK);
        xAxis.setStrokeWidth(4);

        Line trend = new Line(38, 62, 52, 44);
        trend.setStroke(AppTheme.ICON_DARK);
        trend.setStrokeWidth(4);

        Line trend2 = new Line(52, 44, 66, 58);
        trend2.setStroke(AppTheme.ICON_DARK);
        trend2.setStrokeWidth(4);

        Line trend3 = new Line(66, 58, 82, 32);
        trend3.setStroke(AppTheme.ICON_DARK);
        trend3.setStrokeWidth(4);

        Circle p1 = new Circle(38, 62, 4.5, AppTheme.ICON_DARK);
        Circle p2 = new Circle(52, 44, 4.5, AppTheme.ICON_DARK);
        Circle p3 = new Circle(66, 58, 4.5, AppTheme.ICON_DARK);
        Circle p4 = new Circle(82, 32, 4.5, AppTheme.ICON_DARK);

        return new StackPane(new Group(yAxis, xAxis, trend, trend2, trend3, p1, p2, p3, p4));
    }

    private static StackPane createElectricityIcon() {
        // simple battery + resistor icon
        Line wire1 = new Line(18, 48, 36, 48);
        wire1.setStroke(AppTheme.ICON_DARK);
        wire1.setStrokeWidth(3);

        // battery plates
        Line plate1 = new Line(40, 36, 40, 60);
        plate1.setStroke(AppTheme.ICON_MID);
        plate1.setStrokeWidth(4);

        Line plate2 = new Line(50, 42, 50, 54);
        plate2.setStroke(AppTheme.ICON_MID);
        plate2.setStrokeWidth(2);

        // resistor as zig-zag (simplified)
        Line r1 = new Line(56, 48, 64, 40);
        r1.setStroke(AppTheme.ICON_MID);
        r1.setStrokeWidth(3);
        Line r2 = new Line(64, 40, 72, 56);
        r2.setStroke(AppTheme.ICON_MID);
        r2.setStrokeWidth(3);
        Line r3 = new Line(72, 56, 80, 40);
        r3.setStroke(AppTheme.ICON_MID);
        r3.setStrokeWidth(3);

        Line wire2 = new Line(80, 48, 96, 48);
        wire2.setStroke(AppTheme.ICON_DARK);
        wire2.setStrokeWidth(3);

        Circle plus = new Circle(44, 28, 3, AppTheme.ICON_DARK);
        Circle minus = new Circle(44, 76, 3, AppTheme.ICON_LIGHT);

        return new StackPane(new Group(wire1, plate1, plate2, r1, r2, r3, wire2, plus, minus));
    }

    private static StackPane createKinematicsIcon() {
        // Kinematics icon: moving ball with velocity/acceleration arrows
        Circle ball = new Circle(50, 48, 12, AppTheme.ICON_MID);

        // Velocity arrow (right)
        Line velocityLine = new Line(62, 48, 88, 48);
        velocityLine.setStroke(AppTheme.ICON_DARK);
        velocityLine.setStrokeWidth(3);
        Line velocityArrow1 = new Line(88, 48, 80, 40);
        velocityArrow1.setStroke(AppTheme.ICON_DARK);
        velocityArrow1.setStrokeWidth(3);
        Line velocityArrow2 = new Line(88, 48, 80, 56);
        velocityArrow2.setStroke(AppTheme.ICON_DARK);
        velocityArrow2.setStrokeWidth(3);

        // Acceleration arrow (up-right)
        Line accelLine = new Line(50, 48, 70, 28);
        accelLine.setStroke(AppTheme.ICON_MID);
        accelLine.setStrokeWidth(2);
        Line accelArrow1 = new Line(70, 28, 62, 26);
        accelArrow1.setStroke(AppTheme.ICON_MID);
        accelArrow1.setStrokeWidth(2);
        Line accelArrow2 = new Line(70, 28, 68, 34);
        accelArrow2.setStroke(AppTheme.ICON_MID);
        accelArrow2.setStrokeWidth(2);

        // Ground line
        Line ground = new Line(18, 72, 102, 72);
        ground.setStroke(Color.web("#2b3440"));
        ground.setStrokeWidth(3);

        return new StackPane(new Group(ground, ball, velocityLine, velocityArrow1, velocityArrow2, accelLine, accelArrow1, accelArrow2));
    }

    private static StackPane createAtomicNuclearIcon() {
        // Atomic nucleus
        Circle nucleus = new Circle(60, 48, 12, Color.web("#dc2626"));

        // 3 electron orbits
        Arc orbit1 = new Arc(60, 48, 30, 30, 0, 360);
        orbit1.setFill(Color.TRANSPARENT);
        orbit1.setStroke(Color.web("#2b3440"));
        orbit1.setStrokeWidth(3);

        Arc orbit2 = new Arc(60, 48, 30, 30, 0, 360);
        orbit2.setFill(Color.TRANSPARENT);
        orbit2.setStroke(Color.web("#2b3440"));
        orbit2.setStrokeWidth(3);
        orbit2.setRotate(60);

        Arc orbit3 = new Arc(60, 48, 30, 30, 0, 360);
        orbit3.setFill(Color.TRANSPARENT);
        orbit3.setStroke(Color.web("#2b3440"));
        orbit3.setStrokeWidth(3);
        orbit3.setRotate(120);

        // Electrons
        Circle electron1 = new Circle(90, 48, 5, Color.web("#3b82f6"));
        Circle electron2 = new Circle(60, 18, 5, Color.web("#3b82f6"));
        Circle electron3 = new Circle(30, 48, 5, Color.web("#3b82f6"));

        return new StackPane(new Group(orbit1, orbit2, orbit3, nucleus, electron1, electron2, electron3));
    }
}
