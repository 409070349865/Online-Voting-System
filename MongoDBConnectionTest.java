/**
 * Simple test class to verify MongoDB connection and operations
 */
public class MongoDBConnectionTest {
    public static void main(String[] args) {
        System.out.println("Testing MongoDB Connection...");
        
        try {
            // Initialize MongoDB connection
            MongoDBConnection.initialize();
            System.out.println("MongoDB connection successful!");
            
            // Test voter registration
            String testVoterId = "test_voter_" + System.currentTimeMillis();
            boolean voterResult = MongoDBConnection.registerVoter(testVoterId);
            System.out.println("Voter registration test: " + (voterResult ? "PASSED" : "FAILED"));
            
            // Test candidate registration
            String testCandidateName = "test_candidate_" + System.currentTimeMillis();
            boolean candidateResult = MongoDBConnection.registerCandidate(testCandidateName);
            System.out.println("Candidate registration test: " + (candidateResult ? "PASSED" : "FAILED"));
            
            // Test voting
            boolean voteResult = MongoDBConnection.castVote(testVoterId, testCandidateName);
            System.out.println("Voting test: " + (voteResult ? "PASSED" : "FAILED"));
            
            // Test retrieving candidates
            System.out.println("Retrieving candidates...");
            MongoDBConnection.getAllCandidates().forEach(doc -> 
                System.out.println("Candidate: " + doc.getString("name") + ", Votes: " + doc.getInteger("votes")));
            
            // Close connection
            MongoDBConnection.close();
            System.out.println("MongoDB connection closed successfully");
            
            System.out.println("\nAll tests completed. If all tests passed, your MongoDB setup is working correctly!");
        } catch (Exception e) {
            System.err.println("Error during MongoDB connection test: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
