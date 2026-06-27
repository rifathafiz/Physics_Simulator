package com.physicssim.model.atomic_nuclear;

public class ElectronTransitionModel {

    private static final double[] ENERGY_LEVELS = {-13.6, -3.4, -1.5, -0.85};
    private static final double[] SHELL_RADII = {50, 90, 130, 170};
    private static final String[] SHELL_NAMES = {"K (n=1)", "L (n=2)", "M (n=3)", "N (n=4)"};

    private int initialLevel = 1;
    private int finalLevel = 2;
    private double electronAngle = 0.0;
    private double transitionProgress = 0.0;
    private boolean isTransitioning = false;
    private boolean showPhoton = false;
    private double photonAngle = 0.0;
    private double photonDistance = 0.0;
    private boolean isAbsorbing = false;
    private boolean photonProcessed = false;

    public ElectronTransitionModel() {}

    public int getInitialLevel() { return initialLevel; }
    public void setInitialLevel(int initialLevel) {
        if (!isTransitioning) {
            this.initialLevel = Math.max(1, Math.min(4, initialLevel));
        }
    }

    public int getFinalLevel() { return finalLevel; }
    public void setFinalLevel(int finalLevel) {
        if (!isTransitioning) {
            this.finalLevel = Math.max(1, Math.min(4, finalLevel));
        }
    }

    public double getElectronAngle() { return electronAngle; }
    public void advanceElectronAngle(double deltaTime) {
        electronAngle += deltaTime * 2.0;
    }

    public double getShellRadius(int level) {
        int index = level - 1;
        if (index >= 0 && index < SHELL_RADII.length) {
            return SHELL_RADII[index];
        }
        return SHELL_RADII[0];
    }

    public String getShellName(int level) {
        int index = level - 1;
        if (index >= 0 && index < SHELL_NAMES.length) {
            return SHELL_NAMES[index];
        }
        return "";
    }

    public double getEnergyLevel(int level) {
        int index = level - 1;
        if (index >= 0 && index < ENERGY_LEVELS.length) {
            return ENERGY_LEVELS[index];
        }
        return ENERGY_LEVELS[0];
    }

    public double getEnergyDifference() {
        return Math.abs(getEnergyLevel(finalLevel) - getEnergyLevel(initialLevel));
    }

    public boolean isAbsorbing() { return finalLevel > initialLevel; }
    public boolean isEmitting() { return finalLevel < initialLevel; }
    public boolean isTransitioning() { return isTransitioning; }
    public double getTransitionProgress() { return transitionProgress; }
    public boolean isShowingPhoton() { return showPhoton; }
    public double getPhotonAngle() { return photonAngle; }
    public double getPhotonDistance() { return photonDistance; }

    public void startTransition() {
        if (initialLevel != finalLevel && !isTransitioning) {
            isTransitioning = true;
            transitionProgress = 0.0;
            isAbsorbing = finalLevel > initialLevel;
            showPhoton = true;
            photonAngle = electronAngle;
            photonDistance = isAbsorbing ? 200.0 : 0.0;
            photonProcessed = false;
        }
    }

    public void updateTransition(double deltaTime) {
        if (isTransitioning) {
            if (showPhoton) {
                if (isAbsorbing) {
                    photonDistance -= deltaTime * 150;
                    if (photonDistance <= 0) {
                        photonDistance = 0;
                        showPhoton = false;
                        photonProcessed = true;
                    }
                } else {
                    photonDistance += deltaTime * 150;
                    if (photonDistance > 250) {
                        showPhoton = false;
                        photonProcessed = true;
                    }
                }
            }

            if (photonProcessed) {
                transitionProgress += deltaTime * 1.5;
                if (transitionProgress >= 1.0) {
                    transitionProgress = 1.0;
                    isTransitioning = false;
                    initialLevel = finalLevel;
                }
            }
        }
    }

    public void reset() {
        initialLevel = 1;
        finalLevel = 2;
        electronAngle = 0.0;
        transitionProgress = 0.0;
        isTransitioning = false;
        showPhoton = false;
        photonDistance = 0.0;
        photonProcessed = false;
    }

    public double getCurrentRadius() {
        if (!isTransitioning) {
            return getShellRadius(initialLevel);
        }

        double startRadius = getShellRadius(initialLevel);
        double endRadius = getShellRadius(finalLevel);
        return startRadius + (endRadius - startRadius) * transitionProgress;
    }
}
