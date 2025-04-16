compile: javac -cp "lib\*" -d classes \*.java
run: java -cp "classes;lib\*" OnlineVotingSystemUI --mongodb

# Online Voting System

This is a Java Swing application for an online voting system with MongoDB integration.

## Features

- Register voters with unique IDs
- Register candidates for the election
- Cast votes for registered candidates
- View real-time voting results
- Data persistence with MongoDB (optional)

## For Absolute Beginners

### Step 1: Make sure Java is installed

1. Open Command Prompt (search for "cmd" in Windows search)
2. Type `java -version` and press Enter
3. If you see version information, Java is installed
4. If not, download and install Java from [Oracle's website](https://www.oracle.com/java/technologies/downloads/)

### Step 2: Download MongoDB (if you want to use database storage)

1. Download MongoDB Community Server from [MongoDB website](https://www.mongodb.com/try/download/community)
2. Install MongoDB following the installation wizard
3. Start MongoDB service:
   - On Windows: MongoDB should start automatically as a service
   - To verify, open Task Manager and check if "mongod.exe" is running

### Step 3: Run the application

#### Option A: Using Helper Scripts (easiest method)

1. Double-click on `download_mongodb_jars.bat` to download required libraries
2. Wait for the download to complete
3. Double-click on `compile_and_run.bat` to compile and run the application
4. When prompted "Run with MongoDB?":
   - Type `y` and press Enter if you want to use MongoDB (make sure MongoDB is running)
   - Type `n` and press Enter to use in-memory storage (no database required)

#### Option B: Manually Adding JAR Files

1. Create a folder named `lib` in the project directory
2. Download these JAR files manually:
   - [mongodb-driver-sync-4.9.1.jar](https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/4.9.1/mongodb-driver-sync-4.9.1.jar)
   - [bson-4.9.1.jar](https://repo1.maven.org/maven2/org/mongodb/bson/4.9.1/bson-4.9.1.jar)
   - [mongodb-driver-core-4.9.1.jar](https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-core/4.9.1/mongodb-driver-core-4.9.1.jar)
3. Place all downloaded JAR files in the `lib` folder
4. Open Command Prompt (cmd) and navigate to the project directory:
   ```
   cd path\to\your\project
   ```
5. Compile the Java files:
   ```
   mkdir classes
   javac -cp "lib\*" -d classes *.java
   ```
6. Run the application:
   - Without MongoDB (in-memory mode):
     ```
     java -cp "classes;lib\*" OnlineVotingSystemUI
     ```
   - With MongoDB:
     ```
     java -cp "classes;lib\*" OnlineVotingSystemUI --mongodb
     ```

### Step 4: Using the application

1. Register voters by entering a Voter ID and clicking "Register Voter"
2. Register candidates by entering a Candidate Name and clicking "Register Candidate"
3. Cast votes by entering a Voter ID and Candidate Name, then clicking "Cast Vote"
4. View results by clicking "Display Results"

## Prerequisites (for advanced users)

- Java 11 or higher
- Maven (optional)
- MongoDB (running locally on default port 27017) - only if using MongoDB mode

## How to Build and Run

### Option 1: Using Maven (if installed)

1. Build the project using Maven:

   ```
   mvn clean package
   ```

2. Run in In-Memory Mode (Default):

   ```
   java -cp target/online-voting-system-1.0-SNAPSHOT-jar-with-dependencies.jar OnlineVotingSystemUI
   ```

3. Run in MongoDB Mode:
   ```
   java -cp target/online-voting-system-1.0-SNAPSHOT-jar-with-dependencies.jar OnlineVotingSystemUI --mongodb
   ```

### Option 2: Using javac (without Maven)

#### Using Helper Scripts (Recommended)

1. Download the required MongoDB JAR files:

   - On Windows: Run `download_mongodb_jars.bat`
   - On Linux/Mac: Run `./download_mongodb_jars.sh` (you may need to make it executable with `chmod +x download_mongodb_jars.sh`)

2. Compile and run the application:
   - On Windows: Run `compile_and_run.bat`
   - On Linux/Mac: Run `./compile_and_run.sh` (you may need to make it executable with `chmod +x compile_and_run.sh`)

#### Manual Steps

1. Download the MongoDB Java Driver JAR file:

   - Visit: https://repo1.maven.org/maven2/org/mongodb/mongodb-driver-sync/4.9.1/
   - Download: mongodb-driver-sync-4.9.1.jar
   - Also download the required dependencies:
     - bson-4.9.1.jar
     - mongodb-driver-core-4.9.1.jar

2. Place the JAR files in a 'lib' folder in your project directory.

3. Compile the Java files:

   ```
   mkdir -p classes
   javac -cp "lib/*" -d classes *.java
   ```

4. Run in In-Memory Mode (Default):

   ```
   java -cp "classes;lib/*" OnlineVotingSystemUI
   ```

   (On Linux/Mac use: `java -cp "classes:lib/*" OnlineVotingSystemUI`)

5. Run in MongoDB Mode:
   ```
   java -cp "classes;lib/*" OnlineVotingSystemUI --mongodb
   ```
   (On Linux/Mac use: `java -cp "classes:lib/*" OnlineVotingSystemUI --mongodb`)

## MongoDB Integration

When running in MongoDB mode, the application:

1. Stores voter and candidate data in MongoDB
2. Persists data across application restarts
3. Uses the following collections in the `votingSystem` database:
   - `voters`: Stores voter information
   - `candidates`: Stores candidate information

Make sure MongoDB is running on localhost:27017 before starting the application in MongoDB mode.

## Testing MongoDB Connection

### With Maven:

```
java -cp target/classes MongoDBConnectionTest
```

### Without Maven:

```
java -cp "classes;lib/*" MongoDBConnectionTest
```

(On Linux/Mac use: `java -cp "classes:lib/*" MongoDBConnectionTest`)

This will verify that your MongoDB setup is working correctly.
