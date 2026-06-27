package com.physicssim.model.atomic_nuclear;

import java.util.ArrayList;
import java.util.List;

public class BohrModel {

    public static class ShellElectron {
        private final int shellIndex;
        private double angle;
        private final double angularSpeed;

        public ShellElectron(int shellIndex, double angle, double angularSpeed) {
            this.shellIndex = shellIndex;
            this.angle = angle;
            this.angularSpeed = angularSpeed;
        }

        public void update(double deltaTime, double speedMultiplier) {
            angle += angularSpeed * deltaTime * speedMultiplier;
        }

        public int getShellIndex() { return shellIndex; }
        public double getAngle() { return angle; }
    }

    private static final double[] SHELL_RADII = {60, 95, 130, 165};
    private static final String[] SHELL_NAMES = {"K", "L", "M", "N"};

    private int numShells = 4;
    private int totalElectrons = 10;
    private double speedMultiplier = 1.0;
    private final List<ShellElectron> electrons = new ArrayList<>();

    public BohrModel() {
        initializeElectrons();
    }

    private void initializeElectrons() {
        electrons.clear();
        int electronsPerShell[] = {2, 8, 18, 32};
        int remaining = totalElectrons;

        for (int shell = 0; shell < numShells && remaining > 0; shell++) {
            int maxForShell = electronsPerShell[shell];
            int count = Math.min(maxForShell, remaining);
            for (int i = 0; i < count; i++) {
                double angle = (2 * Math.PI / count) * i;
                double speed = 2.0 + shell * 0.5;
                electrons.add(new ShellElectron(shell, angle, speed));
            }
            remaining -= count;
        }
    }

    public int getNumShells() { return numShells; }
    public void setNumShells(int numShells) {
        this.numShells = Math.max(1, Math.min(4, numShells));
        initializeElectrons();
    }

    public int getTotalElectrons() { return totalElectrons; }
    public void setTotalElectrons(int totalElectrons) {
        this.totalElectrons = Math.max(1, Math.min(54, totalElectrons));
        initializeElectrons();
    }

    public double getSpeedMultiplier() { return speedMultiplier; }
    public void setSpeedMultiplier(double speedMultiplier) {
        this.speedMultiplier = Math.max(0.1, Math.min(5.0, speedMultiplier));
    }

    public double getShellRadius(int index) {
        if (index >= 0 && index < SHELL_RADII.length) {
            return SHELL_RADII[index];
        }
        return SHELL_RADII[0];
    }

    public String getShellName(int index) {
        if (index >= 0 && index < SHELL_NAMES.length) {
            return SHELL_NAMES[index];
        }
        return "";
    }

    public List<ShellElectron> getElectrons() { return electrons; }

    public void update(double deltaTime) {
        for (ShellElectron e : electrons) {
            e.update(deltaTime, speedMultiplier);
        }
    }

    public void resetTime() {
        initializeElectrons();
    }
}
