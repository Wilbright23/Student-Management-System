import java.io.*;
import java.util.*;

class Student {
    private int id;
    private String name;
    private String department;
    private int year;
    private double cgpa;
    private String email;

    public Student(int id, String name, String department, int year,
                   double cgpa, String email) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.year = year;
        this.cgpa = cgpa;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public int getYear() {
        return year;
    }

    public double getCgpa() {
        return cgpa;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setCgpa(double cgpa) {
        this.cgpa = cgpa;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String toFileString() {
        return id + "|" + name + "|" + department + "|" + year + "|" + cgpa + "|" + email;
    }

    public static Student fromFileString(String line) {
        String[] data = line.split("\\|");

        return new Student(
                Integer.parseInt(data[0]),
                data[1],
                data[2],
                Integer.parseInt(data[3]),
                Double.parseDouble(data[4]),
                data[5]
        );
    }

    public void display() {
        System.out.printf(
                "%-5d %-20s %-15s %-6d %-8.2f %-30s%n",
                id, name, department, year, cgpa, email
        );
    }
}

class StudentManager {
    private final ArrayList<Student> students = new ArrayList<>();
    private final String fileName = "students.txt";

    public StudentManager() {
        loadFromFile();
    }

    public boolean addStudent(Student student) {
        if (findById(student.getId()) != null) {
            return false;
        }

        students.add(student);
        saveToFile();
        return true;
    }

    public Student findById(int id) {
        for (Student student : students) {
            if (student.getId() == id) {
                return student;
            }
        }

        return null;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public boolean deleteStudent(int id) {
        Student student = findById(id);

        if (student == null) {
            return false;
        }

        students.remove(student);
        saveToFile();
        return true;
    }

    public boolean updateStudent(int id, String name, String department,
                                 int year, double cgpa, String email) {
        Student student = findById(id);

        if (student == null) {
            return false;
        }

        student.setName(name);
        student.setDepartment(department);
        student.setYear(year);
        student.setCgpa(cgpa);
        student.setEmail(email);

        saveToFile();
        return true;
    }

    public void displayAllStudents() {
        if (students.isEmpty()) {
            System.out.println("\nNo student records available.");
            return;
        }

        System.out.println("\n==================== STUDENT RECORDS ====================");
        System.out.printf(
                "%-5s %-20s %-15s %-6s %-8s %-30s%n",
                "ID", "NAME", "DEPARTMENT", "YEAR", "CGPA", "EMAIL"
        );
        System.out.println("-------------------------------------------------------------------------------");

        for (Student student : students) {
            student.display();
        }
    }

    public void searchByName(String keyword) {
        boolean found = false;

        System.out.println("\nSearch Results:");
        System.out.printf(
                "%-5s %-20s %-15s %-6s %-8s %-30s%n",
                "ID", "NAME", "DEPARTMENT", "YEAR", "CGPA", "EMAIL"
        );
        System.out.println("-------------------------------------------------------------------------------");

        for (Student student : students) {
            if (student.getName().toLowerCase().contains(keyword.toLowerCase())) {
                student.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No matching student found.");
        }
    }

    public void sortByCgpaDescending() {
        students.sort(Comparator.comparingDouble(Student::getCgpa).reversed());
        saveToFile();
        System.out.println("\nStudents sorted by CGPA in descending order.");
    }

    public void sortByName() {
        students.sort(Comparator.comparing(Student::getName));
        saveToFile();
        System.out.println("\nStudents sorted alphabetically by name.");
    }

    public void showStatistics() {
        if (students.isEmpty()) {
            System.out.println("\nNo data available for statistics.");
            return;
        }

        double total = 0;
        Student topper = students.get(0);

        for (Student student : students) {
            total += student.getCgpa();

            if (student.getCgpa() > topper.getCgpa()) {
                topper = student;
            }
        }

        double average = total / students.size();

        System.out.println("\n==================== STUDENT STATISTICS ====================");
        System.out.println("Total Students : " + students.size());
        System.out.printf("Average CGPA   : %.2f%n", average);
        System.out.println("Topper Name    : " + topper.getName());
        System.out.printf("Topper CGPA    : %.2f%n", topper.getCgpa());
    }

    private void saveToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Student student : students) {
                writer.write(student.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving student records.");
        }
    }

    private void loadFromFile() {
        File file = new File(fileName);

        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    students.add(Student.fromFileString(line));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading student records.");
        }
    }
}

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static final StudentManager manager = new StudentManager();

    public static void main(String[] args) {
        while (true) {
            displayMenu();

            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1:
                    addStudent();
                    break;

                case 2:
                    manager.displayAllStudents();
                    break;

                case 3:
                    updateStudent();
                    break;

                case 4:
                    deleteStudent();
                    break;

                case 5:
                    searchStudent();
                    break;

                case 6:
                    manager.sortByCgpaDescending();
                    break;

                case 7:
                    manager.sortByName();
                    break;

                case 8:
                    manager.showStatistics();
                    break;

                case 9:
                    System.out.println("\nThank you for using Student Management System!");
                    scanner.close();
                    return;

                default:
                    System.out.println("\nInvalid choice. Please try again.");
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\n============================================================");
        System.out.println("       STUDENT MANAGEMENT SYSTEM - JAVA PROJECT");
        System.out.println("============================================================");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Update Student");
        System.out.println("4. Delete Student");
        System.out.println("5. Search Student by Name");
        System.out.println("6. Sort Students by CGPA");
        System.out.println("7. Sort Students by Name");
        System.out.println("8. View Statistics");
        System.out.println("9. Exit");
        System.out.println("============================================================");
    }

    private static void addStudent() {
        System.out.println("\n--------------- ADD STUDENT ---------------");

        int id = readInt("Enter Student ID: ");

        if (manager.findById(id) != null) {
            System.out.println("Student ID already exists.");
            return;
        }

        String name = readText("Enter Name: ");
        String department = readText("Enter Department: ");
        int year = readYear("Enter Year (1-4): ");
        double cgpa = readCgpa("Enter CGPA (0-10): ");
        String email = readEmail("Enter Email: ");

        Student student = new Student(id, name, department, year, cgpa, email);

        if (manager.addStudent(student)) {
            System.out.println("Student added successfully.");
        } else {
            System.out.println("Unable to add student.");
        }
    }

    private static void updateStudent() {
        System.out.println("\n--------------- UPDATE STUDENT ---------------");

        int id = readInt("Enter Student ID to update: ");

        if (manager.findById(id) == null) {
            System.out.println("Student not found.");
            return;
        }

        String name = readText("Enter New Name: ");
        String department = readText("Enter New Department: ");
        int year = readYear("Enter New Year (1-4): ");
        double cgpa = readCgpa("Enter New CGPA (0-10): ");
        String email = readEmail("Enter New Email: ");

        if (manager.updateStudent(id, name, department, year, cgpa, email)) {
            System.out.println("Student updated successfully.");
        } else {
            System.out.println("Update failed.");
        }
    }

    private static void deleteStudent() {
        System.out.println("\n--------------- DELETE STUDENT ---------------");

        int id = readInt("Enter Student ID to delete: ");

        if (manager.deleteStudent(id)) {
            System.out.println("Student deleted successfully.");
        } else {
            System.out.println("Student not found.");
        }
    }

    private static void searchStudent() {
        System.out.println("\n--------------- SEARCH STUDENT ---------------");

        String keyword = readText("Enter name or part of name: ");
        manager.searchByName(keyword);
    }

    private static int readInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid integer.");
            }
        }
    }

    private static String readText(String message) {
        while (true) {
            System.out.print(message);
            String value = scanner.nextLine().trim();

            if (!value.isEmpty() && !value.contains("|")) {
                return value;
            }

            System.out.println("Input cannot be empty or contain '|'.");
        }
    }

    private static int readYear(String message) {
        while (true) {
            int year = readInt(message);

            if (year >= 1 && year <= 4) {
                return year;
            }

            System.out.println("Year must be between 1 and 4.");
        }
    }

    private static double readCgpa(String message) {
        while (true) {
            try {
                System.out.print(message);
                double cgpa = Double.parseDouble(scanner.nextLine().trim());

                if (cgpa >= 0 && cgpa <= 10) {
                    return cgpa;
                }

                System.out.println("CGPA must be between 0 and 10.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readEmail(String message) {
        while (true) {
            String email = readText(message);

            if (email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                return email;
            }

            System.out.println("Please enter a valid email address.");
        }
    }
}
