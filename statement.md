5.2 Statement
**PROBLEM STATEMENT**
In our hostel, several students may need to use the same laundry machines. When many students want to wash their clothes at the same time, it can be difficult to know which machine is free and whose turn it is. Students may also have to wait near the laundry area or keep checking the machines again and again.
Another problem is that students may not know when their laundry cycle has finished. If a machine stops working, the students using it or waiting for it also need to be informed.
To make this process easier, we developed the Hostel Laundry Queue & Booking System using Java. This project allows students to register, book a laundry machine, join a queue when all machines are busy, and receive notifications about their laundry.
The system keeps track of the condition of each machine. When a wash cycle is completed, the next student in the queue can use the machine. If a machine develops a fault, it can be marked as out of service until it is repaired.
The project is a console-based application, and the important details are saved in a text file so that the data can be used again when the program is restarted.
**SCOPE OF THE PROJECT**
The main aim of this project is to make the use of hostel laundry machines more systematic and reduce confusion among students.
The project includes the following functions:
Students can register by entering their student ID and name.
Users can check the status of all the laundry machines.
A student can book a machine if it is available.
If a machine is already being used, the student can join its waiting queue.
Students are given turns according to the order in which they joined the queue.
Each laundry cycle is considered to take 45 minutes.
The system displays the expected finishing time of a running laundry cycle.
Students receive notifications when their laundry starts and when it is completed.
Students can view their saved notifications.
Users can report a problem with a laundry machine.
A faulty machine is marked as out of service until it is repaired.
Students using or waiting for a faulty machine are informed about the problem.
Once a machine is repaired, the waiting queue can continue.
A student cannot have more than one active or waiting booking at a time.
Student details, machine status, bookings, queues, fault reports, and notifications are saved in laundry_data.txt.
This project is limited to managing laundry bookings and queues through a Java console program. It does not physically operate the washing machines. It also does not include a mobile application, online payment system, or graphical user interface.
**TARGET USERS**
1. Hostel Students
Students are the main users of the system. They can register themselves, check which machines are available, book a machine, join a queue, and receive updates about their laundry.
2. Hostel Staff
Hostel or laundry staff can use the system to check machine conditions, record faults, and mark machines as repaired.
3. Hostel Administration
The hostel administration can use the system to keep the laundry booking process organized and reduce confusion regarding machine usage and waiting turns.
**HIGH LEVEL FEATURES**
1. Student Registration
Students can register by providing their student ID and name. The system checks whether the student ID is already registered before adding a new student.
2. Machine Status
The system shows the status of every laundry machine. A machine can be Available, Running, or Out of Service. For a running machine, the system also shows the student using it and the expected completion time.
3. Machine Booking
A registered student can choose an available machine and start a laundry cycle. The duration of each cycle is set to 45 minutes.
4. Waiting Queue
When a machine is busy, another student can join its waiting queue instead of having to wait without knowing their turn. The queue follows the first-come, first-served method.
5. Automatic Turn Management
After a laundry cycle is marked as completed, the system removes the current booking and gives the machine to the next student in the queue, if someone is waiting.
6. Notifications
The system sends notifications to students about important events, such as:
Their laundry cycle has started.
Their position in the queue has been recorded.
Their laundry cycle has finished.
A machine fault has affected their booking or queue.
7. Fault Reporting
A user can report a problem with a machine by entering a description of the fault. The machine is then marked as out of service.
8. Machine Repair
After the machine is repaired, it can be marked as available again. If students are waiting for it, the next student can start using it.
9. File-Based Data Storage
The program saves its data in a file named laundry_data.txt. This includes student information, machine details, bookings, queues, fault reports, and notifications. The data is loaded when the program starts.
10. Input Validation
The program checks inputs such as menu choices, machine numbers, student IDs, and names. This helps avoid invalid entries and makes the program easier to use.
11. Simple Console Menu
All the operations are available through a numbered menu. Users can select an option to view machines, register students, book a machine, complete a cycle, check notifications, report faults, or repair a machine.
