package com.example.practice;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.util.ArrayList;
import java.util.Scanner;

public class ManagerDashboardController {
    private Employee employee;
    private static final String FILENAME = "Employees.dat";

    public ManagerDashboardController(Employee employee) {
        this.employee = employee;
    }

    @FXML
    public void showMenu() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\nManager Menu:");
            System.out.println("1. View total earnings by ID");
            System.out.println("2. View payment history");
            System.out.println("3. Search user by ID");
            System.out.println("4. Search user by Salary Type");
            System.out.println("5. Archive user");
            System.out.println("6. Change salary");
            System.out.println("7. Show all employees");
            System.out.println("8. Show all managers");
            System.out.println("9. Update profile");
            System.out.println("10. Log out");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    calculateEarnings();
                    break;
                case 2:
                    System.out.println("Payment history:");
                    for (Salary salary : employee.getPaymentHistory()) {
                        System.out.println(salary);
                    }
                    break;
                case 3:
                    searchUserById();
                    break;
                case 4:
                    searchUserBySalaryType();
                    break;
                case 5:
                    archiveUser();
                    break;
                case 6:
                    changeSalary();
                    break;
                case 7:
                    Employee.showAllEmployees(FILENAME);
                    break;
                case 8:
                    Employee.showAllManagers(FILENAME);
                    break;
                case 9:
                    Employee.updateProfile(FILENAME);
                    break;
                case 10:
                    System.out.println("Logging out...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 10);
    }

    @FXML
    private void calculateEarnings() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user ID: ");
        int id = scanner.nextInt();
        double earnings = Employee.calculateEarnings(id, FILENAME);
        System.out.println("Total earnings: " + earnings);
    }

    @FXML
    private void searchUserById() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user ID: ");
        int id = scanner.nextInt();
        Employee foundEmployee = Employee.findById(id, FILENAME);
        if (foundEmployee != null) {
            System.out.println("User found: " + foundEmployee);
        } else {
            System.out.println("User not found.");
        }
    }

    @FXML
    private void searchUserBySalaryType() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter salary type (1: Fixed, 2: Hourly, 3: Commission, 4: Base Plus Commission): ");
        int salaryTypeChoice = scanner.nextInt();
        Class<? extends Salary> salaryType = null;
        switch (salaryTypeChoice) {
            case 1:
                salaryType = Fixed.class;
                break;
            case 2:
                salaryType = HourlyWage.class;
                break;
            case 3:
                salaryType = Commission.class;
                break;
            case 4:
                salaryType = BasePlusCommission.class;
                break;
            default:
                System.out.println("Invalid salary type. Please try again.");
                return;
        }

        ArrayList<Employee> result = Employee.searchBySalaryType(salaryType, FILENAME);
        if (result.isEmpty()) {
            System.out.println("No employees found with the specified salary type.");
        } else {
            System.out.println("Employees with specified salary type:");
            for (Employee e : result) {
                System.out.println(e);
            }
        }
    }

    @FXML
    private void archiveUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user ID to archive: ");
        int id = scanner.nextInt();
        Employee.archiveEmployee(id, FILENAME);
    }

    @FXML
    private void changeSalary() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter user ID: ");
        int id = scanner.nextInt();
        Employee foundEmployee = Employee.findById(id, FILENAME);
        if (foundEmployee == null) {
            System.out.println("User not found.");
            return;
        }

        System.out.print("Enter salary type (1: Fixed, 2: Hourly, 3: Commission, 4: Base Plus Commission): ");
        int salaryTypeChoice = scanner.nextInt();
        Salary newSalary = null;

        System.out.print("Enter start date (yyyy-mm-dd): ");
        String startDateStr = scanner.next();
        Date startDate = Date.valueOf(startDateStr);

        System.out.print("Enter end date (yyyy-mm-dd): ");
        String endDateStr = scanner.next();
        Date endDate = Date.valueOf(endDateStr);

        System.out.print("Is the salary active (true/false)? ");
        boolean activeSalary = scanner.nextBoolean();

        switch (salaryTypeChoice) {
            case 1:
                System.out.print("Enter monthly salary: ");
                double monthlySalary = scanner.nextDouble();
                newSalary = new Fixed(startDate, endDate, activeSalary, foundEmployee, monthlySalary);
                break;
            case 2:
                System.out.print("Enter hourly wage: ");
                double hourlyWage = scanner.nextDouble();
                System.out.print("Enter hours worked: ");
                double hoursWorked = scanner.nextDouble();
                newSalary = new HourlyWage(startDate, endDate, activeSalary, foundEmployee, hourlyWage, hoursWorked);
                break;
            case 3:
                System.out.print("Enter gross sales: ");
                double grossSales = scanner.nextDouble();
                System.out.print("Enter commission rate: ");
                double commissionRate = scanner.nextDouble();
                newSalary = new Commission(startDate, endDate, activeSalary, foundEmployee, grossSales, commissionRate);
                break;
            case 4:
                System.out.print("Enter base salary: ");
                double baseSalary = scanner.nextDouble();
                System.out.print("Enter gross sales: ");
                grossSales = scanner.nextDouble();
                System.out.print("Enter commission rate: ");
                commissionRate = scanner.nextDouble();
                newSalary = new BasePlusCommission(startDate, endDate, activeSalary, foundEmployee, baseSalary, grossSales, commissionRate);
                break;
            default:
                System.out.println("Invalid salary type. Please try again.");
                return;
        }

        Employee.changeSalary(id, newSalary, FILENAME);
        System.out.println("Salary updated for user ID " + id);
    }

    @FXML
    private void handleViewEmployeesButtonAction(ActionEvent event) {
        // منطق مشاهده کارمندان
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Employees Information");
        alert.setHeaderText(null);
        alert.setContentText("Employee details...");
        alert.showAndWait();
    }

    @FXML
    private void handleLogoutButtonAction(ActionEvent event) {
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/login.fxml"));
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(login));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewPaymentHistory(ActionEvent event) {
    }

    public void showAllEmployees(ActionEvent event) {
    }

    public void showAllManagers(ActionEvent event) {
    }

    public void updateProfile(ActionEvent event) {
    }

    public void logOut(ActionEvent event) {
    }
}
