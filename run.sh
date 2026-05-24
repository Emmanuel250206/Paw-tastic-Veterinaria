#!/usr/bin/env bash
# Build and launch the Paw‑tastic JavaFX application using Maven.
# This avoids the "Module not found" error caused by running java directly.

# Exit on any error
enable -e

# Ensure we are in the project root
dir="$(cd "$(dirname "${BASH_SOURCE[0]}") && pwd)"
cd "$dir"

# Clean, compile and run the JavaFX application
mvn clean compile javafx:run
