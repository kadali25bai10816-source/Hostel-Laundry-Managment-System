import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Console application for managing hostel laundry machines.
 * Data is saved to and loaded from a text file (laundry_data.txt) in VS Code.
 */
public class HostelLaundrySystem {
    private static final Scanner INPUT = new Scanner(System.in);
    private static final Map<String, Student> STUDENTS = new LinkedHashMap<>();
    private static final Map<Integer, LaundryMachine> MACHINES = new LinkedHashMap<>();
    private static final Map<String, List<String>> NOTIFICATIONS = new HashMap<>();
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM, hh:mm a");
    private static final int WASH_CYCLE_MINUTES = 45;
    private static final String DATA_FILE = "laundry_data.txt";

    private enum MachineStatus {
        AVAILABLE, RUNNING, OUT_OF_SERVICE
    }

    private static class Student {
        private final String id;
        private final String name;

        Student(String id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private static class Booking {
        private final String studentId;
        private final LocalDateTime requestedAt;

        Booking(String studentId) {
            this.studentId = studentId;
            this.requestedAt = LocalDateTime.now();
        }

        Booking(String studentId, LocalDateTime requestedAt) {
            this.studentId = studentId;
            this.requestedAt = requestedAt;
        }
    }

    private static class LaundryMachine {
        private final int number;
        private MachineStatus status = MachineStatus.AVAILABLE;
        private Booking currentBooking;
        private LocalDateTime expectedFinish;
        private final Deque<Booking> queue = new ArrayDeque<>();
        private final List<String> faultReports = new ArrayList<>();

        LaundryMachine(int number) {
            this.number = number;
        }
    }

    public static void main(String[] args) {
        setUpMachines();
        loadDataFromFile();

        System.out.println("===============================================");
        System.out.println("     HOSTEL LAUNDRY QUEUE & BOOKING SYSTEM");
        System.out.println("===============================================");

        boolean running = true;
        while (running) {
            printMenu();
            switch (readInt("Choose an option: ")) {
                case 1 -> showMachineStatus();
                case 2 -> registerStudent();
                case 3 -> reserveMachine();
                case 4 -> completeCycle();
                case 5 -> viewNotifications();
                case 6 -> reportFault();
                case 7 -> repairMachine();
                case 8 -> {
                    running = false;
                    System.out.println("Goodbye. Laundry management closed.");
                }
                default -> System.out.println("Please choose a number from 1 to 8.");
            }
        }
    }

    private static void setUpMachines() {
        for (int number = 1; number <= 4; number++) {
            MACHINES.put(number, new LaundryMachine(number));
        }
    }

    private static void printMenu() {
        System.out.println("\n1. View live machine status and queues");
        System.out.println("2. Register student");
        System.out.println("3. Reserve a machine / join its queue");
        System.out.println("4. Mark a wash cycle as completed");
        System.out.println("5. View student notifications");
        System.out.println("6. Report a machine fault");
        System.out.println("7. Mark a machine as repaired");
        System.out.println("8. Exit");
    }

    private static void showMachineStatus() {
        System.out.println("\n--- LIVE LAUNDRY STATUS ---");
        for (LaundryMachine machine : MACHINES.values()) {
            System.out.printf("Machine %d: %s%n", machine.number, formatStatus(machine.status));

            if (machine.currentBooking != null) {
                Student user = STUDENTS.get(machine.currentBooking.studentId);
                String userName = user != null ? user.name : "Unknown";
                System.out.printf("  In use by: %s (%s)%n", userName, machine.currentBooking.studentId);
                if (machine.expectedFinish != null) {
                    System.out.printf("  Expected finish: %s%n", TIME_FORMAT.format(machine.expectedFinish));
                }
            }

            if (!machine.queue.isEmpty()) {
                System.out.println("  Waiting queue:");
                int position = 1;
                for (Booking booking : machine.queue) {
                    Student student = STUDENTS.get(booking.studentId);
                    String studentName = student != null ? student.name : "Unknown";
                    System.out.printf("    %d. %s (%s) — joined %s%n", position++, studentName,
                            booking.studentId, TIME_FORMAT.format(booking.requestedAt));
                }
            } else {
                System.out.println("  Waiting queue: empty");
            }

            if (machine.status == MachineStatus.OUT_OF_SERVICE && !machine.faultReports.isEmpty()) {
                System.out.println("  Latest fault: " + machine.faultReports.get(machine.faultReports.size() - 1));
            }
        }
    }

    private static void registerStudent() {
        String id = readRequired("Student ID: ").toUpperCase();
        if (STUDENTS.containsKey(id)) {
            System.out.println("That student ID is already registered.");
            return;
        }

        String name = readRequired("Student name: ");
        addStudent(id, name);
        saveDataToFile();
        System.out.println("Student registered successfully.");
    }

    private static void reserveMachine() {
        Student student = findStudent();
        if (student == null) {
            return;
        }

        int number = readInt("Machine number (1-4): ");
        LaundryMachine machine = MACHINES.get(number);
        if (machine == null) {
            System.out.println("Machine not found.");
            return;
        }

        if (hasActiveOrQueuedBooking(student.id)) {
            System.out.println("This student already has an active or queued laundry booking.");
            return;
        }

        if (machine.status == MachineStatus.OUT_OF_SERVICE) {
            System.out.println("This machine is out of service. Please select another machine.");
            return;
        }

        Booking booking = new Booking(student.id);
        if (machine.status == MachineStatus.AVAILABLE) {
            startCycle(machine, booking);
        } else {
            machine.queue.addLast(booking);
            int position = machine.queue.size();
            String message = "You joined the queue for Machine " + machine.number
                    + " at position " + position + ".";
            notifyStudent(student.id, message);
            System.out.println(message);
        }
        saveDataToFile();
    }

    private static void completeCycle() {
        int number = readInt("Machine number: ");
        LaundryMachine machine = MACHINES.get(number);
        if (machine == null) {
            System.out.println("Machine not found.");
            return;
        }
        if (machine.status != MachineStatus.RUNNING || machine.currentBooking == null) {
            System.out.println("This machine does not have a running wash cycle.");
            return;
        }

        Student completedFor = STUDENTS.get(machine.currentBooking.studentId);
        String studentName = completedFor != null ? completedFor.name : machine.currentBooking.studentId;
        String doneMessage = "Your laundry cycle on Machine " + machine.number
                + " is complete. Please collect your clothes.";
        notifyStudent(machine.currentBooking.studentId, doneMessage);
        System.out.println("Completion alert sent to " + studentName + ".");

        machine.currentBooking = null;
        machine.expectedFinish = null;
        machine.status = MachineStatus.AVAILABLE;

        if (!machine.queue.isEmpty()) {
            Booking nextBooking = machine.queue.removeFirst();
            startCycle(machine, nextBooking);
        } else {
            System.out.println("Machine " + machine.number + " is now available.");
        }
        saveDataToFile();
    }

    private static void viewNotifications() {
        Student student = findStudent();
        if (student == null) {
            return;
        }

        List<String> messages = NOTIFICATIONS.get(student.id);
        System.out.println("\n--- Notifications for " + student.name + " ---");
        if (messages == null || messages.isEmpty()) {
            System.out.println("No new notifications.");
            return;
        }

        for (String message : messages) {
            System.out.println("- " + message);
        }
        messages.clear();
        saveDataToFile();
    }

    private static void reportFault() {
        int number = readInt("Machine number: ");
        LaundryMachine machine = MACHINES.get(number);
        if (machine == null) {
            System.out.println("Machine not found.");
            return;
        }

        String description = readRequired("Describe the fault: ");
        machine.faultReports.add(TIME_FORMAT.format(LocalDateTime.now()) + " — " + description);

        if (machine.currentBooking != null) {
            String affectedStudentId = machine.currentBooking.studentId;
            notifyStudent(affectedStudentId, "Machine " + machine.number
                    + " has been reported faulty. Your current cycle was interrupted; please rebook another machine.");
            machine.currentBooking = null;
            machine.expectedFinish = null;
        }

        for (Booking booking : machine.queue) {
            notifyStudent(booking.studentId, "Machine " + machine.number
                    + " is out of service. Your queue position is preserved until it is repaired.");
        }

        machine.status = MachineStatus.OUT_OF_SERVICE;
        System.out.println("Fault recorded. The machine has been marked out of service.");
        saveDataToFile();
    }

    private static void repairMachine() {
        int number = readInt("Machine number: ");
        LaundryMachine machine = MACHINES.get(number);
        if (machine == null) {
            System.out.println("Machine not found.");
            return;
        }
        if (machine.status != MachineStatus.OUT_OF_SERVICE) {
            System.out.println("This machine is not currently marked out of service.");
            return;
        }

        machine.status = MachineStatus.AVAILABLE;
        System.out.println("Machine " + machine.number + " has been marked as repaired.");

        if (!machine.queue.isEmpty()) {
            startCycle(machine, machine.queue.removeFirst());
        }
        saveDataToFile();
    }

    private static void startCycle(LaundryMachine machine, Booking booking) {
        machine.status = MachineStatus.RUNNING;
        machine.currentBooking = booking;
        machine.expectedFinish = LocalDateTime.now().plusMinutes(WASH_CYCLE_MINUTES);

        Student student = STUDENTS.get(booking.studentId);
        String studentId = student != null ? student.id : booking.studentId;
        String message = "Your booking for Machine " + machine.number + " has started. Expected finish: "
                + TIME_FORMAT.format(machine.expectedFinish) + ".";
        notifyStudent(studentId, message);
        System.out.println(message);
    }

    private static boolean hasActiveOrQueuedBooking(String studentId) {
        for (LaundryMachine machine : MACHINES.values()) {
            if (machine.currentBooking != null && machine.currentBooking.studentId.equals(studentId)) {
                return true;
            }
            for (Booking booking : machine.queue) {
                if (booking.studentId.equals(studentId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static Student findStudent() {
        String id = readRequired("Student ID: ").toUpperCase();
        Student student = STUDENTS.get(id);
        if (student == null) {
            System.out.println("Student not found. Register the student first.");
        }
        return student;
    }

    private static void addStudent(String id, String name) {
        STUDENTS.put(id, new Student(id, name));
        NOTIFICATIONS.putIfAbsent(id, new ArrayList<>());
    }

    private static void notifyStudent(String studentId, String message) {
        NOTIFICATIONS.computeIfAbsent(studentId, key -> new ArrayList<>()).add(message);
    }

    private static String formatStatus(MachineStatus status) {
        return switch (status) {
            case AVAILABLE -> "AVAILABLE";
            case RUNNING -> "RUNNING";
            case OUT_OF_SERVICE -> "OUT OF SERVICE";
        };
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = INPUT.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static String readRequired(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = INPUT.nextLine().trim();
            if (!value.isEmpty()) {
                return value;
            }
            System.out.println("This field cannot be empty.");
        }
    }

    // =========================================================================
    // PERSISTENCE ENGINE (TXT FILE DATABASE)
    // =========================================================================

    private static void saveDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            // Write Students
            writer.write("[STUDENTS]\n");
            for (Student student : STUDENTS.values()) {
                writer.write(student.id + "|" + student.name + "\n");
            }

            // Write Machines
            writer.write("[MACHINES]\n");
            for (LaundryMachine machine : MACHINES.values()) {
                String bookingStr = (machine.currentBooking != null)
                        ? machine.currentBooking.studentId + "," + machine.currentBooking.requestedAt
                        : "NONE";
                String finishStr = (machine.expectedFinish != null)
                        ? machine.expectedFinish.toString()
                        : "NONE";

                writer.write(machine.number + "|" + machine.status + "|" + bookingStr + "|" + finishStr + "\n");

                // Save Machine Queue
                for (Booking booking : machine.queue) {
                    writer.write("QUEUE|" + machine.number + "|" + booking.studentId + "|" + booking.requestedAt + "\n");
                }

                // Save Fault Reports
                for (String fault : machine.faultReports) {
                    writer.write("FAULT|" + machine.number + "|" + fault + "\n");
                }
            }

            // Write Notifications
            writer.write("[NOTIFICATIONS]\n");
            for (Map.Entry<String, List<String>> entry : NOTIFICATIONS.entrySet()) {
                for (String note : entry.getValue()) {
                    writer.write(entry.getKey() + "|" + note + "\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to write data to file: " + e.getMessage());
        }
    }

    private static void loadDataFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            // Seed initial data if file does not exist yet
            addStudent("S101", "Aarav Sharma");
            addStudent("S102", "Priya Patel");
            saveDataToFile();
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            String section = "";

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.startsWith("[")) {
                    section = line;
                    continue;
                }

                String[] parts = line.split("\\|", -1);

                switch (section) {
                    case "[STUDENTS]" -> {
                        if (parts.length >= 2) {
                            addStudent(parts[0], parts[1]);
                        }
                    }
                    case "[MACHINES]" -> {
                        if (parts[0].equals("QUEUE")) {
                            int mNum = Integer.parseInt(parts[1]);
                            LaundryMachine machine = MACHINES.get(mNum);
                            if (machine != null) {
                                machine.queue.addLast(new Booking(parts[2], LocalDateTime.parse(parts[3])));
                            }
                        } else if (parts[0].equals("FAULT")) {
                            int mNum = Integer.parseInt(parts[1]);
                            LaundryMachine machine = MACHINES.get(mNum);
                            if (machine != null) {
                                machine.faultReports.add(parts[2]);
                            }
                        } else if (parts.length >= 4) {
                            int number = Integer.parseInt(parts[0]);
                            LaundryMachine machine = MACHINES.get(number);
                            if (machine != null) {
                                machine.status = MachineStatus.valueOf(parts[1]);
                                if (!parts[2].equals("NONE")) {
                                    String[] bParts = parts[2].split(",");
                                    machine.currentBooking = new Booking(bParts[0], LocalDateTime.parse(bParts[1]));
                                }
                                if (!parts[3].equals("NONE")) {
                                    machine.expectedFinish = LocalDateTime.parse(parts[3]);
                                }
                            }
                        }
                    }
                    case "[NOTIFICATIONS]" -> {
                        if (parts.length >= 2) {
                            notifyStudent(parts[0], parts[1]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read data from file: " + e.getMessage());
        }
    }
}