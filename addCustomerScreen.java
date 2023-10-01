package controller;

import DBAccess.DBCountries;
import DBAccess.DBFirstLevel;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Country;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
/**
 * public controller class for add Customer Screen that sets up GUI functionality
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

public class addCustomerScreen implements Initializable {
    public TextField custId;
    public TextField custName;
    public TextField custAddress;
    public TextField custPhone;
    public ComboBox<String> custState;
    public TextField custZip;
    public Button cusAddButt;
    public Button cusCancelButt;
    public ComboBox<String> custCountry;
    Stage stage;

    /**
     * method for adding customers
     * @param actionEvent
     */

    public void onCusAddClick(ActionEvent actionEvent) {
        try {
            Connection connection = JDBC.getConnection();

            if (!custName.getText().isEmpty() || !custAddress.getText().isEmpty() || !custAddress.getText().isEmpty() || !custZip.getText().isEmpty() || !custPhone.getText().isEmpty() || !custCountry.getValue().isEmpty() || !custState.getValue().isEmpty())
            {

                //create random ID for new customer id
                Integer newCustomerID = (int) (Math.random() * 100);

                int firstLevelDivisionName = 0;
                for (DBFirstLevel firstLevelDivision : DBFirstLevel.getAllFirstLevelDivisions()) {
                    if (custState.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivisionName())) {
                        firstLevelDivisionName = firstLevelDivision.getDivisionID();
                    }
                }
                String insertStatement = "INSERT INTO customers (Customer_ID, Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)";
                JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
                PreparedStatement ps = JDBC.getPreparedStatement();
                ps.setInt(1, newCustomerID);
                ps.setString(2, custName.getText());
                ps.setString(3, custAddress.getText());
                ps.setString(4, custZip.getText());
                ps.setString(5, custPhone.getText());
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(7, "admin");
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, firstLevelDivisionName);
                ps.execute();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/mainScreen.fxml"));
                Parent root = loader.load();
                stage = (Stage) cusCancelButt.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * returns to main screen
     * @param actionEvent
     * @throws IOException
     */
    public void onCusCancelClick(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/mainScreen.fxml"));
        Parent root = loader.load();
        stage = (Stage) cusCancelButt.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * populates Country and first level divisions then filters them accordingly
     * @param actionEvent
     */
    public void onEditCountry(ActionEvent actionEvent) {

        try {
           JDBC.getConnection();

            String selectedCountry = custCountry.getSelectionModel().getSelectedItem();

            ObservableList<DBFirstLevel> getAllFirstLevelDivisions = DBFirstLevel.getAllFirstLevelDivisions();

            ObservableList<String> flDivisionUS = FXCollections.observableArrayList();
            ObservableList<String> flDivisionUK = FXCollections.observableArrayList();
            ObservableList<String> flDivisionCanada = FXCollections.observableArrayList();

            getAllFirstLevelDivisions.forEach(firstLevelDivision -> {
                if (firstLevelDivision.getCountry_ID() == 1) {
                    flDivisionUS.add(firstLevelDivision.getDivisionName());
                } else if (firstLevelDivision.getCountry_ID() == 2) {
                    flDivisionUK.add(firstLevelDivision.getDivisionName());
                } else if (firstLevelDivision.getCountry_ID() == 3) {
                    flDivisionCanada.add(firstLevelDivision.getDivisionName());
                }
            });

            /*
            * I learned the switch clause is more viable than else-if statements if multiple options are available
            * */
            switch (selectedCountry) {
                case "U.S" -> custState.setItems(flDivisionUS);
                case "UK" -> custState.setItems(flDivisionUK);
                case "Canada" -> custState.setItems(flDivisionCanada);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void onEditState(ActionEvent actionEvent) {
    }

    /**
     * fills Country and State Combo Boxes
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<DBFirstLevel> firstLevelObservableList = DBFirstLevel.getAllFirstLevelDivisions();
            ObservableList<String> countryNames = FXCollections.observableArrayList();
            ObservableList<Country> clist = DBCountries.getAllCountries();
            ObservableList<String> firstLevelDivisionAllNames = FXCollections.observableArrayList();

            clist.stream().map(Country::getCountryName).forEach(countryNames::add);
            custCountry.setItems(countryNames);

            /*
            3.) Lambda that passes .getDivisionNames through .add
             */
            firstLevelObservableList.forEach(FirstLevelDivision -> firstLevelDivisionAllNames.add(FirstLevelDivision.getDivisionName()));
            custState.setItems(firstLevelDivisionAllNames);



        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
