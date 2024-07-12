package com.example.practice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

public class ManagerDashboardController implements ManagerDashboardControllerInterface {

    @FXML
    private TextArea resultTextArea;

    private Employee employee;
    private static final String FILENAME = "Employees.ser";
    private Organization organization =new Organization();

    public ManagerDashboardController(Employee employee) {
        this.employee = employee;
    }
    @Override
    @FXML
    public void handleViewEarningsById(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter user ID:");
        if (id != -1) {
            double earnings = Employee.calculateEarnings(id, FILENAME);
            resultTextArea.appendText("Total earnings: " + earnings);
        }
    }
@Override
    @FXML
public void handleViewPaymentHistory(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter employee ID:");
        if (id != -1) {
            Set<Employee> employees = Employee.readEmployeesFromFile(FILENAME);
            for (Employee employee : employees) {
                if (employee.getId() == id) {
                    resultTextArea.appendText("Payment History for Employee ID " + id + ":\n");
                    for (Salary salary : employee.getPaymentHistory()) {
                        resultTextArea.appendText(salary.toString() + "\n");
                        if (salary.activeSalary) {
                            resultTextArea.appendText("(Active Salary)\n");
                        }
                    }
                    return;
                }
            }
            resultTextArea.appendText("Employee not found.");
        }
    }

@Override
    @FXML
public void handleSearchUserById(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter user ID:");
        if (id != -1) {
            Employee foundEmployee = Employee.findById(id, FILENAME);
            if (foundEmployee != null) {
                resultTextArea.appendText("User found: " + foundEmployee);
            } else {
                resultTextArea.appendText("User not found.");
            }
        }
    }

    @Override
    @FXML
    public void handleSearchUserBySalaryType(ActionEvent event) {
        resultTextArea.clear();
        int salaryTypeChoice = getUserInputAsInt("Enter salary type (1: Fixed, 2: Hourly, 3: Commission, 4: Base Plus Commission):");
        if (salaryTypeChoice != -1) {
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
                    resultTextArea.appendText("Invalid salary type. Please try again.");
                    return;
            }

            ArrayList<Employee> result = Employee.searchBySalaryType(salaryType, FILENAME);
            if (result.isEmpty()) {
                resultTextArea.appendText("No employees found with the specified salary type.");
            } else {
                resultTextArea.appendText("Employees with specified salary type:\n");
                for (Employee e : result) {
                    resultTextArea.appendText(e.toString() + "\n");
                }
            }
        }
    }

    @Override
    @FXML
    public void handleShowAllEmployees(ActionEvent event) {
        resultTextArea.clear();
        Set<Employee> employees = Employee.readEmployeesFromFile(FILENAME);
        int employeeCount = 0;
        resultTextArea.appendText("All employees (excluding managers):\n");
        for (Employee employee : employees) {
            if (!employee.isManager()) {
                resultTextArea.appendText(employee.toString() + "\n");
                employeeCount++;
            }
        }
        resultTextArea.appendText("\nTotal number of employees (excluding managers): " + employeeCount);
    }
    @Override
    @FXML
    public void handleViewAllArchivedEmployees() {
        List<Employee> archivedEmployees = Employee.showAllArchivedEmployees(FILENAME);
        resultTextArea.clear();
        resultTextArea.appendText("Total archived employees: " + archivedEmployees.size() + "\n\n");
        resultTextArea.appendText("Archived Employee list:\n");
        for (Employee employee : archivedEmployees) {
            resultTextArea.appendText(employee.toString() + "\n\n");
        }
    }
    @Override
    @FXML
    public void handleShowAllManagers(ActionEvent event) {
        resultTextArea.clear();
        Set<Employee> employees = Employee.readEmployeesFromFile(FILENAME);
        List<Employee> managers = new ArrayList<>();
        for (Employee employee : employees) {
            if (employee.isManager()) {
                managers.add(employee);
            }
        }
        resultTextArea.appendText("Total number of managers: " + managers.size() + "\n\n");
        resultTextArea.appendText("All managers:\n");
        for (Employee manager : managers) {
            resultTextArea.appendText(manager.toString() + "\n");
        }
    }
@Override
    @FXML
public void handleUpdateProfile(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter employee ID:");
        if (id != -1) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Input Required");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter username:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String username = result.get();
                Set<Employee> employees = Employee.readEmployeesFromFile(FILENAME);
                Employee employeeToUpdate = null;
                for (Employee employee : employees) {
                    if (employee.getId() == id && employee.getUserName().equals(username)) {
                        employeeToUpdate = employee;
                        break;
                    }
                }
                if (employeeToUpdate == null) {
                    resultTextArea.appendText("Employee with ID " + id + " and username " + username + " not found.");
                    return;
                }

                resultTextArea.appendText("Current details of the employee:\n");
                resultTextArea.appendText(employeeToUpdate.toString() + "\n");

                TextInputDialog updateDialog = new TextInputDialog();
                updateDialog.setTitle("Update Profile");
                updateDialog.setHeaderText(null);
                updateDialog.setContentText("Select field to update (1: First Name, 2: Last Name, 3: Social Security Number, 4: Birth Date):");
                Optional<String> fieldChoice = updateDialog.showAndWait();
                if (fieldChoice.isPresent()) {
                    int choice = Integer.parseInt(fieldChoice.get());
                    switch (choice) {
                        case 1:
                            updateDialog.setContentText("Enter new first name:");
                            Optional<String> newFirstName = updateDialog.showAndWait();
                            if (newFirstName.isPresent()) {
                                employeeToUpdate.setFirstName(newFirstName.get());
                            }
                            break;
                        case 2:
                            updateDialog.setContentText("Enter new last name:");
                            Optional<String> newLastName = updateDialog.showAndWait();
                            if (newLastName.isPresent()) {
                                employeeToUpdate.setLastName(newLastName.get());
                            }
                            break;
                        case 3:
                            updateDialog.setContentText("Enter new social security number:");
                            Optional<String> newSSN = updateDialog.showAndWait();
                            if (newSSN.isPresent()) {
                                employeeToUpdate.setSocialSecurityNumber(newSSN.get());
                            }
                            break;
                        case 4:
                            updateDialog.setContentText("Enter new birth date (yyyy-MM-dd):");
                            Optional<String> newBirthDateStr = updateDialog.showAndWait();
                            if (newBirthDateStr.isPresent()) {
                                Date newBirthDate = Date.valueOf(newBirthDateStr.get());
                                employeeToUpdate.setBirthDate(newBirthDate);
                            }
                            break;
                        default:
                            resultTextArea.appendText("Invalid choice. Please try again.");
                            return;
                    }

                    Employee.writeEmployeesToFile(employees, FILENAME);
                    resultTextArea.appendText("Employee details updated successfully.");
                }
            }
        }
    }
@Override
    @FXML
public void handleViewDepartmentEarnings(ActionEvent event) {
        resultTextArea.clear();
        int departmentId = getUserInputAsInt("Enter department ID:");
        if (departmentId != -1) {
            double departmentEarnings = Employee.calculateDepartmentEarnings(departmentId, FILENAME);
            if (departmentEarnings == 0) {
                resultTextArea.appendText("No earnings for the department.");
            } else {
                resultTextArea.appendText("Total department earnings: " + departmentEarnings);
            }
        }
    }
@Override
    @FXML
public void handleViewAllEmployeesEarnings(ActionEvent event) {
        resultTextArea.clear();
        double totalEarnings = Employee.calculateAllEmployeesEarnings(FILENAME);
        resultTextArea.appendText("Total earnings of all employees: " + totalEarnings);
    }
    @Override
    @FXML
    public void handleArchiveUser(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter user ID to archive:");
        if (id != -1) {
            Set<Employee> employees = Employee.readEmployeesFromFile(FILENAME);
            for (Employee employee : employees) {
                if (employee.getId() == id) {
                    resultTextArea.appendText(employee.toString() + "\n");

                    // Create a ChoiceDialog for selecting the reason for archiving
                    List<String> choices = Arrays.asList("NO_PAYOFF", "FIRED", "STOPPED_COOPERATING", "RETIREMENT");
                    ChoiceDialog<String> dialog = new ChoiceDialog<>("NO_PAYOFF", choices);
                    dialog.setTitle("Select Reason for Archiving");
                    dialog.setHeaderText("Select reason for archiving the employee:");
                    dialog.setContentText("Reason:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        String reason = result.get();
                        switch (reason) {
                            case "NO_PAYOFF":
                                employee.setStatus(Activity.NO_PAYOFF);
                                break;
                            case "FIRED":
                                employee.setStatus(Activity.FIRED);
                                break;
                            case "STOPPED_COOPERATING":
                                employee.setStatus(Activity.STOPPED_COOPERATING);
                                break;
                            case "RETIREMENT":
                                employee.setStatus(Activity.RETIREMENT);
                                break;
                            default:
                                resultTextArea.appendText("Invalid reason selected.\n");
                                return;
                        }

                        employee.setArchived(true);
                        employee.addArchiveHistory(new Date(), true);
                        Employee.writeEmployeesToFile(employees, FILENAME);
                        resultTextArea.appendText("Employee archived with status: " + employee.getStatus() + "\n");
                    }
                    return;
                }
            }
            resultTextArea.appendText("Employee with ID " + id + " not found.\n");
        }
    }

    @Override
    @FXML
    public void handleUnarchiveUser(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter user ID to unarchive:");
        if (id != -1) {
            Set<Employee> employees = Employee.readEmployeesFromFile(FILENAME);
            for (Employee employee : employees) {
                if (employee.getId() == id) {
                    if (!employee.isArchived(false)) {
                        resultTextArea.appendText("Employee is not archived.\n");
                        return;
                    }

                    resultTextArea.appendText(employee.toString() + "\n");

                    employee.setArchived(false);
                    employee.setStatus(Activity.ACTIVE);
                    employee.addArchiveHistory(new Date(), false);
                    Employee.writeEmployeesToFile(employees, FILENAME);
                    resultTextArea.appendText("Employee unarchived successfully.\n");
                    return;
                }
            }
            resultTextArea.appendText("Employee with ID " + id + " not found.\n");
        }
    }


    @Override
    @FXML
public void handleChangeSalary(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter employee ID:");

        if (id != -1) {
            TextInputDialog salaryTypeDialog = new TextInputDialog();
            salaryTypeDialog.setTitle("Change Salary");
            salaryTypeDialog.setHeaderText(null);
            salaryTypeDialog.setContentText("Enter salary type (1: Fixed, 2: Hourly, 3: Commission, 4: Base Plus Commission):");
            Optional<String> salaryTypeResult = salaryTypeDialog.showAndWait();

            if (salaryTypeResult.isPresent()) {
                try {
                    int salaryTypeChoice = Integer.parseInt(salaryTypeResult.get());
                    Salary newSalary = null;

                    TextInputDialog startDateDialog = new TextInputDialog();
                    startDateDialog.setTitle("Change Salary");
                    startDateDialog.setHeaderText(null);
                    startDateDialog.setContentText("Enter start date (yyyy-mm-dd):");
                    Optional<String> startDateResult = startDateDialog.showAndWait();

                    if (startDateResult.isPresent()) {
                        Date startDate = Date.valueOf(startDateResult.get());

                        TextInputDialog endDateDialog = new TextInputDialog();
                        endDateDialog.setTitle("Change Salary");
                        endDateDialog.setHeaderText(null);
                        endDateDialog.setContentText("Enter end date (yyyy-mm-dd):");
                        Optional<String> endDateResult = endDateDialog.showAndWait();

                        if (endDateResult.isPresent()) {
                            Date endDate = Date.valueOf(endDateResult.get());

                            switch (salaryTypeChoice) {
                                case 1:
                                    TextInputDialog monthlySalaryDialog = new TextInputDialog();
                                    monthlySalaryDialog.setTitle("Change Salary");
                                    monthlySalaryDialog.setHeaderText(null);
                                    monthlySalaryDialog.setContentText("Enter monthly salary:");
                                    Optional<String> monthlySalaryResult = monthlySalaryDialog.showAndWait();

                                    if (monthlySalaryResult.isPresent()) {
                                        try {
                                            double monthlySalary = Double.parseDouble(monthlySalaryResult.get());
                                            newSalary = new Fixed(startDate, endDate, true, Employee.findById(id, FILENAME), monthlySalary);
                                            Employee.changeSalary(id, newSalary, FILENAME);
                                            resultTextArea.appendText("Salary updated for user ID " + id + "\n");
                                        } catch (NumberFormatException e) {
                                            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for monthly salary.");
                                        }
                                    } else {
                                        return; // User canceled monthly salary input
                                    }
                                    break;
                                case 2:
                                    TextInputDialog hourlyWageDialog = new TextInputDialog();
                                    hourlyWageDialog.setTitle("Change Salary");
                                    hourlyWageDialog.setHeaderText(null);
                                    hourlyWageDialog.setContentText("Enter hourly wage:");
                                    Optional<String> hourlyWageResult = hourlyWageDialog.showAndWait();

                                    if (hourlyWageResult.isPresent()) {
                                        try {
                                            double hourlyWage = Double.parseDouble(hourlyWageResult.get());

                                            TextInputDialog hoursWorkedDialog = new TextInputDialog();
                                            hoursWorkedDialog.setTitle("Change Salary");
                                            hoursWorkedDialog.setHeaderText(null);
                                            hoursWorkedDialog.setContentText("Enter hours worked:");
                                            Optional<String> hoursWorkedResult = hoursWorkedDialog.showAndWait();

                                            if (hoursWorkedResult.isPresent()) {
                                                try {
                                                    double hoursWorked = Double.parseDouble(hoursWorkedResult.get());
                                                    newSalary = new HourlyWage(startDate, endDate, true, Employee.findById(id, FILENAME), hourlyWage, hoursWorked);
                                                    Employee.changeSalary(id, newSalary, FILENAME);
                                                    resultTextArea.appendText("Salary updated for user ID " + id + "\n");
                                                } catch (NumberFormatException e) {
                                                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for hours worked.");
                                                }
                                            } else {
                                                return; // User canceled hours worked input
                                            }
                                        } catch (NumberFormatException e) {
                                            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for hourly wage.");
                                        }
                                    } else {
                                        return; // User canceled hourly wage input
                                    }
                                    break;
                                case 3:
                                    TextInputDialog grossSalesDialog = new TextInputDialog();
                                    grossSalesDialog.setTitle("Change Salary");
                                    grossSalesDialog.setHeaderText(null);
                                    grossSalesDialog.setContentText("Enter gross sales:");
                                    Optional<String> grossSalesResult = grossSalesDialog.showAndWait();

                                    if (grossSalesResult.isPresent()) {
                                        try {
                                            double grossSales = Double.parseDouble(grossSalesResult.get());

                                            TextInputDialog commissionRateDialog = new TextInputDialog();
                                            commissionRateDialog.setTitle("Change Salary");
                                            commissionRateDialog.setHeaderText(null);
                                            commissionRateDialog.setContentText("Enter commission rate:");
                                            Optional<String> commissionRateResult = commissionRateDialog.showAndWait();

                                            if (commissionRateResult.isPresent()) {
                                                try {
                                                    double commissionRate = Double.parseDouble(commissionRateResult.get());
                                                    newSalary = new Commission(startDate, endDate, true, Employee.findById(id, FILENAME), grossSales, commissionRate);
                                                    Employee.changeSalary(id, newSalary, FILENAME);
                                                    resultTextArea.appendText("Salary updated for user ID " + id + "\n");
                                                } catch (NumberFormatException e) {
                                                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for commission rate.");
                                                }
                                            } else {
                                                return; // User canceled commission rate input
                                            }
                                        } catch (NumberFormatException e) {
                                            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for gross sales.");
                                        }
                                    } else {
                                        return; // User canceled gross sales input
                                    }
                                    break;
                                case 4:
                                    TextInputDialog baseSalaryDialog = new TextInputDialog();
                                    baseSalaryDialog.setTitle("Change Salary");
                                    baseSalaryDialog.setHeaderText(null);
                                    baseSalaryDialog.setContentText("Enter base salary:");
                                    Optional<String> baseSalaryResult = baseSalaryDialog.showAndWait();

                                    if (baseSalaryResult.isPresent()) {
                                        try {
                                            double baseSalary = Double.parseDouble(baseSalaryResult.get());

                                            TextInputDialog grossSalesDialogBP = new TextInputDialog();
                                            grossSalesDialogBP.setTitle("Change Salary");
                                            grossSalesDialogBP.setHeaderText(null);
                                            grossSalesDialogBP.setContentText("Enter gross sales:");
                                            Optional<String> grossSalesResultBP = grossSalesDialogBP.showAndWait();

                                            if (grossSalesResultBP.isPresent()) {
                                                try {
                                                    double grossSalesBP = Double.parseDouble(grossSalesResultBP.get());

                                                    TextInputDialog commissionRateDialogBP = new TextInputDialog();
                                                    commissionRateDialogBP.setTitle("Change Salary");
                                                    commissionRateDialogBP.setHeaderText(null);
                                                    commissionRateDialogBP.setContentText("Enter commission rate:");
                                                    Optional<String> commissionRateResultBP = commissionRateDialogBP.showAndWait();

                                                    if (commissionRateResultBP.isPresent()) {
                                                        try {
                                                            double commissionRateBP = Double.parseDouble(commissionRateResultBP.get());
                                                            newSalary = new BasePlusCommission(startDate, endDate, true, Employee.findById(id, FILENAME), baseSalary, grossSalesBP, commissionRateBP);
                                                            Employee.changeSalary(id, newSalary, FILENAME);
                                                            resultTextArea.appendText("Salary updated for user ID " + id + "\n");
                                                        } catch (NumberFormatException e) {
                                                            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for commission rate.");
                                                        }
                                                    } else {
                                                        return; // User canceled commission rate input
                                                    }
                                                } catch (NumberFormatException e) {
                                                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for gross sales.");
                                                }
                                            } else {
                                                return; // User canceled gross sales input
                                            }
                                        } catch (NumberFormatException e) {
                                            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for base salary.");
                                        }
                                    } else {
                                        return; // User canceled base salary input
                                    }
                                    break;
                                default:
                                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid salary type (1 to 4).");
                                    return;
                            }
                        } else {
                            return; // User canceled end date input
                        }
                    } else {
                        return; // User canceled start date input
                    }
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for salary type.");
                }
            } else {
                return; // User canceled salary type input
            }
        }
    }
    @Override
    @FXML
    public void handleGenerateRandomEmployee(ActionEvent event) {
        resultTextArea.clear();
        Employee newEmployee = RandomEmployee.employeeGenerator(FILENAME);
        ArrayList<Salary> salaries = RandomEmployee.salaryGenerator(newEmployee);
        for (Salary salary : salaries) {
            newEmployee.addSalary(salary);
        }
        saveEmployeeToFile(newEmployee, FILENAME);
        resultTextArea.appendText("Random employee generated and added:\n");
        resultTextArea.appendText(newEmployee.toString() + "\n");
    }

@Override
    @FXML
public void handleAddDepartment(ActionEvent event) {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Add Department");
        idDialog.setHeaderText(null);
        idDialog.setContentText("Enter department ID:");
        Optional<String> idResult = idDialog.showAndWait();

        int departmentId;
        if (idResult.isPresent()) {
            try {
                departmentId = Integer.parseInt(idResult.get());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid department ID.");
                return;
            }
        } else {
            return; // User canceled the dialog
        }

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Add Department");
        nameDialog.setHeaderText(null);
        nameDialog.setContentText("Enter department name:");
        Optional<String> nameResult = nameDialog.showAndWait();

        if (nameResult.isPresent()) {
            String departmentName = nameResult.get();
            // Call the method to add department
            if (organization.addDepartment(departmentId, departmentName))
            resultTextArea.appendText("Department added successfully.\n");
            else
                resultTextArea.appendText("Department already exists.\n");
        }
    }

@Override
    @FXML
public void handleCountEmployeesInDepartment(ActionEvent event) {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Count Employees in Department");
        idDialog.setHeaderText(null);
        idDialog.setContentText("Enter department ID:");
        Optional<String> idResult = idDialog.showAndWait();

        int departmentId;
        if (idResult.isPresent()) {
            try {
                departmentId = Integer.parseInt(idResult.get());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid department ID.");
                return;
            }
        } else {
            return; // User canceled the dialog
        }

        int count = Department.countEmployeesInDepartment(departmentId);
        resultTextArea.appendText("Total employees in department " + departmentId + ": " + count + "\n");
    }

@Override
    @FXML
public void handleChangeEmployeeDepartment(ActionEvent event) {
        TextInputDialog idDialog = new TextInputDialog();
        idDialog.setTitle("Change Employee Department");
        idDialog.setHeaderText(null);
        idDialog.setContentText("Enter employee ID:");
        Optional<String> idResult = idDialog.showAndWait();

        int employeeId;
        if (idResult.isPresent()) {
            try {
                employeeId = Integer.parseInt(idResult.get());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid employee ID.");
                return;
            }
        } else {
            return; // User canceled the dialog
        }

        TextInputDialog newIdDialog = new TextInputDialog();
        newIdDialog.setTitle("Change Employee Department");
        newIdDialog.setHeaderText(null);
        newIdDialog.setContentText("Enter new department ID:");
        Optional<String> newIdResult = newIdDialog.showAndWait();

        int newDepartmentId;
        if (newIdResult.isPresent()) {
            try {
                newDepartmentId = Integer.parseInt(newIdResult.get());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid department ID.");
                return;
            }
        } else {
            return; // User canceled the dialog
        }

        if (organization.changeEmployeeDepartment(employeeId, newDepartmentId, FILENAME))
        resultTextArea.appendText("Employee " + employeeId + " moved to department " + newDepartmentId + " successfully.\n");
        else
            resultTextArea.appendText("Managers cannot change their departments.\n");

    }

    private void saveEmployeeToFile(Employee employee, String filename) {
        System.out.println("Saving employee to file...");
        Set<Employee> employees = Employee.readEmployeesFromFile("Employees.ser");

        employees.add(employee);
        System.out.println("Employee added to set: " + employee);

        try (FileOutputStream fileOut = new FileOutputStream("Employees.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(employees);
            System.out.println("Employee data is saved in Employees.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

@Override
    @FXML
public void handleViewAllDepartments(ActionEvent event) {
        resultTextArea.clear();
        List<String> departments = organization.showAllDepartments();
        if (departments.isEmpty()) {
            resultTextArea.appendText("No departments found.");
        } else {
            resultTextArea.appendText("Departments:\n");
            for (String department : departments) {
                resultTextArea.appendText(department.toString() + "\n");
            }
        }
    }

@Override
    @FXML
public void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setFullScreenExitHint("");
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login FXML file.");
        }
    }

    private int getUserInputAsInt(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Required");
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        Optional<String> result = dialog.showAndWait();
        try {
            return result.map(Integer::parseInt).orElse(-1);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
            return -1;
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
