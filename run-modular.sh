#!/usr/bin/env bash
# --------------------------------------------------------------
# run-modular.sh – Build the project and launch it using the Java module system.
# This avoids the "Module not found" error when invoking java directly.
# --------------------------------------------------------------

set -e  # exit on any error

# ------- 1. Ensure we are in the project root -------
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

# ------- 2. Compile and package dependencies -------
# Clean and compile the sources
mvn -q clean compile
# Copy all runtime dependencies to target/dependency (requires maven-dependency-plugin in pom)
mvn -q package

# ------- 3. Build the module path -------
# target/classes contains our own module
MODULE_PATH="target/classes"
# Append all jars in target/dependency
for jar in target/dependency/*.jar; do
  MODULE_PATH="$MODULE_PATH:$jar"
done

# ------- 4. Run the application -------
java \
  --module-path "$MODULE_PATH" \
  -m com.mycompany.aplicacion/com.mycompany.aplicacion.App
