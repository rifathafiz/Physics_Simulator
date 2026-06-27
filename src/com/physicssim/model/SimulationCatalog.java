package com.physicssim.model;

import java.util.List;

public final class SimulationCatalog {

    private SimulationCatalog() {
    }

    public static List<SimulationItem> homeItems() {
        return List.of(
                new SimulationItem("(1)", "Pendulum\nDynamics", SimulationType.PENDULUM),
                new SimulationItem("(2)", "Mechanics &\nElasticity", SimulationType.MECHANICS),
                new SimulationItem("(3)", "Kinematics", SimulationType.KINEMATICS),
                new SimulationItem("(4)", "Orbital Gravity", SimulationType.ORBIT),
                new SimulationItem("(5)", "Data Analysis", SimulationType.ANALYTICS),
                new SimulationItem("(6)", "Electricity\nCurrent electricity", SimulationType.ELECTRICITY),
                new SimulationItem("(7)", "Atomic &\nNuclear Physics", SimulationType.ATOMIC_NUCLEAR));
    }
}
