import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class DiskSchedulingGUI extends JFrame {

    private JTextField requestsField, headField, diskSizeField;
    private JComboBox<String> algorithmBox;
    private JTextArea resultArea;

    public DiskSchedulingGUI() {
        setTitle("Disk Scheduling Simulator");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Input Panel
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        requestsField = new JTextField();
        headField = new JTextField();
        diskSizeField = new JTextField();
        algorithmBox = new JComboBox<>(new String[]{"FCFS", "SSTF", "SCAN", "C-SCAN"});

        inputPanel.add(new JLabel("Disk Requests (comma-separated):"));
        inputPanel.add(requestsField);

        inputPanel.add(new JLabel("Initial Head Position:"));
        inputPanel.add(headField);

        inputPanel.add(new JLabel("Disk Size (max track):"));
        inputPanel.add(diskSizeField);

        inputPanel.add(new JLabel("Algorithm:"));
        inputPanel.add(algorithmBox);

        JButton runButton = new JButton("Run Selected Algorithm");
        JButton compareButton = new JButton("Compare All Algorithms");

        inputPanel.add(runButton);
        inputPanel.add(compareButton);

        // Result Area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setMargin(new Insets(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        runButton.addActionListener(e -> runSelectedAlgorithm());
        compareButton.addActionListener(e -> compareAllAlgorithms());

        setVisible(true);
    }

    private void runSelectedAlgorithm() {
        try {
            int[] requests = parseRequests();
            int head = Integer.parseInt(headField.getText().trim());
            int diskSize = Integer.parseInt(diskSizeField.getText().trim());
            String algo = (String) algorithmBox.getSelectedItem();

            resultArea.setText(""); // Clear previous results

            switch (algo) {
                case "FCFS" -> FCFS(requests, head);
                case "SSTF" -> SSTF(requests, head);
                case "SCAN" -> SCAN(requests, head, diskSize);
                case "C-SCAN" -> CSCAN(requests, head, diskSize);
                default -> resultArea.setText("Invalid algorithm selected.");
            }

        } catch (Exception ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }

    private void compareAllAlgorithms() {
        try {
            int[] requests = parseRequests();
            int head = Integer.parseInt(headField.getText().trim());
            int diskSize = Integer.parseInt(diskSizeField.getText().trim());

            resultArea.setText(""); // Clear previous results

            FCFS(requests.clone(), head);
            resultArea.append("\n");
            SSTF(requests.clone(), head);
            resultArea.append("\n");
            SCAN(requests.clone(), head, diskSize);
            resultArea.append("\n");
            CSCAN(requests.clone(), head, diskSize);

        } catch (Exception ex) {
            resultArea.setText("Error: " + ex.getMessage());
        }
    }

    // ---- Algorithms Below ----

    private void FCFS(int[] requests, int headPosition) {
        int totalSeekTime = 0, currentHead = headPosition;
        for (int request : requests) {
            totalSeekTime += Math.abs(request - currentHead);
            currentHead = request;
        }
        printResults("FCFS", requests.length, totalSeekTime);
    }

    private void SSTF(int[] requests, int headPosition) {
        int totalSeekTime = 0, currentHead = headPosition;
        boolean[] visited = new boolean[requests.length];

        for (int i = 0; i < requests.length; i++) {
            int minDist = Integer.MAX_VALUE, closestRequest = -1;

            for (int j = 0; j < requests.length; j++) {
                if (!visited[j]) {
                    int dist = Math.abs(requests[j] - currentHead);
                    if (dist < minDist) {
                        minDist = dist;
                        closestRequest = j;
                    }
                }
            }

            visited[closestRequest] = true;
            totalSeekTime += minDist;
            currentHead = requests[closestRequest];
        }
        printResults("SSTF", requests.length, totalSeekTime);
    }

    private void SCAN(int[] requests, int headPosition, int diskSize) {
        int totalSeekTime = 0;
        int currentHead = headPosition;
        java.util.Arrays.sort(requests);

        java.util.List<Integer> left = new java.util.ArrayList<>();
        java.util.List<Integer> right = new java.util.ArrayList<>();

        for (int r : requests) {
            if (r < currentHead) left.add(r);
            else right.add(r);
        }

        java.util.Collections.reverse(left);

        for (int r : right) {
            totalSeekTime += Math.abs(currentHead - r);
            currentHead = r;
        }

        if (!left.isEmpty()) {
            totalSeekTime += Math.abs(currentHead - (diskSize - 1));
            currentHead = diskSize - 1;

            for (int r : left) {
                totalSeekTime += Math.abs(currentHead - r);
                currentHead = r;
            }
        }

        printResults("SCAN", requests.length, totalSeekTime);
    }

    private void CSCAN(int[] requests, int headPosition, int diskSize) {
        int totalSeekTime = 0;
        int currentHead = headPosition;
        java.util.Arrays.sort(requests);

        java.util.List<Integer> left = new java.util.ArrayList<>();
        java.util.List<Integer> right = new java.util.ArrayList<>();

        for (int r : requests) {
            if (r < currentHead) left.add(r);
            else right.add(r);
        }

        for (int r : right) {
            totalSeekTime += Math.abs(currentHead - r);
            currentHead = r;
        }

        if (!left.isEmpty()) {
            totalSeekTime += Math.abs(currentHead - (diskSize - 1)) + (diskSize - 1);
            currentHead = 0;

            for (int r : left) {
                totalSeekTime += Math.abs(currentHead - r);
                currentHead = r;
            }
        }

        printResults("C-SCAN", requests.length, totalSeekTime);
    }

    private void printResults(String algorithm, int requestCount, int totalSeekTime) {
        double avg = (double) totalSeekTime / requestCount;
        double throughput = (double) requestCount / totalSeekTime;

        resultArea.append("Algorithm: " + algorithm + "\n");
        resultArea.append("Total Seek Time: " + totalSeekTime + "\n");
        resultArea.append("Average Seek Time: " + String.format("%.2f", avg) + "\n");
        resultArea.append("Throughput: " + String.format("%.4f", throughput) + "\n");
    }

    private int[] parseRequests() {
        String[] parts = requestsField.getText().split(",");
        int[] requests = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            requests[i] = Integer.parseInt(parts[i].trim());
        }
        return requests;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DiskSchedulingGUI::new);
    }
}
