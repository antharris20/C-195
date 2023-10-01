package controller;

import DBAccess.DBAppointments;
import DBAccess.DBUsers;
import helper.JDBC;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.Appointments;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
/**
 * public controller class for Login Screen that sets up GUI functionality
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

public class logInScreen implements Initializable {

    public Button exitButt;
    public Label zoneIDLabel;
    public Label logInLabel;
    @FXML
    private TextField zoneIDField;
    @FXML
    private Button enterButt;
    @FXML
    private PasswordField pWordTextBox;
    @FXML
    private Label uNameLabel;
    @FXML
    private TextField uNameTextBox;
    @FXML
    private Label pWordLabel;
    Stage stage;

    /**
     * method that verifies login info with database and calls validateUser method
     * also checks if there is an appointment within 15 minutes of login time and alerts user
     * logs successful logins to necessary documents
     * and calls language resource bundle for translations
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     * @throws Exception
     */
    @FXML
    private void enterButt(ActionEvent actionEvent)  throws SQLException, IOException, Exception {
        try {
            JDBC.getConnection();

            //definitions for +/- 15 minute appointment check
            ObservableList<Appointments> getAllAppointments = DBAppointments.getAllAppointments();
            LocalDateTime currentTimeMinus15Min = LocalDateTime.now().minusMinutes(15);
            LocalDateTime currentTimePlus15Min = LocalDateTime.now().plusMinutes(15);
            LocalDateTime startTime;
            int getAppointmentID = 0;
            LocalDateTime displayTime = null;
            boolean appointmentWithin15Min = false;

            ResourceBundle rb = ResourceBundle.getBundle("language", Locale.getDefault());

            String usernameInput = uNameTextBox.getText();
            String passwordInput = pWordTextBox.getText();
            int userId = DBUsers.validateUser(usernameInput, passwordInput);

            FileWriter fileWriter = new FileWriter("login_activity.txt", true);
            PrintWriter outputFile = new PrintWriter(fileWriter);

            if (userId > 0) {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/mainScreen.fxml"));
                Parent root = loader.load();
                stage = (Stage) enterButt.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

                //log the successful login
                outputFile.print("user: " + usernameInput + " successfully logged in at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");

                //check for upcoming appointments if user is validated
                for (Appointments appointment: getAllAppointments) {
                    startTime = appointment.getStart();
                    if ((startTime.isAfter(currentTimeMinus15Min) || startTime.isEqual(currentTimeMinus15Min)) && (startTime.isBefore(currentTimePlus15Min) || (startTime.isEqual(currentTimePlus15Min)))) {
                        getAppointmentID = appointment.getAppointmentID();
                        displayTime = startTime;
                        appointmentWithin15Min = true;
                    }
                }
                if (appointmentWithin15Min !=false) {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Appointment within 15 minutes: " + getAppointmentID + " and appointment start time of: " + displayTime);
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("There is an appointment within 15 minutes");
                }

                else {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No upcoming appointments.");
                    Optional<ButtonType> confirmation = alert.showAndWait();
                    System.out.println("no upcoming appointments");
                }
            } else if (userId < 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Incorrect");
                alert.show();

                //log the failed login attempt
                outputFile.print("user: " + usernameInput + " failed login attempt at: " + Timestamp.valueOf(LocalDateTime.now()) + "\n");

            }
            outputFile.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    /**
     * exits Application
     * @param actionEvent
     */
    public void onExitClick(ActionEvent actionEvent) {  {
        System.exit(0);
    }
    }

    /**
     * gets locale information and sets text fields
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try
        {
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);

            ZoneId zone = ZoneId.systemDefault();
            zoneIDField.setText(String.valueOf(zone));

            rb = ResourceBundle.getBundle("language", Locale.getDefault());
            logInLabel.setText(rb.getString("Login"));
            uNameLabel.setText(rb.getString("UserName"));
            pWordLabel.setText(rb.getString("password"));
            enterButt.setText(rb.getString("Enter"));
            exitButt.setText(rb.getString("Exit"));
            zoneIDLabel.setText(rb.getString("Location"));

        } catch(MissingResourceException e) {
            System.out.println("Resource file missing: " + e);
        } catch (Exception e)
        {
            System.out.println(e);
        }

    }


}
