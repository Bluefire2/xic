#!/usr/bin/env bash
# Executes the given arguments on the right version of gradle: either the gradle
# installed by the package manager, or the supplied wrapper

if which gradle > /dev/null; then
    # gradle installed by package manager
    gradle "$@"
else
    # gradle not installed by package manager, try the gradle wrapper supplied
    ./gradlew "$@"
fi
