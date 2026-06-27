package com.physicssim.model.atomic_nuclear;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HalfLifeModel {

    private int initialAtoms = 100;
    private double halfLife = 10.0;
    private double elapsedTime = 0.0;

    private final List<Atom> atoms = new ArrayList<>();
    private final Random random = new Random();

    public HalfLifeModel() {
        initializeAtoms();
    }

    /**
     * Initialize atoms randomly distributed in the container
     */
    public void initializeAtoms() {
        atoms.clear();
        for (int i = 0; i < initialAtoms; i++) {
            double x = 20 + random.nextDouble() * 360;
            double y = 20 + random.nextDouble() * 360;
            atoms.add(new Atom(x, y));
        }
    }

    /**
     * Calculate remaining atoms using half-life equation: N = N₀ × (1/2)^(t/T)
     */
    public int calculateRemainingAtoms() {
        double halfLivesPassed = elapsedTime / halfLife;
        double remainingFraction = Math.pow(0.5, halfLivesPassed);
        return (int) Math.round(initialAtoms * remainingFraction);
    }

    /**
     * Calculate decayed atoms
     */
    public int calculateDecayedAtoms() {
        return initialAtoms - calculateRemainingAtoms();
    }

    /**
     * Calculate number of half-lives passed
     */
    public double calculateHalfLivesPassed() {
        return elapsedTime / halfLife;
    }

    /**
     * Calculate percentage remaining
     */
    public double calculatePercentageRemaining() {
        if (initialAtoms == 0) return 0.0;
        return (calculateRemainingAtoms() * 100.0) / initialAtoms;
    }

    /**
     * Calculate percentage decayed
     */
    public double calculatePercentageDecayed() {
        if (initialAtoms == 0) return 0.0;
        return (calculateDecayedAtoms() * 100.0) / initialAtoms;
    }

    /**
     * Update atom states based on elapsed time
     * Randomly decay atoms to match the mathematical model
     */
    public void updateAtoms(double deltaTime) {
        int targetRemaining = calculateRemainingAtoms();
        int currentDecayed = (int) atoms.stream().filter(Atom::isDecayed).count();
        int currentRemaining = initialAtoms - currentDecayed;

        // If we have more atoms than target, decay some randomly
        if (currentRemaining > targetRemaining) {
            int toDecay = currentRemaining - targetRemaining;
            List<Atom> undecayed = new ArrayList<>();
            for (Atom atom : atoms) {
                if (!atom.isDecayed()) {
                    undecayed.add(atom);
                }
            }

            // Randomly select atoms to decay
            for (int i = 0; i < toDecay && !undecayed.isEmpty(); i++) {
                int index = random.nextInt(undecayed.size());
                undecayed.get(index).decay();
                undecayed.remove(index);
            }
        }

        // Update fade animation for decayed atoms
        for (Atom atom : atoms) {
            atom.updateFade(deltaTime);
        }
    }

    public int getInitialAtoms() {
        return initialAtoms;
    }

    public void setInitialAtoms(int initialAtoms) {
        if (initialAtoms > 0) {
            this.initialAtoms = initialAtoms;
            initializeAtoms();
        }
    }

    public double getHalfLife() {
        return halfLife;
    }

    public void setHalfLife(double halfLife) {
        if (halfLife > 0) {
            this.halfLife = halfLife;
        }
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(double elapsedTime) {
        this.elapsedTime = Math.max(0, elapsedTime);
        updateAtoms(0);
    }

    public void addElapsedTime(double deltaTime) {
        this.elapsedTime += deltaTime;
        updateAtoms(deltaTime);
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

    public void reset() {
        elapsedTime = 0.0;
        initializeAtoms();
    }

    /**
     * Atom class representing a single radioactive atom
     */
    public static class Atom {
        private final double x;
        private final double y;
        private boolean decayed;
        private double fadeProgress = 0.0;
        private static final double FADE_DURATION = 0.5;

        public Atom(double x, double y) {
            this.x = x;
            this.y = y;
            this.decayed = false;
        }

        public void decay() {
            this.decayed = true;
            this.fadeProgress = 0.0;
        }

        public void updateFade(double deltaTime) {
            if (decayed && fadeProgress < 1.0) {
                fadeProgress += deltaTime / FADE_DURATION;
                if (fadeProgress > 1.0) {
                    fadeProgress = 1.0;
                }
            }
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public boolean isDecayed() {
            return decayed;
        }

        public double getFadeProgress() {
            return fadeProgress;
        }

        public double getAlpha() {
            return 1.0 - fadeProgress;
        }
    }
}
