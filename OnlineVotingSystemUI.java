import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

class Voter {
    private String voterId;
    private boolean hasVoted;

    public Voter(String voterId) {
        this.voterId = voterId;
        this.hasVoted = false;
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

    public VotingSystem() {
        voters = new HashMap<>();
        candidates = new HashMap<>();
    }

    public void registerVoter(String voterId) {
        voters.put(voterId, new Voter(voterId));
    }

    public void registerCandidate(String candidateName) {
        candidates.put(candidateName, new Candidate(candidateName));
    }

    public boolean castVote(String voterId, String candidateName) {
        Voter voter = voters.get(voterId);
        Candidate candidate = candidates.get(candidateName);

        if (voter != null && candidate != null && !voter.hasVoted()) {
            voter.vote();
            candidate.addVote();
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Candidate> getCandidates() {
        return candidates;
    }
}

public class OnlineVotingSystemUI extends JFrame {
    private VotingSystem votingSystem;
    private JTextArea resultsArea;

    public OnlineVotingSystemUI() {
        votingSystem = new VotingSystem();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Online Voting System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(3, 1));

        // Voter Registration Panel
        JPanel voterPanel = new JPanel(new FlowLayout());
        voterPanel.setBorder(BorderFactory.createTitledBorder("Register Voter"));
        JTextField voterIdField = new JTextField(10);
        JButton registerVoterButton = new JButton("Register Voter");
        registerVoterButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String voterId = voterIdField.getText();
                votingSystem.registerVoter(voterId);
                voterIdField.setText("");
                JOptionPane.showMessageDialog(null, "Voter registered successfully!");
            }
        });
        voterPanel.add(new JLabel("Voter ID:"));
        voterPanel.add(voterIdField);
        voterPanel.add(registerVoterButton);

        // Candidate Registration Panel
        JPanel candidatePanel = new JPanel(new FlowLayout());
        candidatePanel.setBorder(BorderFactory.createTitledBorder("Register Candidate"));
        JTextField candidateNameField = new JTextField(10);
        JButton registerCandidateButton = new JButton("Register Candidate");
        registerCandidateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String candidateName = candidateNameField.getText();
                votingSystem.registerCandidate(candidateName);
                candidateNameField.setText("");
                JOptionPane.showMessageDialog(null, "Candidate registered successfully!");
            }
        });
        candidatePanel.add(new JLabel("Candidate Name:"));
        candidatePanel.add(candidateNameField);
        candidatePanel.add(registerCandidateButton);

        // Voting Panel
        JPanel votingPanel = new JPanel(new FlowLayout());
        votingPanel.setBorder(BorderFactory.createTitledBorder("Cast Vote"));
        JTextField votingVoterIdField = new JTextField(10);
        JTextField votingCandidateNameField = new JTextField(10);
        JButton castVoteButton = new JButton("Cast Vote");
        castVoteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String voterId = votingVoterIdField.getText();
                String candidateName = votingCandidateNameField.getText();
                boolean success = votingSystem.castVote(voterId, candidateName);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Vote cast successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid vote or voter has already voted.");
                }
                votingVoterIdField.setText("");
                votingCandidateNameField.setText("");
            }
        });
        votingPanel.add(new JLabel("Voter ID:"));
        votingPanel.add(votingVoterIdField);
        votingPanel.add(new JLabel("Candidate Name:"));
        votingPanel.add(votingCandidateNameField);
        votingPanel.add(castVoteButton);

        // Results Panel
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBorder(BorderFactory.createTitledBorder("Voting Results"));
        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        JButton displayResultsButton = new JButton("Display Results");
        displayResultsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayResults();
            }
        });
        resultsPanel.add(new JScrollPane(resultsArea), BorderLayout.CENTER);
        resultsPanel.add(displayResultsButton, BorderLayout.SOUTH);

        panel.add(voterPanel);
        panel.add(candidatePanel);
        panel.add(votingPanel);

        add(panel, BorderLayout.NORTH);
        add(resultsPanel, BorderLayout.CENTER);
    }

    private void displayResults() {
        StringBuilder results = new StringBuilder();
        for (Candidate candidate : votingSystem.getCandidates().values()) {
            results.append(candidate.getName()).append(": ").append(candidate.getVotes()).append(" votes\n");
        }
        resultsArea.setText(results.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new OnlineVotingSystemUI().setVisible(true);
            }
        });
    }
}
