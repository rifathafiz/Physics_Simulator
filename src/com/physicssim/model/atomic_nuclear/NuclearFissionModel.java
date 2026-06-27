package com.physicssim.model.atomic_nuclear;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NuclearFissionModel {

    private static final double NEUTRON_SPEED = 200.0;
    private static final double COLLISION_RADIUS = 25.0;
    private static final double ENERGY_DURATION = 0.5;
    private static final double ENERGY_RELEASED_PER_FISSION = 200.0; // MeV

    private int uraniumCount = 30;
    private double neutronSpeedMultiplier = 1.0;

    private int fissionEvents = 0;
    private int totalNeutronsProduced = 0;
    private double totalEnergyReleased = 0.0;
    private boolean chainReactionActive = false;

    private final List<UraniumNucleus> uraniumNuclei = new ArrayList<>();
    private final List<Neutron> neutrons = new ArrayList<>();
    private final List<EnergyBurst> energyBursts = new ArrayList<>();
    private final List<FissionFragment> fissionFragments = new ArrayList<>();
    private final List<RoomBlast> roomBlasts = new ArrayList<>();
    private final Random random = new Random();

    public NuclearFissionModel() {
        initializeUraniumNuclei();
    }

    /**
     * Initialize uranium nuclei randomly distributed in the reactor chamber
     */
    public void initializeUraniumNuclei() {
        uraniumNuclei.clear();
        for (int i = 0; i < uraniumCount; i++) {
            double x = 100 + random.nextDouble() * 400;
            double y = 80 + random.nextDouble() * 240;
            uraniumNuclei.add(new UraniumNucleus(x, y));
        }
    }

    /**
     * Fire an initial neutron from the left side
     */
    public void fireNeutron() {
        double targetY = 200 + random.nextDouble() * 100;
        Neutron neutron = new Neutron(0, targetY, neutronSpeedMultiplier * NEUTRON_SPEED, 0);
        neutrons.add(neutron);
        totalNeutronsProduced++;
        chainReactionActive = true;
    }

    /**
     * Update simulation state
     */
    public void update(double deltaTime) {
        // Update neutrons
        List<Neutron> neutronsToRemove = new ArrayList<>();
        for (Neutron neutron : neutrons) {
            neutron.update(deltaTime);

            // Check collision with uranium nuclei
            if (!neutron.hasCollided()) {
                for (UraniumNucleus nucleus : uraniumNuclei) {
                    if (!nucleus.isFissioned() && !nucleus.isVibrating()) {
                        double dx = neutron.getX() - nucleus.getX();
                        double dy = neutron.getY() - nucleus.getY();
                        double distance = Math.sqrt(dx * dx + dy * dy);

                        if (distance < COLLISION_RADIUS) {
                            // Trigger fission
                            triggerFission(nucleus, neutron);
                            neutron.setCollided(true);
                            neutronsToRemove.add(neutron);
                            break;
                        }
                    }
                }
            }

            // Remove neutrons out of bounds
            if (neutron.getX() > 650 || neutron.getX() < -50 ||
                neutron.getY() > 450 || neutron.getY() < -50) {
                neutronsToRemove.add(neutron);
            }
        }
        neutrons.removeAll(neutronsToRemove);

        // Update energy bursts
        List<EnergyBurst> energyToRemove = new ArrayList<>();
        for (EnergyBurst energy : energyBursts) {
            energy.update(deltaTime);
            if (energy.isComplete()) {
                energyToRemove.add(energy);
            }
        }
        energyBursts.removeAll(energyToRemove);

        // Update fission fragments
        List<FissionFragment> fragmentsToRemove = new ArrayList<>();
        for (FissionFragment fragment : fissionFragments) {
            fragment.update(deltaTime);
            if (fragment.isOutOfBounds()) {
                fragmentsToRemove.add(fragment);
            }
        }
        fissionFragments.removeAll(fragmentsToRemove);

        // Update room blasts
        List<RoomBlast> blastsToRemove = new ArrayList<>();
        for (RoomBlast blast : roomBlasts) {
            blast.update(deltaTime);
            if (blast.isComplete()) {
                blastsToRemove.add(blast);
            }
        }
        roomBlasts.removeAll(blastsToRemove);

        // Update uranium nuclei animations
        for (UraniumNucleus nucleus : uraniumNuclei) {
            nucleus.update(deltaTime);
        }

        // Check if chain reaction is still active
        boolean wasActive = chainReactionActive;
        chainReactionActive = !neutrons.isEmpty() && !uraniumNuclei.stream()
                .allMatch(UraniumNucleus::isFissioned);

        // Trigger room blast if chain reaction just ended with high energy
        if (wasActive && !chainReactionActive && totalEnergyReleased > 2000.0) {
            roomBlasts.add(new RoomBlast());
        }
    }

    /**
     * Trigger fission of a uranium nucleus
     */
    private void triggerFission(UraniumNucleus nucleus, Neutron triggeringNeutron) {
        nucleus.startVibration();
        nucleus.fission();

        fissionEvents++;
        totalEnergyReleased += ENERGY_RELEASED_PER_FISSION;

        // Create energy burst
        energyBursts.add(new EnergyBurst(nucleus.getX(), nucleus.getY()));

        // Emit 2-3 neutrons in random directions
        int numNeutrons = 2 + random.nextInt(2);
        for (int i = 0; i < numNeutrons; i++) {
            double angle = random.nextDouble() * 2 * Math.PI;
            double speed = neutronSpeedMultiplier * NEUTRON_SPEED;
            Neutron neutron = new Neutron(
                nucleus.getX(),
                nucleus.getY(),
                speed * Math.cos(angle),
                speed * Math.sin(angle)
            );
            neutrons.add(neutron);
            totalNeutronsProduced++;
        }

        // Create two fission fragments moving in opposite directions
        double angle = random.nextDouble() * 2 * Math.PI;
        double fragmentSpeed = 80.0;
        fissionFragments.add(new FissionFragment(
            nucleus.getX(), nucleus.getY(),
            fragmentSpeed * Math.cos(angle),
            fragmentSpeed * Math.sin(angle)
        ));
        fissionFragments.add(new FissionFragment(
            nucleus.getX(), nucleus.getY(),
            fragmentSpeed * Math.cos(angle + Math.PI),
            fragmentSpeed * Math.sin(angle + Math.PI)
        ));
    }

    /**
     * Reset simulation to initial state
     */
    public void reset() {
        fissionEvents = 0;
        totalNeutronsProduced = 0;
        totalEnergyReleased = 0.0;
        chainReactionActive = false;
        neutrons.clear();
        energyBursts.clear();
        fissionFragments.clear();
        roomBlasts.clear();
        initializeUraniumNuclei();
    }

    // Getters and Setters
    public int getUraniumCount() {
        return uraniumCount;
    }

    public void setUraniumCount(int count) {
        this.uraniumCount = Math.max(10, Math.min(50, count));
        initializeUraniumNuclei();
    }

    public double getNeutronSpeedMultiplier() {
        return neutronSpeedMultiplier;
    }

    public void setNeutronSpeedMultiplier(double multiplier) {
        this.neutronSpeedMultiplier = Math.max(0.5, Math.min(3.0, multiplier));
    }

    public int getFissionEvents() {
        return fissionEvents;
    }

    public int getTotalNeutronsProduced() {
        return totalNeutronsProduced;
    }

    public double getTotalEnergyReleased() {
        return totalEnergyReleased;
    }

    public int getActiveNeutrons() {
        return neutrons.size();
    }

    public int getUraniumRemaining() {
        return (int) uraniumNuclei.stream().filter(n -> !n.isFissioned()).count();
    }

    public boolean isChainReactionActive() {
        return chainReactionActive;
    }

    public List<UraniumNucleus> getUraniumNuclei() {
        return uraniumNuclei;
    }

    public List<Neutron> getNeutrons() {
        return neutrons;
    }

    public List<EnergyBurst> getEnergyBursts() {
        return energyBursts;
    }

    public List<FissionFragment> getFissionFragments() {
        return fissionFragments;
    }

    public List<RoomBlast> getRoomBlasts() {
        return roomBlasts;
    }

    /**
     * Uranium-235 nucleus class
     */
    public static class UraniumNucleus {
        private final double x;
        private final double y;
        private boolean fissioned;
        private boolean vibrating;
        private double vibrationTime;
        private double vibrationPhase;

        public UraniumNucleus(double x, double y) {
            this.x = x;
            this.y = y;
            this.fissioned = false;
            this.vibrating = false;
            this.vibrationTime = 0.0;
            this.vibrationPhase = 0.0;
        }

        public void update(double deltaTime) {
            if (vibrating) {
                vibrationTime += deltaTime;
                vibrationPhase += deltaTime * 80.0; // Much faster vibration!
                if (vibrationTime >= 0.4) { // Slightly longer vibration duration
                    vibrating = false;
                }
            }
        }

        public void startVibration() {
            this.vibrating = true;
            this.vibrationTime = 0.0;
            this.vibrationPhase = 0.0;
        }

        public void fission() {
            this.fissioned = true;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public boolean isFissioned() {
            return fissioned;
        }

        public boolean isVibrating() {
            return vibrating;
        }

        public double getVibrationOffset() {
            if (vibrating) {
                return Math.sin(vibrationPhase) * 8.0; // Much stronger vibration!
            }
            return 0.0;
        }
    }

    /**
     * Neutron class
     */
    public static class Neutron {
        private double x;
        private double y;
        private double vx;
        private double vy;
        private boolean collided;
        private final List<double[]> trail = new ArrayList<>();
        private final String name;
        private static int neutronCounter = 0;

        public Neutron(double x, double y, double vx, double vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.collided = false;
            this.name = "n" + (++neutronCounter);
        }

        public void update(double deltaTime) {
            // Store trail position
            trail.add(new double[]{x, y});
            if (trail.size() > 10) {
                trail.remove(0);
            }

            x += vx * deltaTime;
            y += vy * deltaTime;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public boolean hasCollided() {
            return collided;
        }

        public void setCollided(boolean collided) {
            this.collided = collided;
        }

        public List<double[]> getTrail() {
            return trail;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * Energy burst class
     */
    public static class EnergyBurst {
        private final double x;
        private final double y;
        private double elapsed;
        private final double duration;

        public EnergyBurst(double x, double y) {
            this.x = x;
            this.y = y;
            this.elapsed = 0.0;
            this.duration = ENERGY_DURATION;
        }

        public void update(double deltaTime) {
            elapsed += deltaTime;
        }

        public boolean isComplete() {
            return elapsed >= duration;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getProgress() {
            return elapsed / duration;
        }

        public double getAlpha() {
            return 1.0 - getProgress();
        }

        public double getRadius() {
            return 20 + getProgress() * 40;
        }
    }

    /**
     * Fission fragment class
     */
    public static class FissionFragment {
        private double x;
        private double y;
        private double vx;
        private double vy;

        public FissionFragment(double x, double y, double vx, double vy) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
        }

        public void update(double deltaTime) {
            x += vx * deltaTime;
            y += vy * deltaTime;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public boolean isOutOfBounds() {
            return x < 0 || x > 600 || y < 0 || y > 400;
        }
    }

    /**
     * Room blast effect class
     */
    public static class RoomBlast {
        private double elapsed;
        private final double duration = 2.5; // Longer duration for more powerful blast

        public RoomBlast() {
            this.elapsed = 0.0;
        }

        public void update(double deltaTime) {
            elapsed += deltaTime;
        }

        public double getProgress() {
            return elapsed / duration;
        }

        public boolean isComplete() {
            return elapsed >= duration;
        }
    }
}
