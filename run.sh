#!/bin/bash
set -e

JAVAFX_PATH="lib/javafx_mac_binary/javafx-sdk-21.0.11/lib"

mkdir -p bin

javac \
    --module-path "$JAVAFX_PATH" \
    --add-modules javafx.controls,javafx.graphics \
    -d bin \
    src/com/physicssim/app/*.java \
    src/com/physicssim/components/*.java \
    src/com/physicssim/model/*.java \
    src/com/physicssim/model/electricity/*.java \
    src/com/physicssim/model/atomic_nuclear/*.java \
    src/com/physicssim/theme/*.java \
    src/com/physicssim/views/*.java \
    src/com/physicssim/navigation/*.java \
    src/com/physicssim/features/pendulum/*.java \
    src/com/physicssim/features/mechanics/*.java \
    src/com/physicssim/features/simulations/*.java \
    src/com/physicssim/features/electricity/*.java \
    src/com/physicssim/features/kinematics/*.java \
    src/com/physicssim/features/atomic_nuclear/*.java

# Copy resources
cp -R src/resources/* bin/

java \
    --module-path "$JAVAFX_PATH" \
    --add-modules javafx.controls,javafx.graphics \
    -cp bin \
    com.physicssim.app.PhysicsSimulatorApp