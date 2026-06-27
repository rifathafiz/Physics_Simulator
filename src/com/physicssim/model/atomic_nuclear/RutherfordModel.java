package com.physicssim.model.atomic_nuclear;

import java.util.ArrayList;
import java.util.List;

public class RutherfordModel {

    public static final double FOIL_X = 300;
    public static final double FOIL_Y = 200;
    public static final double FOIL_WIDTH = 10;
    public static final double FOIL_HEIGHT = 200;

    public enum InteractionType {
        TRANSMITTED, DEFLECTED, REFLECTED
    }

    public static class AlphaParticle {
        private double x;
        private double y;
        private double vx;
        private double vy;
        private boolean interacted;
        private InteractionType interactionType;
        private final List<double[]> trail;

        public AlphaParticle(double x, double y, double speed) {
            this.x = x;
            this.y = y;
            this.vx = speed;
            this.vy = 0;
            this.interacted = false;
            this.interactionType = InteractionType.TRANSMITTED;
            this.trail = new ArrayList<>();
        }

        public void update(double deltaTime) {
            trail.add(new double[]{x, y});
            if (trail.size() > 50) {
                trail.remove(0);
            }

            x += vx * deltaTime * 100;
            y += vy * deltaTime * 100;
        }

        public void deflect(double angleChange) {
            double speed = Math.sqrt(vx * vx + vy * vy);
            double currentAngle = Math.atan2(vy, vx);
            double newAngle = currentAngle + angleChange;
            vx = speed * Math.cos(newAngle);
            vy = speed * Math.sin(newAngle);
        }

        public double getX() { return x; }
        public double getY() { return y; }
        public boolean hasInteracted() { return interacted; }
        public void setInteracted(boolean interacted) { this.interacted = interacted; }
        public InteractionType getInteractionType() { return interactionType; }
        public void setInteractionType(InteractionType interactionType) { this.interactionType = interactionType; }
        public List<double[]> getTrail() { return trail; }
    }

    private int transmittedCount = 0;
    private int deflectedCount = 0;
    private int reflectedCount = 0;
    private double speed = 1.0;
    private final List<AlphaParticle> particles = new ArrayList<>();

    public RutherfordModel() {}

    public int getTransmittedCount() { return transmittedCount; }
    public int getDeflectedCount() { return deflectedCount; }
    public int getReflectedCount() { return reflectedCount; }
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = Math.max(0.5, Math.min(3.0, speed)); }
    public List<AlphaParticle> getParticles() { return particles; }

    public void spawnParticle() {
        double startY = FOIL_Y - FOIL_HEIGHT / 2 + Math.random() * FOIL_HEIGHT;
        particles.add(new AlphaParticle(50, startY, speed));
    }

    public void updateParticles(double deltaTime) {
        List<AlphaParticle> toRemove = new ArrayList<>();

        for (AlphaParticle particle : particles) {
            particle.update(deltaTime);

            if (!particle.hasInteracted() && particle.getX() >= FOIL_X && particle.getX() <= FOIL_X + FOIL_WIDTH) {
                determineInteraction(particle);
            }

            if (particle.getX() > 600 || particle.getX() < 0 || particle.getY() < 0 || particle.getY() > 400) {
                toRemove.add(particle);
            }
        }

        particles.removeAll(toRemove);
    }

    private void determineInteraction(AlphaParticle particle) {
        double rand = Math.random();
        double impactParameter = Math.abs(particle.getY() - FOIL_Y);
        double centerProximity = 1.0 - (impactParameter / (FOIL_HEIGHT / 2));

        if (rand < 0.75 + centerProximity * 0.1) {
            transmittedCount++;
            particle.setInteractionType(InteractionType.TRANSMITTED);
        } else if (rand < 0.92) {
            deflectedCount++;
            particle.setInteractionType(InteractionType.DEFLECTED);
            double deflectionAngle = (0.3 + centerProximity * 0.8) * (Math.random() > 0.5 ? 1 : -1);
            particle.deflect(deflectionAngle);
        } else {
            reflectedCount++;
            particle.setInteractionType(InteractionType.REFLECTED);
            double reflectionAngle = Math.PI + (Math.random() - 0.5) * 0.8;
            particle.deflect(reflectionAngle);
        }

        particle.setInteracted(true);
    }

    public void reset() {
        transmittedCount = 0;
        deflectedCount = 0;
        reflectedCount = 0;
        particles.clear();
    }
}
