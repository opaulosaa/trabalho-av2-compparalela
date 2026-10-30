#!/bin/sh
cd "$(dirname "$0")"
mkdir -p bin
javac -encoding UTF-8 -d bin src/trabalho/*.java || exit 1
java -cp bin trabalho.Main "$@"
