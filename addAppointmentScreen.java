package controller;

import DBAccess.DBAppointments;
import DBAccess.DBContact;
import DBAccess.DBCustomers;
import DBAccess.DBUsers;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointments;
import model.Contact;
import model.Customers;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import static helper.timeUtil.convertTimeDateUTC;

/**
 * public controller class for add Appointment Screen that sets up GUI functionality
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

public class addAppointmentScreen implements Initializable {
    public TextField appId;
    public TextField appTitle;
    public TextField appType;
    public TextField appDescription;
    public TextField appLocation;
    public TextField appCusID;
    public  ComboBox<String> appUserId;
    public DatePicker appStart;
    public DatePicker appEnd;
    public Button appAdd;
    public Button appCancel;
    public ComboBox<String> appStartTime;
    public ComboBox<String> appEndTime;
    public ComboBox<String> appContactBox;

    Stage stage;

    /**
     * method to add appointment has various checks to ensure no overlap, no appointments outside of business hours,
     * no weekend dates and no Invalid customer IDs
     * @param actionEvent
     * @throws IOException
     */


    public void onAppAddClick(ActionEvent actionEvent) throws IOException {
        try {

            Connection connection = JDBC.getConnection();

            if (!appTitle.getText().isEmpty() && !appDescription.getText().isEmpty() && !appLocation.getText().isEmpty() && !appType.getText().isEmpty() && appStart.getValue() != null && appEnd.getValue() != null && !appStartTime.getValue().isEmpty() && !appEndTime.getValue().isEmpty() && !appCusID.getText().isEmpty()) {

                ObservableList<Customers> getAllCustomers = DBCustomers.getAllCustomers();
                ObservableList<Integer> storeCustomerIDs = FXCollections.observableArrayList();
                ObservableList<DBUsers> getAllUsers = DBUsers.getAllUsers();
                ObservableList<Integer> storeUserIDs = FXCollections.observableArrayList();
                ObservableList<Appointments> getAllAppointments = DBAppointments.getAllAppointments();


                //IDE converted
                getAllCustomers.stream().map(Customers::getCustomerId).forEach(storeCustomerIDs::add);
                getAllUsers.stream().map(DBUsers::getUserId).forEach(storeUserIDs::add);

                LocalDate localDateEnd = appEnd.getValue();
                LocalDate localDateStart = appStart.getValue();

                DateTimeFormatter minHourFormat = DateTimeFormatter.ofPattern("HH:mm");
                String appointmentStartDate = appStart.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String appointmentStartTime = appStartTime.getValue();

                String endDate = appEnd.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endTime = appEndTime.getValue();

                System.out.println("thisDate + thisStart " + appointmentStartDate + " " + appointmentStartTime + ":00");
                String startUTC = convertTimeDateUTC(appointmentStartDate + " " + appointmentStartTime + ":00");
                String endUTC = convertTimeDateUTC(endDate + " " + endTime + ":00");

                LocalTime localTimeStart = LocalTime.parse(appStartTime.getValue(), minHourFormat);
                LocalTime LocalTimeEnd = LocalTime.parse(appEndTime.getValue(), minHourFormat);

                LocalDateTime dateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
                LocalDateTime dateTimeEnd = LocalDateTime.of(localDateEnd, LocalTimeEnd);

                ZonedDateTime zoneDtStart = ZonedDateTime.of(dateTimeStart, ZoneId.systemDefault());
                ZonedDateTime zoneDtEnd = ZonedDateTime.of(dateTimeEnd, ZoneId.systemDefault());

                ZonedDateTime convertStartEST = zoneDtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime convertEndEST = zoneDtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

                LocalTime startAppointmentTimeToCheck = convertStartEST.toLocalTime();
                LocalTime endAppointmentTimeToCheck = convertEndEST.toLocalTime();

                DayOfWeek startAppointmentDayToCheck = convertStartEST.toLocalDate().getDayOfWeek();
                DayOfWeek endAppointmentDayToCheck = convertEndEST.toLocalDate().getDayOfWeek();

                int startAppointmentDayToCheckInt = startAppointmentDayToCheck.getValue();
                int endAppointmentDayToCheckInt = endAppointmentDayToCheck.getValue();

                int workWeekStart = DayOfWeek.MONDAY.getValue();
                int workWeekEnd = DayOfWeek.FRIDAY.getValue();

                LocalTime estBusinessStart = LocalTime.of(8, 0, 0);
                LocalTime estBusinessEnd = LocalTime.of(22, 0, 0);

                if (startAppointmentDayToCheckInt < workWeekStart || startAppointmentDayToCheckInt > workWeekEnd || endAppointmentDayToCheckInt < workWeekStart || endAppointmentDayToCheckInt > workWeekEnd) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Day is outside of business operations (Monday-Friday)");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("day is outside of business hours");
                    return;
                }

                if (startAppointmentTimeToCheck.isBefore(estBusinessStart) || startAppointmentTimeToCheck.isAfter(estBusinessEnd) || endAppointmentTimeToCheck.isBefore(estBusinessStart) || endAppointmentTimeToCheck.isAfter(estBusinessEnd)) {
                    System.out.println("time is outside of business hours");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Time is outside of business hours (8am-10pm EST): " + startAppointmentTimeToCheck + " - " + endAppointmentTimeToCheck + " EST");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                int newAppointmentID = Integer.parseInt(String.valueOf((int) (Math.random() * 100)));
                int customerID = Integer.parseInt(appCusID.getText());

                if (dateTimeStart.isAfter(dateTimeEnd)) {
                    System.out.println("Appointment has start time after end time");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment has start time after end time");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                if (dateTimeStart.isEqual(dateTimeEnd)) {
                    System.out.println("Appointment has same start and end time");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment has same start and end time");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }
                for (Appointments appointment : getAllAppointments) {
                    LocalDateTime checkStart = appointment.getStart();
                    LocalDateTime checkEnd = appointment.getEnd();

                    //"outer verify" meaning check to see if an appointment exists between start and end.
                    if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&
                            (dateTimeStart.isBefore(checkStart)) && (dateTimeEnd.isAfter(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment overlaps with existing appointment.");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("Appointment overlaps with existing appointment.");
                        return;
                    }

                    if ((customerID == appointment.getCustomerID()) && (newAppointmentID != appointment.getAppointmentID()) &&
//                            Clarification on isEqual is that this does not count as an overlapping appointment
//                            (dateTimeStart.isEqual(checkStart) || dateTimeStart.isAfter(checkStart)) &&
//                            (dateTimeStart.isEqual(checkEnd) || dateTimeStart.isBefore(checkEnd))) {
                            (dateTimeStart.isAfter(checkStart)) && (dateTimeStart.isBefore(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Start time overlaps with existing appointment.");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("Start time overlaps with existing appointment.");
                        return;
                    }


                    if (customerID == appointment.getCustomerID() && (newAppointmentID != appointment.getAppointmentID()) &&
//                            Clarification on isEqual is that this does not count as an overlapping appointment
//                            (dateTimeEnd.isEqual(checkStart) || dateTimeEnd.isAfter(checkStart)) &&
//                            (dateTimeEnd.isEqual(checkEnd) || dateTimeEnd.isBefore(checkEnd)))
                            (dateTimeEnd.isAfter(checkStart)) && (dateTimeEnd.isBefore(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "End time overlaps with existing appointment.");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("End time overlaps with existing appointment.");
                        return;
                    }
                }

                String insertStatement = "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

                JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
                PreparedStatement ps = JDBC.getPreparedStatement();
                ps.setInt(1, newAppointmentID);
                ps.setString(2, appTitle.getText());
                ps.setString(3, appDescription.getText());
                ps.setString(4, appLocation.getText());
                ps.setString(5, appType.getText());
                //ps.setTimestamp(6, Timestamp.valueOf(startLocalDateTimeToAdd));
                ps.setTimestamp(6, Timestamp.valueOf(startUTC));
                ps.setTimestamp(7, Timestamp.valueOf(endUTC));
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(11, 1);
                ps.setInt(12, Integer.parseInt(appCusID.getText()));
                ps.setInt(13, Integer.parseInt(DBContact.findContactID(appContactBox.getValue())));
                ps.setInt(14, Integer.parseInt(DBContact.findContactID(String.valueOf(appUserId.getValue()))));

                //System.out.println("ps " + ps);
                ps.execute();


                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/mainScreen.fxml"));
                Parent root = loader.load();
                stage = (Stage) appAdd.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();}

            } catch (SQLException throwables) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid Customer ID");
            alert.show();
            System.out.println("Customer ID invalid or doesn't exist");
        }

    }

    /**
     * returns to main screen
     * @param actionEvent
     * @throws IOException
     */
    public void onAppCancelClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/mainScreen.fxml"));
        Parent root = loader.load();
        stage = (Stage) appCancel.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void onStartTimeEdit(ActionEvent actionEvent) {
    }

    public void onEndTimeEdit(ActionEvent actionEvent) {
    }

    public void onContactEdit(ActionEvent actionEvent) {
    }

    /**
     * observableLists to populate time, contact, and user id combo boxes
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<DBUsers> usersObservableList = null ;
        try {
            usersObservableList = DBUsers.getAllUsers();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> allUserIDs = FXCollections.observableArrayList();

            usersObservableList.forEach(users -> allUserIDs.add(String.valueOf(Integer.valueOf(String.valueOf(users.getUserId())))));



        ObservableList<Contact> contactsObservableList;
        try {
            contactsObservableList = DBContact.getAllContacts();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ObservableList<String> allContactsNames = FXCollections.observableArrayList();

        // lambda #2
        contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getName()));


        ObservableList<String> appointmentTimes = FXCollections.observableArrayList();

        LocalTime firstAppointment = LocalTime.MIN.plusHours(8);

        LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);


        if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
            while (firstAppointment.isBefore(lastAppointment)) {
                appointmentTimes.add(String.valueOf(firstAppointment));
                firstAppointment = firstAppointment.plusMinutes(15);
            }
        }
        appStartTime.setItems(appointmentTimes);
        appEndTime.setItems(appointmentTimes);
        appContactBox.setItems(allContactsNames);
        appUserId.setItems(allUserIDs);


    }


    public void onUseredit(ActionEvent actionEvent) {
    }
}
