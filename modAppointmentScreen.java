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
/**
 * public controller class for Modify Appointments Screen that sets up GUI functionality
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

import static helper.timeUtil.convertTimeDateUTC;

public class modAppointmentScreen implements Initializable {
    public TextField modAppId;
    public TextField modAppTitle;
    public TextField modAppType;
    public TextField modAppDescription;
    public TextField modAppLocation;

    public TextField modAppCusID;
    public ComboBox<String> modAppUserId;
    public DatePicker modAppStart;
    public DatePicker modAppEnd;
    public Button modAppSave;
    public Button modAppCancel;
    public ComboBox<String> modStartTIme;
    public ComboBox<String> modEndTime;
    public ComboBox<String> modContact;

    Appointments selectedAppointment;
    Stage stage;

    /**
     * this is a method that populates the fields with correct data from the TableView
     * @param selectedIndex
     * @param selectedAppointment
     */

    public void setSelectedAppointment(int selectedIndex, Appointments selectedAppointment) {
        this.selectedAppointment = selectedAppointment;
        modAppId.setText(String.valueOf(selectedAppointment.getAppointmentID()));
        modAppTitle.setText(selectedAppointment.getAppointmentTitle());
        modAppType.setText(selectedAppointment.getAppointmentType());
        modAppDescription.setText(selectedAppointment.getAppointmentDescription());
        modAppLocation.setText(selectedAppointment.getAppointmentLocation());
        modAppStart.setValue(LocalDate.parse(String.valueOf(LocalDate.parse(String.valueOf(selectedAppointment.getStart().toLocalDate())))));
        modAppEnd.setValue(LocalDate.parse(String.valueOf(LocalDate.parse(String.valueOf(selectedAppointment.getEnd().toLocalDate())))));
        modContact.setValue(String.valueOf(selectedAppointment.getContactID()));
        modAppUserId.setValue(String.valueOf(selectedAppointment.getUserID()));
        modAppCusID.setText(String.valueOf(selectedAppointment.getCustomerID()));
        modStartTIme.setValue(String.valueOf(selectedAppointment.getStart().toLocalTime()));
        modEndTime.setValue(String.valueOf(selectedAppointment.getEnd().toLocalTime()));

    }

    /**
     * this is a method that saves modifications to appointments
     * @param actionEvent
     */
    public void onModAppSave(ActionEvent actionEvent) {
        try {

            Connection connection = JDBC.getConnection();

            if (!modAppTitle.getText().isEmpty() && !modAppDescription.getText().isEmpty() && !modAppLocation.getText().isEmpty() && !modAppType.getText().isEmpty() && modAppStart.getValue() != null && modAppEnd.getValue() != null && !modStartTIme.getValue().isEmpty() && !modEndTime.getValue().isEmpty() && !modAppCusID.getText().isEmpty())
            {
                ObservableList<Customers> getAllCustomers = DBCustomers.getAllCustomers();
                ObservableList<Integer> storeCustomerIDs = FXCollections.observableArrayList();
                ObservableList<DBUsers> getAllUsers = DBUsers.getAllUsers();
                ObservableList<Integer> storeUserIDs = FXCollections.observableArrayList();
                ObservableList<Appointments> getAllAppointments = DBAppointments.getAllAppointments();

                getAllCustomers.stream().map(Customers::getCustomerId).forEach(storeCustomerIDs::add);
                getAllUsers.stream().map(DBUsers::getUserId).forEach(storeUserIDs::add);

                LocalDate localDateEnd = modAppEnd.getValue();
                LocalDate localDateStart = modAppStart.getValue();

                DateTimeFormatter minHourFormat = DateTimeFormatter.ofPattern("HH:mm");

                LocalTime localTimeStart = LocalTime.parse(modStartTIme.getValue(), minHourFormat);
                LocalTime LocalTimeEnd = LocalTime.parse(modEndTime.getValue(), minHourFormat);

                LocalDateTime dateTimeStart = LocalDateTime.of(localDateStart, localTimeStart);
                LocalDateTime dateTimeEnd = LocalDateTime.of(localDateEnd, LocalTimeEnd);

                ZonedDateTime zoneDtStart = ZonedDateTime.of(dateTimeStart, ZoneId.systemDefault());
                ZonedDateTime zoneDtEnd = ZonedDateTime.of(dateTimeEnd, ZoneId.systemDefault());

                ZonedDateTime convertStartEST = zoneDtStart.withZoneSameInstant(ZoneId.of("America/New_York"));
                ZonedDateTime convertEndEST = zoneDtEnd.withZoneSameInstant(ZoneId.of("America/New_York"));

                if (convertStartEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue()) || convertStartEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue()) || convertEndEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SATURDAY.getValue())  || convertEndEST.toLocalDate().getDayOfWeek().getValue() == (DayOfWeek.SUNDAY.getValue()) )
                {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Day is outside of business operations (Monday-Friday)");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("day is outside of business hours");
                    return;
                }

                if (convertStartEST.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) || convertStartEST.toLocalTime().isAfter(LocalTime.of(22, 0, 0)) || convertEndEST.toLocalTime().isBefore(LocalTime.of(8, 0, 0)) || convertEndEST.toLocalTime().isAfter(LocalTime.of(22, 0, 0)))
                {
                    System.out.println("time is outside of business hours");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Time is outside of business hours (8am-10pm EST): " + convertStartEST.toLocalTime() + " - " + convertEndEST.toLocalTime() + " EST");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    return;
                }

                int newCustomerID = Integer.parseInt(modAppCusID.getText());
                int appointmentID = Integer.parseInt(modAppId.getText());


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

                for (Appointments appointment: getAllAppointments)
                {
                    LocalDateTime checkStart = appointment.getStart();
                    LocalDateTime checkEnd = appointment.getEnd();

                    //"outer verify" meaning check to see if an appointment exists between start and end.
                    if ((newCustomerID == appointment.getCustomerID()) && (appointmentID != appointment.getAppointmentID()) &&
                            (dateTimeStart.isBefore(checkStart)) && (dateTimeEnd.isAfter(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment overlaps with existing appointment.");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("Appointment overlaps with existing appointment.");
                        return;
                    }

                    if ((newCustomerID == appointment.getCustomerID()) && (appointmentID != appointment.getAppointmentID()) &&
//                            Clarification on isEqual is that this does not count as an overlapping appointment
//                            (dateTimeStart.isEqual(checkStart) || dateTimeStart.isAfter(checkStart)) &&
//                            (dateTimeStart.isEqual(checkEnd) || dateTimeStart.isBefore(checkEnd))) {
                            (dateTimeStart.isAfter(checkStart)) && (dateTimeStart.isBefore(checkEnd))) {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Start time overlaps with existing appointment.");
                        Optional<ButtonType> confirmation = alert.showAndWait();
                        System.out.println("Start time overlaps with existing appointment.");
                        return;
                    }



                    if (newCustomerID == appointment.getCustomerID() && (appointmentID != appointment.getAppointmentID()) &&
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

                String startDate = modAppStart.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String startTime = modStartTIme.getValue();

                String endDate = modAppEnd.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String endTime = modEndTime.getValue();

                String startUTC = convertTimeDateUTC(startDate + " " + startTime + ":00");
                String endUTC = convertTimeDateUTC(endDate + " " + endTime + ":00");

                String insertStatement = "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

                JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
                PreparedStatement ps = JDBC.getPreparedStatement();
                ps.setInt(1, Integer.parseInt(modAppId.getText()));
                ps.setString(2, modAppTitle.getText());
                ps.setString(3, modAppDescription.getText());
                ps.setString(4, modAppLocation.getText());
                ps.setString(5, modAppType.getText());
                ps.setString(6, startUTC);
                ps.setString(7, endUTC);
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, Integer.parseInt(modAppCusID.getText()));
                ps.setInt(11, Integer.parseInt(modAppUserId.getValue()));
                ps.setInt(12, Integer.parseInt(DBContact.findContactID(modContact.getValue())));
                ps.setInt(13, Integer.parseInt(modAppId.getText()));
                ps.execute();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/mainScreen.fxml"));
                Parent root = loader.load();
                stage = (Stage) modAppSave.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            }

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid Customer ID");
            alert.show();
            System.out.println("Customer ID invalid or doesn't exist");

            //e.printStackTrace();
        }

    }

    /**
     * returns to main screen
     * @param actionEvent
     * @throws IOException
     */
    public void onModAppCancel(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/mainScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) modAppCancel.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * this is a method that populates the User Id, Contacts, Start and End time Combo boxes
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



        ObservableList<Contact> contactsObservableList = null;
        try {
            contactsObservableList = DBContact.getAllContacts();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ObservableList<String> allContactsNames = FXCollections.observableArrayList();

        /**
         * Lambda 2 fills allContactsNames observableList with Contact info
         */
        contactsObservableList.forEach(contacts -> allContactsNames.add(contacts.getName()));

        ObservableList<String> appointmentTimes = FXCollections.observableArrayList();

        LocalTime firstAppointment = LocalTime.MIN.plusHours(8);
        LocalTime lastAppointment = LocalTime.MAX.minusHours(1).minusMinutes(45);

        //if statement fixed issue with infinite loop
        if (!firstAppointment.equals(0) || !lastAppointment.equals(0)) {
            while (firstAppointment.isBefore(lastAppointment)) {
                appointmentTimes.add(String.valueOf(firstAppointment));
                firstAppointment = firstAppointment.plusMinutes(15);
            }
        }
        modStartTIme.setItems(appointmentTimes);
        modEndTime.setItems(appointmentTimes);
        modContact.setItems(allContactsNames);
        modAppUserId.setItems(allUserIDs);

    }

    public void onUserID(ActionEvent actionEvent) {
    }
}
