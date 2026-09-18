# Hostel-Laundry-Managment-System
An efficient console-based application designed to streamline laundry scheduling and waitlist management in student residences. The system tracks real-time machine availability, prevents duplicate bookings, and automatically saves state to laundry_data.txt to ensure full data persistence across restarts.
A Java CLI application that manages hostel laundry machine availability, queue reservations, fault logging, and notifications.
Overview

In student hostels laundry management is a mess.
Ways to use this project
There's a command line interface (CLI) that can be used in Java Integrated Development Environment (IDE). Students can register themselves, see washer status, reserve a washer or wait until one becomes available. Information about students, washer status and queue are stored in a local file called laundry_data.txt to keep track of the information between program executions.



Key Features

Machine Status Monitoring: View real-time statuses (AVAILABLE, RUNNING, OUT_OF_SERVICE), current user, calculated finish time, and active queues.
Smart Queueing System: Uses FIFO queues (ArrayDeque) for each machine. If a machine is busy, students join the queue and get updated on their queue position.
File Persistence: The application reads and writes to laundry_data.txt using buffered input and output to store active bookings, student lists, notifications, and a log of faults.
Fault Management & Maintenance: Report faults on broken machines, notify all students currently waiting on the broken machine, and resume services for the affected queue positions upon repair.
In-App Notifications: The app notifies a student when their cycle has started, has finished, or if their machine has developed a fault.
Single-booking limit: Keeps students from having more than one active booking or queue spot at a time. 


Technologies & Tools

Language: Java 17+ (utilizes modern switch expressions and standard library data structures)
Data Structures: LinkedHashMap, HashMap, ArrayDeque, ArrayList
File Handling: BufferedReader, BufferedWriter, FileReader, FileWriter
Development Environment: VS Code / IntelliJ IDEA / Eclipse

Prerequisites & Installation

Prerequisites

Java Development Kit (JDK 17 or higher)
Git installed on your system

Steps

Clone the repository:
Bash
git clone https://github.com/your-username/hostel-laundry-system.git
cd hostel-laundry-system




Compile the Java file:
Bash
javac HostelLaundrySystem.java




Run the application:
Bash
java HostelLaundrySystem

Note:When the program runs for the first time, it automatically sets up laundry_data.txt using the default student records for S101 and S102.

Testing Instructions

Follow these simple console test cases to verify that all features are working properly: 
1. View Initial Machine Status
Choose Option 1.
Expected Result: Displays Machines 1–4 as AVAILABLE with empty queues.
2. Register a New Student
Choose Option 2.
Input Student ID: S103
Input Name: Rohan Gupta
Expected Result: Displays a confirmation message and saves the student to laundry_data.txt. 
3. Reserve an Available Machine
Choose Option 3.
Input Student ID: S101
Select Machine: 1
Expected Result: Wash cycle starts immediately for 45 minutes; Machine 1 status updates to RUNNING.
4. Test Queue System
Choose Option 3.
Input Student ID: S102
Select Machine: 1
Expected Result: The system checks that Machine 1 is running and places student S102 first in line. 
5. Check Single-Booking Guard
Choose Option 3.
Input Student ID: S102 again.
Expected Result: Console displays "This student already has an active or queued laundry booking."
6. Test Data Persistence
Choose Option 8 to exit.
Relaunch the app using java HostelLaundrySystem.
Choose Option 1.
Expected Result: Machine 1 stays active with student S102 in line, confirming that laundry_data.txt loaded properly.

OUTPUT:
===============================================
     HOSTEL LAUNDRY QUEUE & BOOKING SYSTEM
===============================================

1. View live machine status and queues
2. Register student
3. Reserve a machine / join its queue
4. Mark a wash cycle as completed
5. View student notifications
6. Report a machine fault
7. Mark a machine as repaired
8. Exit
Choose an option: 1

--- LIVE LAUNDRY STATUS ---
Machine 1: RUNNING
  In use by: Aarav Sharma (S101)
  Expected finish: 18 Sep, 08:05 PM
  Waiting queue:
    1. Priya Patel (S102) — joined 18 Sep, 07:20 PM
Machine 2: AVAILABLE
  Waiting queue: empty
Machine 3: AVAILABLE
  Waiting queue: empty
Machine 4: AVAILABLE
  Waiting queue: empty
