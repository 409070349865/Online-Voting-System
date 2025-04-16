@echo off
echo Compiling Java files...
mkdir -p classes
javac -cp "lib/*" -d classes *.java

if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed. Please check the error messages above.
    pause
    exit /b %ERRORLEVEL%
)

echo Compilation successful!
echo.

set /p mode="Run with MongoDB? (y/n): "
if /i "%mode%"=="y" (
    echo Starting application with MongoDB...
    echo Make sure MongoDB is running on localhost:27017
    java -cp "classes;lib/*" OnlineVotingSystemUI --mongodb
) else (
    echo Starting application in in-memory mode...
    java -cp "classes;lib/*" OnlineVotingSystemUI
)

pause
