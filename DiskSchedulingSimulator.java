import java.util.*;

public class DiskSchedulingSimulator {

    // Function to simulate FCFS (First-Come, First-Served)
    public static void FCFS(int[] requests, int headPosition) {
        int totalSeekTime = 0;
        int currentHead = headPosition;
        
        for (int i = 0; i < requests.length; i++) {
            totalSeekTime += Math.abs(requests[i] - currentHead);
            currentHead = requests[i];
        }

        double avgSeekTime = (double) totalSeekTime / requests.length;
        double throughput = (double) requests.length / totalSeekTime;

        System.out.println("Total Seek Time (FCFS): " + totalSeekTime);
        System.out.println("Average Seek Time (FCFS): " + avgSeekTime);
        System.out.println("Throughput (FCFS): " + throughput);
    }

    // Function to simulate SSTF (Shortest Seek Time First)
    public static void SSTF(int[] requests, int headPosition) {
        int totalSeekTime = 0;
        int currentHead = headPosition;
        boolean[] visited = new boolean[requests.length];

        for (int i = 0; i < requests.length; i++) {
            int minDist = Integer.MAX_VALUE;
            int closestRequest = -1;

        
            for (int j = 0; j < requests.length; j++) {
                if (!visited[j]) {
                    int dist = Math.abs(requests[j] - currentHead);
                    if (dist < minDist) {
                        minDist = dist;
                        closestRequest = j;
                    }
                }
            }

            // Mark the request as visited
            visited[closestRequest] = true;
            totalSeekTime += minDist;
            currentHead = requests[closestRequest];
        }

        double avgSeekTime = (double) totalSeekTime / requests.length;
        double throughput = (double) requests.length / totalSeekTime;

        System.out.println("Total Seek Time (SSTF): " + totalSeekTime);
        System.out.println("Average Seek Time (SSTF): " + avgSeekTime);
        System.out.println("Throughput (SSTF): " + throughput);
    }

    // Function to simulate SCAN (Elevator Algorithm)
    public static void SCAN(int[] requests, int headPosition, int diskSize) {
        int totalSeekTime = 0;
        int currentHead = headPosition;

        // Sort the requests in ascending order
        Arrays.sort(requests);

        // Separate requests into left and right sides
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();

        for (int request : requests) {
            if (request < currentHead) {
                left.add(request);
            } else {
                right.add(request);
            }
        }

        // Move towards the left and then to the right
        Collections.reverse(left);

        // Move left first
        for (int i = 0; i < left.size(); i++) {
            totalSeekTime += Math.abs(currentHead - left.get(i));
            currentHead = left.get(i);
        }

        // Move right after reaching the leftmost side
        for (int i = 0; i < right.size(); i++) {
            totalSeekTime += Math.abs(currentHead - right.get(i));
            currentHead = right.get(i);
        }

        double avgSeekTime = (double) totalSeekTime / requests.length;
        double throughput = (double) requests.length / totalSeekTime;

        System.out.println("Total Seek Time (SCAN): " + totalSeekTime);
        System.out.println("Average Seek Time (SCAN): " + avgSeekTime);
        System.out.println("Throughput (SCAN): " + throughput);
    }

    // Function to simulate C-SCAN (Circular SCAN)
    public static void CSCAN(int[] requests, int headPosition, int diskSize) {
        int totalSeekTime = 0;
        int currentHead = headPosition;

        // Sort the requests in ascending order
        Arrays.sort(requests);

        // Separate requests into left and right sides
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();

        for (int request : requests) {
            if (request < currentHead) {
                left.add(request);
            } else {
                right.add(request);
            }
        }

        // Move right first
        for (int i = 0; i < right.size(); i++) {
            totalSeekTime += Math.abs(currentHead - right.get(i));
            currentHead = right.get(i);
        }

        // After reaching the last track, move to the first track
        totalSeekTime += Math.abs(currentHead - (diskSize - 1));
        currentHead = 0;

        // Now, move to the left side
        for (int i = left.size() - 1; i >= 0; i--) {
            totalSeekTime += Math.abs(currentHead - left.get(i));
            currentHead = left.get(i);
        }

        double avgSeekTime = (double) totalSeekTime / requests.length;
        double throughput = (double) requests.length / totalSeekTime;

        System.out.println("Total Seek Time (C-SCAN): " + totalSeekTime);
        System.out.println("Average Seek Time (C-SCAN): " + avgSeekTime);
        System.out.println("Throughput (C-SCAN): " + throughput);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Input for the disk scheduling
        System.out.print("Enter number of disk requests: ");
        int numRequests = sc.nextInt();
        int[] requests = new int[numRequests];

        System.out.print("Enter initial head position: ");
        int headPosition = sc.nextInt();

        System.out.print("Enter disk size (maximum track number): ");
        int diskSize = sc.nextInt();

        System.out.println("Enter the disk requests: ");
        for (int i = 0; i < numRequests; i++) {
            requests[i] = sc.nextInt();
        }

        // Choose the scheduling algorithm using if-else
        System.out.println("\nChoose Disk Scheduling Algorithm:");
        System.out.println("1. FCFS (First-Come, First-Served)");
        System.out.println("2. SSTF (Shortest Seek Time First)");
        System.out.println("3. SCAN (Elevator Algorithm)");
        System.out.println("4. C-SCAN (Circular SCAN)");

        System.out.print("Enter your choice: ");
        int choice = sc.nextInt();

        // Execute the corresponding algorithm using if-else
        if (choice == 1) {
            FCFS(requests, headPosition);
        } else if (choice == 2) {
            SSTF(requests, headPosition);
        } else if (choice == 3) {
            SCAN(requests, headPosition, diskSize);
        } else if (choice == 4) {
            CSCAN(requests, headPosition, diskSize);
        } else {
            System.out.println("Invalid choice!");
        }

        sc.close();
    }
}
