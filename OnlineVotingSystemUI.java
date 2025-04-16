import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;

class Voter {
    private String voterId;
    private boolean hasVoted;

    public Voter(String voterId) {
        this.voterId = voterId;
        this.hasVoted = false;
    }
    
    public Voter(Document doc) {
        this.voterId = doc.getString("voterId");
        this.hasVoted = doc.getBoolean("hasVoted");
    }

    public String getVoterId() {
        return voterId;
    }

    public boolean hasVoted() {
        return hasVoted;
    }

    public void vote() {
        hasVoted = true;
    }
}

class Candidate {
    private String name;
    private int votes;

    public Candidate(String name) {
        this.name = name;
        this.votes = 0;
    }
    
    public Candidate(Document doc) {
        this.name = doc.getString("name");
        this.votes = doc.getInteger("votes");
    }

    public String getName() {
        return name;
    }

    public int getVotes() {
        return votes;
    }

    public void addVote() {
        votes++;
    }
}

class VotingSystem {
    private Map<String, Voter> voters;
    private Map<String, Candidate> candidates;
    private boolean useMongoDb;

    public VotingSystem() {
        this(false);
    }
    
    public VotingSystem(boolean useMongoDb) {
        this.useMongoDb = useMongoDb;
        
        if (!useMongoDb) {
            voters = new HashMap<>();
            candidates = new HashMap<>();
        } else {
            // Initialize MongoDB connection
            MongoDBConnection.initialize();
        }
    }

    public void registerVoter(String voterId) {
        if (!useMongoDb) {
            voters.put(voterId, new Voter(voterId));
        } else {
            MongoDBConnection.registerVoter(voterId);
        }
    }

    public void registerCandidate(String candidateName) {
        if (!useMongoDb) {
            candidates.put(candidateName, new Candidate(candidateName));
        } else {
            MongoDBConnection.registerCandidate(candidateName);
        }
    }

    public boolean castVote(String voterId, String candidateName) {
        if (!useMongoDb) {
            Voter voter = voters.get(voterId);
            Candidate candidate = candidates.get(candidateName);

            if (voter != null && candidate != null && !voter.hasVoted()) {
                voter.vote();
                candidate.addVote();
                return true;
            } else {
                return false;
            }
        } else {
            return MongoDBConnection.castVote(voterId, candidateName);
        }
    }

    public Map<String, Candidate> getCandidates() {
        if (!useMongoDb) {
            return candidates;
        } else {
            // Convert MongoDB documents to Candidate objects
            Map<String, Candidate> candidateMap = new HashMap<>();
            List<Document> candidateDocs = MongoDBConnection.getAllCandidates();
            
            for (Document doc : candidateDocs) {
                String name = doc.getString("name");
                candidateMap.put(name, new Candidate(doc));
            }
            
            return candidateMap;
        }
    }
    
    public List<Document> getCandidateDocuments() {
        if (useMongoDb) {
            return MongoDBConnection.getAllCandidates();
        }
        return null;
    }
    
    public boolean isUsingMongoDb() {
        return useMongoDb;
    }
    
    public void close() {
        if (useMongoDb) {
            MongoDBConnection.close();
        }
    }
}

public class OnlineVotingSystemUI extends JFrame {
    private VotingSystem votingSystem;
    private JTextArea resultsArea;
    private boolean useMongoDb;

    public OnlineVotingSystemUI() {
        this(false);
    }
    
    public OnlineVotingSystemUI(boolean useMongoDb) {
        this.useMongoDb = useMongoDb;
        votingSystem = new VotingSystem(useMongoDb);
        initializeUI();
        
        // Add window closing event to close MongoDB connection if using MongoDB
        if (useMongoDb) {
            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    votingSystem.close();
                }
            });
        }
    }

    private void initializeUI() {
        setTitle(useMongoDb ? "Online Voting System (MongoDB)" : "Online Voting System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));

        JPanel panel = new JPanel(new GridLayout(3, 1));

        // Voter Registration Panel with improved styling
        JPanel voterPanel = new JPanel(new FlowLayout());
        voterPanel.setBorder(BorderFactory.createTitledBorder("Register Voter"));
        voterPanel.setBackground(new Color(240, 240, 240));
        
        JLabel voterIdLabel = new JLabel("Voter ID:");
        voterIdLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField voterIdField = new JTextField(15);
        voterIdField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JButton registerVoterButton = new JButton("Register Voter");
        registerVoterButton.setBackground(new Color(66, 134, 244));
        registerVoterButton.setForeground(Color.WHITE);
        registerVoterButton.setFocusPainted(false);
        registerVoterButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        registerVoterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String voterId = voterIdField.getText().trim();
                if (voterId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a Voter ID", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                votingSystem.registerVoter(voterId);
                voterIdField.setText("");
                JOptionPane.showMessageDialog(null, "Voter registered successfully!");
            }
        });
        
        voterPanel.add(voterIdLabel);
        voterPanel.add(voterIdField);
        voterPanel.add(registerVoterButton);

        // Candidate Registration Panel with improved styling
        JPanel candidatePanel = new JPanel(new FlowLayout());
        candidatePanel.setBorder(BorderFactory.createTitledBorder("Register Candidate"));
        candidatePanel.setBackground(new Color(240, 240, 240));
        
        JLabel candidateNameLabel = new JLabel("Candidate Name:");
        candidateNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField candidateNameField = new JTextField(15);
        candidateNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JButton registerCandidateButton = new JButton("Register Candidate");
        registerCandidateButton.setBackground(new Color(66, 134, 244));
        registerCandidateButton.setForeground(Color.WHITE);
        registerCandidateButton.setFocusPainted(false);
        registerCandidateButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        registerCandidateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String candidateName = candidateNameField.getText().trim();
                if (candidateName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a Candidate Name", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                votingSystem.registerCandidate(candidateName);
                candidateNameField.setText("");
                JOptionPane.showMessageDialog(null, "Candidate registered successfully!");
            }
        });
        
        candidatePanel.add(candidateNameLabel);
        candidatePanel.add(candidateNameField);
        candidatePanel.add(registerCandidateButton);

        // Voting Panel with improved styling
        JPanel votingPanel = new JPanel(new FlowLayout());
        votingPanel.setBorder(BorderFactory.createTitledBorder("Cast Vote"));
        votingPanel.setBackground(new Color(240, 240, 240));
        
        JLabel votingVoterIdLabel = new JLabel("Voter ID:");
        votingVoterIdLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField votingVoterIdField = new JTextField(10);
        votingVoterIdField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JLabel votingCandidateNameLabel = new JLabel("Candidate Name:");
        votingCandidateNameLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        JTextField votingCandidateNameField = new JTextField(10);
        votingCandidateNameField.setFont(new Font("Arial", Font.PLAIN, 12));
        
        JButton castVoteButton = new JButton("Cast Vote");
        castVoteButton.setBackground(new Color(46, 204, 113));
        castVoteButton.setForeground(Color.WHITE);
        castVoteButton.setFocusPainted(false);
        castVoteButton.setFont(new Font("Arial", Font.BOLD, 12));
        
        castVoteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String voterId = votingVoterIdField.getText().trim();
                String candidateName = votingCandidateNameField.getText().trim();
                
                if (voterId.isEmpty() || candidateName.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter both Voter ID and Candidate Name", "Input Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                boolean success = votingSystem.castVote(voterId, candidateName);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Vote cast successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid vote or voter has already voted.", "Voting Error", JOptionPane.ERROR_MESSAGE);
                }
                votingVoterIdField.setText("");
                votingCandidateNameField.setText("");
            }
        });
        
        votingPanel.add(votingVoterIdLabel);
        votingPanel.add(votingVoterIdField);
        votingPanel.add(votingCandidateNameLabel);
        votingPanel.add(votingCandidateNameField);
        votingPanel.add(castVoteButton);

    // Results Panel with improved UI
    JPanel resultsPanel = new JPanel(new BorderLayout());
    resultsPanel.setBorder(BorderFactory.createTitledBorder("Voting Results"));
    
    // Create a panel to hold the results instead of a simple text area
    JPanel graphicalResultsPanel = new JPanel();
    graphicalResultsPanel.setLayout(new BoxLayout(graphicalResultsPanel, BoxLayout.Y_AXIS));
    
    // Add scroll capability
    JScrollPane scrollPane = new JScrollPane(graphicalResultsPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    
    // Keep a reference to the panel for updating
    resultsArea = new JTextArea();
    resultsArea.setVisible(false); // We'll keep this for compatibility but not show it
    
    JButton displayResultsButton = new JButton("Display Results");
    displayResultsButton.setBackground(new Color(66, 134, 244));
    displayResultsButton.setForeground(Color.WHITE);
    displayResultsButton.setFocusPainted(false);
    displayResultsButton.setFont(new Font("Arial", Font.BOLD, 14));
    
    displayResultsButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            displayResults(graphicalResultsPanel);
        }
    });
    
    resultsPanel.add(scrollPane, BorderLayout.CENTER);
    resultsPanel.add(displayResultsButton, BorderLayout.SOUTH);

        panel.add(voterPanel);
        panel.add(candidatePanel);
        panel.add(votingPanel);

        add(panel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);
    }

    private void displayResults(JPanel resultsPanel) {
        // Clear previous results
        resultsPanel.removeAll();
        
        // Title label
        JLabel titleLabel = new JLabel("Election Results");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));
        resultsPanel.add(titleLabel);
        
        // Get total votes for percentage calculation
        int totalVotes = 0;
        Map<String, Integer> candidateVotes = new HashMap<>();
        
        if (useMongoDb) {
            List<Document> candidates = votingSystem.getCandidateDocuments();
            
            if (candidates.isEmpty()) {
                JLabel noResultsLabel = new JLabel("No candidates registered yet.");
                noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                noResultsLabel.setFont(new Font("Arial", Font.ITALIC, 14));
                resultsPanel.add(noResultsLabel);
            } else {
                for (Document candidateDoc : candidates) {
                    String name = candidateDoc.getString("name");
                    int votes = candidateDoc.getInteger("votes");
                    totalVotes += votes;
                    candidateVotes.put(name, votes);
                }
            }
        } else {
            for (Candidate candidate : votingSystem.getCandidates().values()) {
                totalVotes += candidate.getVotes();
                candidateVotes.put(candidate.getName(), candidate.getVotes());
            }
        }
        
        // If we have candidates, display them with graphical bars
        if (!candidateVotes.isEmpty()) {
            // Sort candidates by votes (descending)
            List<Map.Entry<String, Integer>> sortedCandidates = new ArrayList<>(candidateVotes.entrySet());
            sortedCandidates.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            
            // Display each candidate with a progress bar
            for (Map.Entry<String, Integer> entry : sortedCandidates) {
                String name = entry.getKey();
                int votes = entry.getValue();
                double percentage = totalVotes > 0 ? (votes * 100.0 / totalVotes) : 0;
                
                // Create a panel for this candidate
                JPanel candidatePanel = new JPanel(new BorderLayout(10, 0));
                candidatePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                
                // Candidate name
                JLabel nameLabel = new JLabel(name);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
                nameLabel.setPreferredSize(new Dimension(150, 25));
                candidatePanel.add(nameLabel, BorderLayout.WEST);
                
                // Progress bar for votes
                JProgressBar progressBar = new JProgressBar(0, 100);
                progressBar.setValue((int) percentage);
                progressBar.setStringPainted(true);
                progressBar.setString(votes + " votes (" + String.format("%.1f", percentage) + "%)");
                progressBar.setPreferredSize(new Dimension(300, 25));
                
                // Set color based on position
                if (sortedCandidates.indexOf(entry) == 0) {
                    // Leading candidate
                    progressBar.setForeground(new Color(46, 204, 113));
                } else if (sortedCandidates.indexOf(entry) == 1 && sortedCandidates.size() > 1) {
                    // Second place
                    progressBar.setForeground(new Color(52, 152, 219));
                } else {
                    // Others
                    progressBar.setForeground(new Color(149, 165, 166));
                }
                
                candidatePanel.add(progressBar, BorderLayout.CENTER);
                
                // Add to results panel
                resultsPanel.add(candidatePanel);
            }
            
            // Add total votes information
            JLabel totalLabel = new JLabel("Total Votes: " + totalVotes);
            totalLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            totalLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            totalLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
            resultsPanel.add(totalLabel);
        }
        
        // Update the text area for compatibility (not visible)
        StringBuilder results = new StringBuilder();
        for (Map.Entry<String, Integer> entry : candidateVotes.entrySet()) {
            results.append(entry.getKey()).append(": ").append(entry.getValue()).append(" votes\n");
        }
        resultsArea.setText(results.toString());
        
        // Refresh the panel
        resultsPanel.revalidate();
        resultsPanel.repaint();
    }

    public static void main(String[] args) {
        // Check if MongoDB should be used
        boolean useMongoDb = false;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("--mongodb") || arg.equalsIgnoreCase("-m")) {
                useMongoDb = true;
                break;
            }
        }
        
        final boolean finalUseMongoDb = useMongoDb;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new OnlineVotingSystemUI(finalUseMongoDb).setVisible(true);
            }
        });
    }
}
