package controller;

import DBAccess.DBAppointments;
import DBAccess.DBCustomers;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.Appointments;
import model.Customers;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;
/**
 * public controller class for Main Screen that sets up GUI functionality
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */
public class mainScreen implements Initializable{

    @FXML
    private TableView<Appointments> appTView;
    @FXML
    private TableView<Customers> custTView;
    @FXML
    private Button appADD;
    @FXML
    private Button appMod;
    @FXML
    private Button appDelete;
    @FXML
    private Button cusAdd;
    @FXML
    private Button cusMod;
    @FXML
    private Button cusDelete;
    @FXML
    private TableColumn<?, ?> appIdCol;
    @FXML
    private TableColumn<?, ?> appTitleCol;
    @FXML
    private TableColumn<?, ?> appTypeCol;
    @FXML
    private TableColumn<?, ?> appDescrCol;
    @FXML
    private TableColumn<?, ?> appLocationCol;
    @FXML
    private TableColumn<?, ?> appStartDateCol;
    @FXML
    private TableColumn<?, ?> appEndDateCol;
    @FXML
    private TableColumn<?, ?> appContactIDCol;
    @FXML
    private TableColumn<?, ?> appCusIDCol;
    @FXML
    private TableColumn<?, ?> appUserIdCol;
    @FXML
    private TableColumn<?, ?> custIDCol;
    @FXML
    private TableColumn<?, ?> custNameCol;
    @FXML
    private TableColumn<?, ?> custAddressCol;
    @FXML
    private TableColumn<?, ?> custPhoneCol;
    @FXML
    private TableColumn<?, ?> custStateCol;
    @FXML
    private TableColumn<?, ?> custZipCol;
    @FXML
    private Button reportsButt;
    @FXML
    private Button exitMain;
    @FXML
    private RadioButton allAppRadio;
    @FXML
    private RadioButton weeklyAppRadio;
    @FXML
    private RadioButton monthlyAppRadio;

    Stage stage;
    Parent Scene;

    /**
     * goes to add appointments screen
     * @param actionEvent
     * @throws IOException
     */
    public void onAppAddClick(ActionEvent actionEvent) throws IOException {
        stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
        Scene = FXMLLoader.load(getClass().getResource("/view/addAppointmentScreen.fxml"));
        stage.setScene(new Scene(Scene));
        stage.show();

    }

    /**
     * goes to modify appointments screen and calls setSelectedAppointment method to initialize data
     * throws exception to select appointment if none are selected
     * @param actionEvent
     */
    public void onAppModClick (ActionEvent actionEvent){

           try{ FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/modAppointmentScreen.fxml"));
            loader.load();

            modAppointmentScreen MAScreen = loader.getController();
            MAScreen.setSelectedAppointment(appTView.getSelectionModel().getSelectedIndex(),
                    appTView.getSelectionModel().getSelectedItem());

            stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setScene(new Scene(scene));
            stage.show();
        }
            catch (NullPointerException | IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Select an Appointment");
        alert.show();
    }
        }

    /**
     * method to delete an appointment that lists type and id also throws exception to select an appointment
     * @param actionEvent
     */
    public void onAppDeleteClick (ActionEvent actionEvent){
            try {
                Connection connection = JDBC.getConnection();
                int deleteAppointmentID = appTView.getSelectionModel().getSelectedItem().getAppointmentID();
                String deleteAppointmentType = appTView.getSelectionModel().getSelectedItem().getAppointmentType();
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected appointment with appointment id: " + deleteAppointmentID + " and appointment type " + deleteAppointmentType);
                Optional<ButtonType> confirmation = alert.showAndWait();
                if (confirmation.isPresent() && confirmation.get() == ButtonType.OK) {
                    DBAppointments.deleteAppointment(deleteAppointmentID, connection);

                    ObservableList<Appointments> allAppointmentsList = DBAppointments.getAllAppointments();
                    appTView.setItems(allAppointmentsList);
                }
            } catch (NullPointerException | SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Select an Appointment");
                alert.show();

               // e.printStackTrace();
                System.out.println("Select an Appointment");
            }

        }

    /**
     * goes to add customer screen
     * @param actionEvent
     * @throws IOException
     */

    public void cusAddClick (ActionEvent actionEvent) throws IOException {
          stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
          Scene = FXMLLoader.load(getClass().getResource("/view/addCustomerScreen.fxml"));
          stage.setScene(new Scene(Scene));
          stage.show();

        }

    /**
     * goes to modify customers screen and calls setCustomer method to initialize data
     * throws exception to select a customer
     * @param actionEvent
     */

    public void cusModClick (ActionEvent actionEvent){
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/modCustomersScreen.fxml"));
                loader.load();

                modCustomersScreen MCSCreen = loader.getController();
                MCSCreen.setCustomer(custTView.getSelectionModel().getSelectedIndex(),
                        custTView.getSelectionModel().getSelectedItem());


                stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                Parent scene = loader.getRoot();
                stage.setScene(new Scene(scene));
                stage.show();
            }
            catch (NullPointerException | IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Select a Customer first");
                alert.show();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }


        }

    /**
     * method to delete customer and associated appointments throws exception to select a customer
     * @param actionEvent
     * @throws NullPointerException
     */
    public void cusDeleteClick (ActionEvent actionEvent) throws NullPointerException {
            try {
                Connection connection = JDBC.getConnection();
                ObservableList<Appointments> getAllAppointmentsList = DBAppointments.getAllAppointments();

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Delete the selected customer and all appointments? ");
                Optional<ButtonType> confirmation = alert.showAndWait();
                if (confirmation.isPresent() && ((Optional<?>) confirmation).get() == ButtonType.OK) {
                    int deleteCustomerID = custTView.getSelectionModel().getSelectedItem().getCustomerId();
                    DBAppointments.deleteAppointment(deleteCustomerID, connection);

                    String sqlDelete = "DELETE FROM customers WHERE Customer_ID = ?";
                    JDBC.setPreparedStatement(JDBC.getConnection(), sqlDelete);

                    PreparedStatement psDelete = JDBC.getPreparedStatement();
                    int customerFromTable = custTView.getSelectionModel().getSelectedItem().getCustomerId();

                    /**
                     * deletes appointments
                     */
                    for (Appointments appointment : getAllAppointmentsList) {
                        int customerFromAppointments = appointment.getCustomerID();
                        if (customerFromTable == customerFromAppointments) {
                            String deleteStatementAppointments = "DELETE FROM appointments WHERE Appointment_ID = ?";
                            JDBC.setPreparedStatement(JDBC.getConnection(), deleteStatementAppointments);
                        }
                    }
                    psDelete.setInt(1, customerFromTable);
                    psDelete.execute();
                    ObservableList<Customers> refreshCustomersList = DBCustomers.getAllCustomers();
                    custTView.setItems(refreshCustomersList);
                }
            } catch (NullPointerException | SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Select a Customer");
                alert.show();
                System.out.println("Select a Customer");
            }
        }

    /**
     * method that populates TableViews with necessary data
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        try {
            ObservableList<Customers> customersObservableList = DBCustomers.getAllCustomers();
                custIDCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
                custNameCol.setCellValueFactory(new PropertyValueFactory<>("cusName"));
                custAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
                custPhoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
                custStateCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
                custZipCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

                custTView.setItems(customersObservableList);

                ObservableList<Appointments> appointmentsObservableList = DBAppointments.getAllAppointments();
                appIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
                appTitleCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
                appDescrCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDescription"));
                appLocationCol.setCellValueFactory(new PropertyValueFactory<>("appointmentLocation"));
                appTypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
                appStartDateCol.setCellValueFactory(new PropertyValueFactory<>("start"));
                appEndDateCol.setCellValueFactory(new PropertyValueFactory<>("end"));
                appCusIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
                appContactIDCol.setCellValueFactory(new PropertyValueFactory<>("contactID"));
                appUserIdCol.setCellValueFactory(new PropertyValueFactory<>("userID"));

                appTView.setItems(appointmentsObservableList);


        } catch (SQLException e) {
                        throw new RuntimeException(e);
                }
    }


    /**
     * goes to reports screen
     * @param actionEvent
     * @throws IOException
     */
    public void onReportsClick(ActionEvent actionEvent) throws IOException {
        stage = (Stage) ((Button)actionEvent.getSource()).getScene().getWindow();
        Scene = FXMLLoader.load(getClass().getResource("/view/reportsScreen.fxml"));
        stage.setScene(new Scene(Scene));
        stage.show();
    }

    /**
     * exits application from main screen
     * @param actionEvent
     */
    public void onExitMain(ActionEvent actionEvent) {
        System.exit(0);

    }

    /**
     * method for all appointments radio button
     * @param actionEvent
     */
    @FXML
    void onAllApps(ActionEvent actionEvent) {
        try {
            ObservableList<Appointments> appointmentsObservableList = DBAppointments.getAllAppointments();

            if (appointmentsObservableList != null)
                for (model.Appointments appointment : appointmentsObservableList) {
                    appTView.setItems(appointmentsObservableList);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * method for weekly appointments radio button
     * @param actionEvent
     */
    @FXML
    void onWeeklyApps(ActionEvent actionEvent) {
        try {

            ObservableList<Appointments> appointmentsObservableList = DBAppointments.getAllAppointments();
            ObservableList<Appointments> appointmentsWeek = FXCollections.observableArrayList();

            LocalDateTime weekStart = LocalDateTime.now().minusWeeks(1);
            LocalDateTime weekEnd = LocalDateTime.now().plusWeeks(1);

            if (appointmentsObservableList != null)
                //IDE converted forEach
                appointmentsObservableList.forEach(appointment -> {
                    if (appointment.getEnd().isAfter(weekStart) && appointment.getEnd().isBefore(weekEnd)) {
                        appointmentsWeek.add(appointment);
                    }
                    appTView.setItems(appointmentsWeek);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method for monthly appointments radio button
     * @param actionEvent
     */
  @FXML
  void onMonthlyApps(ActionEvent actionEvent) {
        try {
            ObservableList<Appointments> appointmentsObservableList = DBAppointments.getAllAppointments();
            ObservableList<Appointments> appointmentsMonth = FXCollections.observableArrayList();

            LocalDateTime currentMonthStart = LocalDateTime.now().minusMonths(1);
            LocalDateTime currentMonthEnd = LocalDateTime.now().plusMonths(1);


            if (appointmentsObservableList != null)
                //IDE converted to forEach
                appointmentsObservableList.forEach(appointment -> {
                    if (appointment.getEnd().isAfter(currentMonthStart) && appointment.getEnd().isBefore(currentMonthEnd)) {
                        appointmentsMonth.add(appointment);
                    }
                    appTView.setItems(appointmentsMonth);
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



