#!/bin/bash

echo "Compiling Java files..."
mkdir -p classes
javac -cp "lib/*" -d classes *.java

if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check the error messages above."
    read -p "Press Enter to continue..."
    exit 1
fi

echo "Compilation successful!"
echo ""

read -p "Run with MongoDB? (y/n): " mode
if [[ $mode == "y" || $mode == "Y" ]]; then
    echo "Starting application with MongoDB..."
    echo "Make sure MongoDB is running on localhost:27017"
    java -cp "classes:lib/*" OnlineVotingSystemUI --mongodb
else
    echo "Starting application in in-memory mode..."
    java -cp "classes:lib/*" OnlineVotingSystemUI
fi

read -p "Press Enter to continue..."
