@echo off
setlocal

:: Set the JavaFX SDK path
set JAVAFX_PATH=lib/javafx/lib

:: Create output directory
if not exist bin mkdir bin

:: Compile the application source tree
javac ^
    --module-path "%JAVAFX_PATH%" ^
    --add-modules javafx.controls,javafx.graphics ^
    -d bin ^
    src/com/physicssim/app/*.java ^
    src/com/physicssim/components/*.java ^
    src/com/physicssim/model/*.java ^
    src/com/physicssim/model/electricity/*.java ^
    src/com/physicssim/model/atomic_nuclear/*.java ^
    src/com/physicssim/theme/*.java ^
    src/com/physicssim/views/*.java ^
    src/com/physicssim/navigation/*.java ^
    src/com/physicssim/features/pendulum/*.java ^
    src/com/physicssim/features/mechanics/*.java ^
    src/com/physicssim/features/simulations/*.java ^
    src/com/physicssim/features/electricity/*.java ^
    src/com/physicssim/features/kinematics/*.java ^
    src/com/physicssim/features/atomic_nuclear/*.java

if %errorlevel% neq 0 (
    echo Compilation failed.
    exit /b 1
)

:: Copy resources into bin so CSS and assets are available at runtime
if exist src\resources (
    xcopy src\resources\* bin\ /E /I /Y >nul
)

:: Run the Java program
java ^
    --module-path "%JAVAFX_PATH%" ^
    --add-modules javafx.controls,javafx.graphics ^
    -cp bin ^
    com.physicssim.app.PhysicsSimulatorApp

endlocal