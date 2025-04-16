@echo off
echo Creating lib directory...
mkdir lib

echo Downloading MongoDB Java Driver and dependencies...
echo This may take a few moments...

echo Downloading mongodb-driver-sync-4.9.1.jar...
curl -L https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/4.9.1/mongodb-driver-sync-4.9.1.jar -o lib/mongodb-driver-sync-4.9.1.jar

echo Downloading bson-4.9.1.jar...
curl -L https://repo1.maven.org/maven2/org/mongodb/bson/4.9.1/bson-4.9.1.jar -o lib/bson-4.9.1.jar

echo Downloading mongodb-driver-core-4.9.1.jar...
curl -L https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-core/4.9.1/mongodb-driver-core-4.9.1.jar -o lib/mongodb-driver-core-4.9.1.jar

echo.
echo Download complete!
echo.
echo Now you can compile the application using:
echo   mkdir -p classes
echo   javac -cp "lib/*" -d classes *.java
echo.
echo And run it using:
echo   java -cp "classes;lib/*" OnlineVotingSystemUI
echo.
echo Or with MongoDB:
echo   java -cp "classes;lib/*" OnlineVotingSystemUI --mongodb
echo.

pause
