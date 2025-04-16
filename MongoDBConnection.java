import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for MongoDB connection and operations
 */
public class MongoDBConnection {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    private static final String CONNECTION_STRING = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "votingSystem";
    private static final String VOTERS_COLLECTION = "voters";
    private static final String CANDIDATES_COLLECTION = "candidates";

    /**
     * Initialize the MongoDB connection
     */
    public static void initialize() {
        try {
            mongoClient = MongoClients.create(CONNECTION_STRING);
            database = mongoClient.getDatabase(DATABASE_NAME);
            
            // Ensure collections exist
            if (!collectionExists(VOTERS_COLLECTION)) {
                database.createCollection(VOTERS_COLLECTION);
            }
            
            if (!collectionExists(CANDIDATES_COLLECTION)) {
                database.createCollection(CANDIDATES_COLLECTION);
            }
            
            System.out.println("MongoDB connection initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing MongoDB connection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Check if a collection exists in the database
     * 
     * @param collectionName Name of the collection
     * @return true if collection exists, false otherwise
     */
    private static boolean collectionExists(String collectionName) {
        for (String name : database.listCollectionNames()) {
            if (name.equals(collectionName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Close the MongoDB connection
     */
    public static void close() {
        if (mongoClient != null) {
            mongoClient.close();
            System.out.println("MongoDB connection closed");
        }
    }

    /**
     * Get the voters collection
     * 
     * @return MongoCollection for voters
     */
    public static MongoCollection<Document> getVotersCollection() {
        return database.getCollection(VOTERS_COLLECTION);
    }

    /**
     * Get the candidates collection
     * 
     * @return MongoCollection for candidates
     */
    public static MongoCollection<Document> getCandidatesCollection() {
        return database.getCollection(CANDIDATES_COLLECTION);
    }

    /**
     * Register a new voter
     * 
     * @param voterId Voter ID
     * @return true if registration successful, false otherwise
     */
    public static boolean registerVoter(String voterId) {
        try {
            MongoCollection<Document> voters = getVotersCollection();
            
            // Check if voter already exists
            Document existingVoter = voters.find(Filters.eq("voterId", voterId)).first();
            if (existingVoter != null) {
                return false;
            }
            
            // Create new voter document
            Document voter = new Document("voterId", voterId)
                    .append("hasVoted", false);
            
            voters.insertOne(voter);
            return true;
        } catch (Exception e) {
            System.err.println("Error registering voter: " + e.getMessage());
            return false;
        }
    }

    /**
     * Register a new candidate
     * 
     * @param candidateName Candidate name
     * @return true if registration successful, false otherwise
     */
    public static boolean registerCandidate(String candidateName) {
        try {
            MongoCollection<Document> candidates = getCandidatesCollection();
            
            // Check if candidate already exists
            Document existingCandidate = candidates.find(Filters.eq("name", candidateName)).first();
            if (existingCandidate != null) {
                return false;
            }
            
            // Create new candidate document
            Document candidate = new Document("name", candidateName)
                    .append("votes", 0);
            
            candidates.insertOne(candidate);
            return true;
        } catch (Exception e) {
            System.err.println("Error registering candidate: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cast a vote for a candidate
     * 
     * @param voterId Voter ID
     * @param candidateName Candidate name
     * @return true if vote cast successfully, false otherwise
     */
    public static boolean castVote(String voterId, String candidateName) {
        try {
            MongoCollection<Document> voters = getVotersCollection();
            MongoCollection<Document> candidates = getCandidatesCollection();
            
            // Check if voter exists and hasn't voted
            Document voter = voters.find(Filters.eq("voterId", voterId)).first();
            if (voter == null || voter.getBoolean("hasVoted")) {
                return false;
            }
            
            // Check if candidate exists
            Document candidate = candidates.find(Filters.eq("name", candidateName)).first();
            if (candidate == null) {
                return false;
            }
            
            // Update voter status
            Bson voterFilter = Filters.eq("voterId", voterId);
            Bson voterUpdate = Updates.set("hasVoted", true);
            UpdateResult voterResult = voters.updateOne(voterFilter, voterUpdate);
            
            // Increment candidate votes
            Bson candidateFilter = Filters.eq("name", candidateName);
            Bson candidateUpdate = Updates.inc("votes", 1);
            UpdateResult candidateResult = candidates.updateOne(candidateFilter, candidateUpdate);
            
            return voterResult.getModifiedCount() > 0 && candidateResult.getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Error casting vote: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get all candidates with their vote counts
     * 
     * @return List of candidate documents
     */
    public static List<Document> getAllCandidates() {
        List<Document> candidateList = new ArrayList<>();
        try {
            MongoCollection<Document> candidates = getCandidatesCollection();
            candidates.find().into(candidateList);
        } catch (Exception e) {
            System.err.println("Error getting candidates: " + e.getMessage());
        }
        return candidateList;
    }
}
