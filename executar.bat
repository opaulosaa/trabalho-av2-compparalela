@echo off
cd /d "%~dp0"
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src\trabalho\*.java
if errorlevel 1 (
  echo Erro ao compilar.
  pause
  exit /b 1
)
java -cp bin trabalho.Main %*
pause
