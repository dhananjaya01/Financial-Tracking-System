import java.io.*;
import java.util.*;

public class Main {
    private static final int size = 100;
    private static Student[] stdData = new Student[size];
    private static int filled = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String preference;
        do {
            printMenuOption();
                preference = scanner.next();
                scanner.nextLine(); // Consume the newline character
                switch (preference) {
                    case "0":
                        System.out.println("Exiting the program. Goodbye!");
                        break;
                    case "1":
                        obtainableSeats();
                        break;
                    case "2":
                        registretion(scanner);
                        break;
                    case "3":
                        delStudent(scanner);
                        break;
                    case "4":
                        findSt(scanner);
                        break;
                    case "5":
                        storeData();
                        break;
                    case "6":
                        loadData();
                        break;
                    case "7":
                        SortView();
                        break;
                    case "8":
                        additionalMenu(scanner);
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 8");
                }
        } while (!preference.equals("0"));

        scanner.close();
    }

    // Display the options of the menu
    private static void printMenuOption() {
        System.out.println("""
        \n1. Check available seats
        2. Register student
        3. Delete student
        4. Find student
        5. Store student details into a file
        6. Load student details from the file
        7. View sorted list of students
        8. Student details menu
        0. Exit""");
        System.out.print("Enter your choice: ");
    }

    // Check and display the number of available seats
    private static void obtainableSeats() {
        int availableSeats = size - filled;
        System.out.println("\nAvailable seats: " + availableSeats);
    }

    // Register a new student
    private static void registretion(Scanner scanner) {
        if (filled >= size) {
            System.err.println("No free seats at this moment!\nCannot register more students!");
            return;
        }
        System.out.print("Enter new student ID: ");
        String ID = scanner.next();
        if (ID.length() != 8) {
            System.out.println("ID length must be 8 characters!");
            return;
        }
        if (stDIDLocator(ID) != -1) {
            System.out.println("This ID already exists!");
        } else {
            stdData[filled++] = new Student(ID.toLowerCase());
            System.out.println("Student registered successfully.");
        }
    }

    // Use a for loop to check if a student ID already exists, return index if found, -1 otherwise
    private static int stDIDLocator(String ID) {
        for (int i = 0; i < filled; i++) {
            if (stdData[i].getId().toLowerCase().equals(ID.toLowerCase())) {
                return i;
            }
        }
        return -1;
    }

    // Delete a student from the system
    private static void delStudent(Scanner scanner) {
        if (filled <= 0) {
            System.out.println("No IDs available at this moment. \nCannot delete students...!");
            return;
        }
        System.out.print("Enter student ID to delete: ");
        String ID = scanner.next();
        int index = stDIDLocator(ID);
        if (index != -1) {
            System.out.print("Student ID-\"" + ID + "\" exists! Do you want to delete this [Y/N]: ");
            String answer = scanner.next();
            if (answer.toUpperCase().equals("Y")) {
                // Shift all elements after the deleted student to fill the gap
                for (int i = index; i < filled - 1; i++) {
                    stdData[i] = stdData[i + 1];
                }
                filled--;
                System.out.println("Student ID-\"" + ID + "\" is deleted successfully.");
            } else {
                System.out.println("Student ID-\"" + ID + "\" is not deleted");
            }
        } else {
            System.out.println("Student ID-\"" + ID + "\" is not found!");
        }
    }

    // Find and display a student's information
    private static void findSt(Scanner scanner) {
        if (filled == 0) {
            System.err.println("No IDs available at this moment!");
            return;
        }
        System.out.print("Enter student ID: ");
        String ID = scanner.next();
        int index = stDIDLocator(ID);
        if (index != -1) {
            System.out.println("\nThis ID found at index: " + index);
            System.out.println("Name : "+ stdData[index].getName());
            System.out.println("ID   : "+ stdData[index].getId());
            System.out.println("\nMarks-------------");
            int total =0;
            for(int i =0;i<3;i++){
                System.out.println("Module "+(i+1)+" : "+ stdData[index].getModuleMark(i));
                total+= stdData[index].getModuleMark(i);
            }
            double average = total / 3.0;
            System.out.println("Total    : "+total);
            System.out.printf("Average  : "+"%.2f\n",getStudentAverage(stdData[index]));
            System.out.println("Grade    : "+ setGrade(average));
        } else {
            System.out.println("Entered student ID is unassociated!");
        }
    }

    // Store student data to a file
    private static void storeData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("students.txt"))) {
            writer.println(filled);
            for (int i = 0; i < filled; i++) {
                Student student = stdData[i];
                writer.printf("%s,%s,%d,%d,%d\n",
                        student.getId() != null ? student.getId().toLowerCase() : "",
                        student.getName() != null ? student.getName().toLowerCase() : "",
                        student.getModuleMark(0),
                        student.getModuleMark(1),
                        student.getModuleMark(2));
            }
            System.out.println("Student details stored successfully.");
        } catch (IOException e) {
            System.out.println("Error storing student details: " + e.getMessage());
        }
    }

    // Load student data from a file
    private static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("students.txt"))) {
            filled = Integer.parseInt(reader.readLine());
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null && index < filled) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String id = parts[0];
                    String name = parts[1];
                    stdData[index] = new Student(id.toLowerCase());
                    stdData[index].setName(name.toLowerCase());
                    for (int j = 0; j < 3; j++) {
                        int mark = Integer.parseInt(parts[j + 2]);
                        stdData[index].setModuleMark(j, mark);
                    }
                    index++;
                }
            }
            System.out.println("Student details loaded successfully.");
        } catch (IOException e) {
            System.out.println("Error loading student details: " + e.getMessage());
        }
    }

    // View sorted list of students
    private static void SortView() {
        if (filled == 0) {
            System.err.println("No IDs available at this moment!");
            return;
        }
        Student[] sortedStudents = Arrays.copyOf(stdData, filled);
        stSort(sortedStudents);
        System.out.println("\nSorted list of students");
        System.out.printf("%-20s %-20s\n","NAME","ID");
        System.out.println("-----------------------------");
        for (int i = 0; i < filled; i++) {
            System.out.printf("%-20s %-20s\n",sortedStudents[i].getName(),sortedStudents[i].getId());
        }
    }

    // Bubble sort algorithm to sort students by name
    // This algorithm repeatedly steps through the list, compares adjacent elements 
    // and swaps them if they are in the wrong order. The pass through the list is repeated 
    // until the list is sorted.
    private static void stSort(Student[] arrayStudents) {
        int n = arrayStudents.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // Compare adjacent students' names
                if (arrayStudents[j].getName().compareToIgnoreCase(arrayStudents[j + 1].getName()) > 0) {
                    // Swap students if they are in the wrong order
                    Student passer = arrayStudents[j];
                    arrayStudents[j] = arrayStudents[j + 1];
                    arrayStudents[j + 1] = passer;
                    
                }
            }
        }
    }

    // Display and handle the student details menu
    private static void additionalMenu(Scanner scanner) {
        System.out.println("""
        \nStudent Details Menu
        a. Add student name
        b. Add module marks
        c. Generate summary report
        d. Generate complete report
        e. Exit""");
        System.out.print("Enter your choice (a/b/c/d/e): ");
        String preference = scanner.next();

        switch (preference) {
            case "a":
                addStudentName(scanner);
                break;
            case "b":
                addModuleMarks(scanner);
                break;
            case "c":
                generateSummaryReport();
                break;
            case "d":
                generateCompleteReport();
                break;
            case "e":
                break;

            default:
                System.out.println("Invalid choice. Enter form a-e");
        }
    }

    // Add or update a student's name
    private static void addStudentName(Scanner scanner) {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();
        int index = stDIDLocator(id);
        if (index == -1) {
            System.out.println("Student not found.");
            return;
        }
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        stdData[index].setName(newName);
        System.out.println("Name updated successfully.");
    }

    // Add module marks of the student
    private static void addModuleMarks(Scanner scanner) {
        System.out.print("Enter student ID: ");
        String id = scanner.nextLine();
        int index = stDIDLocator(id);
        if (index == -1) {
            System.out.println("Student not found.");
            return;
        }
        for (int i = 0; i < 3; i++) {
            System.out.print("Enter mark for " + stdData[index].getModule(i).getName() + ": ");
            int mark = scanner.nextInt();
            if(mark>0){
                stdData[index].setModuleMark(i, mark);
            }else{
                System.out.println("Invalid Mark...!");
                return;
            }

        }
        scanner.nextLine();
        System.out.println("Module marks updated successfully.");
    }

    private static void generateSummaryReport() {
        int totalStudents = filled;
        int[] above40 = new int[3];

        for (int i = 0; i < filled; i++) {
            Student student = stdData[i];
            for (int j = 0; j < 3; j++) {
                if (student.getModuleMark(j) > 40) {
                    above40[j]++;
                }
            }
        }

        System.out.println("\nSummary Report");
        System.out.println("Total student registrations: " + totalStudents);
        System.out.println("\nStudents scoring above 40 ");
        System.out.println("Module 1: " + above40[0]);
        System.out.println("Module 2: " + above40[1]);
        System.out.println("Module 3: " + above40[2]);
    }

    // Generate a complete report of all students, sorted by average score
    private static void generateCompleteReport() {
        Student[] sortedStudents = Arrays.copyOf(stdData, filled);
        averageSort(sortedStudents);

        System.out.println("\nStudent Report ");
        System.out.printf("%-10s %-20s %-10s %-10s %-10s %-10s %-10s %-10s\n",
                "ID", "Name", "Module 1", "Module 2", "Module 3", "Total", "Average", "Grade");
        System.out.println("--------------------------------------------------------------------------------------------------");

        for (int i = 0; i < filled; i++) {
            Student student = sortedStudents[i];
            int total = student.getModuleMark(0) + student.getModuleMark(1) + student.getModuleMark(2);
            double average = total / 3.0;
            String grade = setGrade(average);

            System.out.printf("%-10s %-20s %-10d %-10d %-10d %-10d %-10.2f %-10s\n",student.getId(),student.getName(),student.getModuleMark(0),student.getModuleMark(1),student.getModuleMark(2),total,average,grade);
        }
    }

    //sort students by their average score in descending order
    private static void averageSort(Student[] arr) {
        int n = arr.length;
        for (int m = 0; m < n - 1; m++) {
            for (int z = 0; z < n - m - 1; z++) {
                if (getStudentAverage(arr[z]) < getStudentAverage(arr[z + 1])) {
                    // Swap students if the average of the current student is less than the next
                    Student copy = arr[z];
                    arr[z] = arr[z + 1];
                    arr[z + 1] = copy;
                }
            }
        }
    }

    // Calculate the average score for a student
    private static double getStudentAverage(Student student) {
        return (student.getModuleMark(0) + student.getModuleMark(1) + student.getModuleMark(2)) / 3.0;
    }

    // Calculate the grade
    private static String setGrade(double average) {
        if (average >= 80) return "Distinction";
        else if (average >= 70) return "Merit";
        else if (average >= 40) return "Pass";
        else return "Fail";
    }
}

// Class to represent a student
class Student {
    private String id;
    private String name;
    private Module[] modules;

    // Constructors with id
    public Student(String id) {
        this.id = id;
        this.modules = new Module[3];
        for (int i = 0; i < 3; i++) {
            this.modules[i] = new Module("Module " + (i + 1));
        }
    }

    // Getter for student id
    public String getId() {
        return id;
    }

    // Getter for student name
    public String getName() {
        return name;
    }

    // Getter for a specific module
    public Module getModule(int index) {
        return modules[index];
    }

    // Getter for a specific module's mark
    public int getModuleMark(int index) {
        return modules[index].getMark();
    }

    // Setter for student name
    public void setName(String name) {
        this.name = name;
    }

    // Setter for a specific module's mark
    public void setModuleMark(int index, int mark) {
        modules[index].setMark(mark);
    }
}

// Class to represent a module
class Module {
    private String name;
    private int mark;

    // Constructor to initialize a module with a name
    public Module(String name) {
        this.name = name;
        this.mark = 0;
    }

    // Getter for module name
    public String getName() {
        return name;
    }

    // Getter for module mark
    public int getMark() {
        return mark;
    }

    // Setter for module mark
    public void setMark(int mark) {
        this.mark = mark;
    }
}