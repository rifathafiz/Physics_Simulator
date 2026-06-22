#!/bin/bash
set -e

# Set JavaFX SDK path (macOS)
JAVAFX_PATH="lib/javafx_mac_binary/javafx-sdk-21.0.11/lib"

# Create bin folder if not exists
mkdir -p bin

# Compile the application source tree
javac \
    --module-path "$JAVAFX_PATH" \
    --add-modules javafx.controls,javafx.graphics \
    -d bin \
    src/com/physicssim/app/PhysicsSimulatorApp.java \
    src/com/physicssim/components/*.java \
    src/com/physicssim/model/*.java \
    src/com/physicssim/theme/*.java \
    src/com/physicssim/views/*.java

# Run only if compilation succeeds (because of set -e)
java \
    --module-path "$JAVAFX_PATH" \
    --add-modules javafx.controls,javafx.graphics \
    -cp bin \
    com.physicssim.app.PhysicsSimulatorApp