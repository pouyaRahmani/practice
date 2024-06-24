package com.example.practice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public interface EmployeeDashboardControllerInterface {
    @FXML
    void handleViewEarningsById(ActionEvent event);

    @FXML
    void handleViewPaymentHistory(ActionEvent event);

    @FXML
    void handleSearchUserById(ActionEvent event);

    @FXML
    void handleSearchUserBySalaryType(ActionEvent event);

    @FXML
    void handleShowAllEmployees(ActionEvent event);

    @FXML
    void handleViewAllArchivedEmployees();

    @FXML
    void handleShowAllManagers(ActionEvent event);

    @FXML
    void handleUpdateProfile(ActionEvent event);

    @FXML
    void handleViewDepartmentEarnings(ActionEvent event);

    @FXML
    void handleViewAllEmployeesEarnings(ActionEvent event);

    @FXML
    void handleLogout(ActionEvent event);
}
