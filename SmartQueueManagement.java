import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Iterator;
import java.time.LocalDateTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.io.*;

class Person implements Comparable<Person> {
    String name;
    int priority; // Lower value = higher priority
    LocalDateTime arrivalTime;

    Person(String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.arrivalTime = LocalDateTime.now();
    }

    @Override
    public int compareTo(Person other) {
        // Primary sort by priority
        if (this.priority != other.priority) {
            return this.priority - other.priority;
        }
        // Secondary sort by arrival time (earlier first)
        return this.arrivalTime.compareTo(other.arrivalTime);
    }

    @Override
    public String toString() {
        return name + " (Priority: " + priority + ", Arrived at: " + arrivalTime.toLocalTime() + ")";
    }
}

public class SmartQueueManagement {
    private static PriorityQueue<Person> queue = new PriorityQueue<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        loadQueueFromFile();

        while (true) {
            showMenu();
            int choice = getIntegerInput("Enter choice: ");

            switch (choice) {
                case 1 -> addPerson();
                case 2 -> servePerson();
                case 3 -> viewQueue();
                case 4 -> cancelPerson();
                case 5 -> averageWaitingTime();
                case 6 -> {
                    saveQueueToFile();
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\n1. Add person to queue");
        System.out.println("2. Serve person");
        System.out.println("3. View queue");
        System.out.println("4. Cancel person");
        System.out.println("5. Average waiting time");
        System.out.println("6. Exit");
    }

    private static int getIntegerInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int input = Integer.parseInt(scanner.nextLine());
                return input;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, please enter a number.");
            }
        }
    }

    private static void addPerson() {
        System.out.print("Enter name: ");
        String name = scanner.nextLine();

        int priority = getIntegerInput("Enter priority (lower number = higher priority): ");
        Person person = new Person(name, priority);
        queue.add(person);
        System.out.println(name + " added to queue at " + person.arrivalTime.toLocalTime());
    }

    private static void servePerson() {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        Person served = queue.poll();
        Duration waitDuration = Duration.between(served.arrivalTime, LocalDateTime.now());
        System.out.println("Serving: " + served.name + ". Waited for: " + waitDuration.toMinutes() + " minutes.");
    }

    private static void viewQueue() {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        System.out.println("Current queue:");
        Iterator<Person> it = queue.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
        }
    }

    private static void cancelPerson() {
        System.out.print("Enter the name of the person to cancel: ");
        String name = scanner.nextLine();

        boolean removed = queue.removeIf(person -> person.name.equalsIgnoreCase(name));
        if (removed) {
            System.out.println(name + " has been removed from the queue.");
        } else {
            System.out.println(name + " not found in the queue.");
        }
    }

    private static void averageWaitingTime() {
        if (queue.isEmpty()) {
            System.out.println("Queue is empty.");
            return;
        }
        long totalMinutes = 0;
        int count = 0;

        LocalDateTime now = LocalDateTime.now();
        for (Person p : queue) {
            Duration duration = Duration.between(p.arrivalTime, now);
            totalMinutes += duration.toMinutes();
            count++;
        }

        double avgWait = (double) totalMinutes / count;
        System.out.printf("Average waiting time in queue: %.2f minutes.%n", avgWait);
    }

    private static void saveQueueToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("queue_data.txt"))) {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            for (Person p : queue) {
                String line = p.name + "," + p.priority + "," + p.arrivalTime.format(formatter);
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving queue data: " + e.getMessage());
        }
    }

    private static void loadQueueFromFile() {
        File file = new File("queue_data.txt");
        if (!file.exists()) return;

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String name = parts[0];
                    int priority = Integer.parseInt(parts[1]);
                    LocalDateTime arrivalTime = LocalDateTime.parse(parts[2], formatter);
                    Person p = new Person(name, priority);
                    p.arrivalTime = arrivalTime;
                    queue.add(p);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading queue data: " + e.getMessage());
        }
    }
}
