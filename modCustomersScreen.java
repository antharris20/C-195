package controller;

import DBAccess.DBCountries;
import DBAccess.DBFirstLevel;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Country;
import model.Customers;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
/**
 * public controller class for Modify Customers Screen that sets up GUI functionality
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

public class modCustomersScreen implements Initializable {
    @FXML
    private Button modCusSaveButt;
    @FXML
    private Button modCusCancel;
    @FXML
    private ComboBox<String> modCusCountry;
    @FXML
    private ComboBox<String> modCusState;
    @FXML
    private TextField modCusID;
    @FXML
    private TextField modCusAddress;
    @FXML
    private TextField modCusPhone;
    @FXML
    private TextField modCusZip;
    @FXML
    private TextField modCusName;
    public Customers selectedCustomer;
    private int countryID;

    public Country selectedCountry;
    private int currentIndex = 0;

    /**
     * this is a method that fills the Modify Customer Screen fields with the proper data from the tableview,
     * upon clicking the modify customers button.
     * @param selectedIndex
     * @param selectedCustomer
     * @throws SQLException
     */

    public void setCustomer(int selectedIndex, Customers selectedCustomer) throws SQLException {
        this.selectedCustomer = selectedCustomer;
        currentIndex = selectedIndex;
        modCusID.setText(String.valueOf(selectedCustomer.getCustomerId()));
        modCusName.setText(selectedCustomer.getCusName());
        modCusPhone.setText(selectedCustomer.getPhone());
        modCusAddress.setText(selectedCustomer.getAddress());
        modCusZip.setText(selectedCustomer.getPostalCode());
        modCusCountry.setValue(String.valueOf(selectedCustomer.getCountryId()));
        modCusState.setValue(String.valueOf(selectedCustomer.getDivisionName()));

        for (Country country : DBCountries.getAllCountries()) {
            if (country.getCountryID() == selectedCustomer.getCountryId()) {
                selectedCountry = country;
                break;}
        }
          assert selectedCountry != null;
        /**
         * Here i'm setting the Country Combobox to initialize with the proper Country Name based of country Id.
          */
        modCusCountry.getSelectionModel().select(String.valueOf(selectedCountry));
          countryID = selectedCountry.getCountryID();



    }

    /**
     * method that saves a modification to a customer
     * @param actionEvent
     */
    public void saveModCus(ActionEvent actionEvent) {
        try {
            Connection connection = JDBC.getConnection();
            if (!modCusName.getText().isEmpty() || !modCusAddress.getText().isEmpty() || !modCusAddress.getText().isEmpty() || !modCusZip.getText().isEmpty() || !modCusPhone.getText().isEmpty() || !modCusCountry.getValue().isEmpty() || !modCusState.getValue().isEmpty())
            {

                int firstLevelDivisionName = 0;
                for (DBFirstLevel firstLevelDivision : DBFirstLevel.getAllFirstLevelDivisions()) {
                    if (modCusState.getSelectionModel().getSelectedItem().equals(firstLevelDivision.getDivisionName())) {
                        firstLevelDivisionName = firstLevelDivision.getDivisionID();
                    }
                }

                String insertStatement = "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, Postal_Code = ?, Phone = ?, Create_Date = ?, Created_By = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?";
                JDBC.setPreparedStatement(JDBC.getConnection(), insertStatement);
                PreparedStatement ps = JDBC.getPreparedStatement();
                ps.setInt(1, Integer.parseInt(modCusID.getText()));
                ps.setString(2, modCusName.getText());
                ps.setString(3, modCusAddress.getText());
                ps.setString(4, modCusZip.getText());
                ps.setString(5, modCusPhone.getText());
                ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(7, "admin");
                ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
                ps.setString(9, "admin");
                ps.setInt(10, firstLevelDivisionName);
                ps.setInt(11, Integer.parseInt(modCusID.getText()));
                ps.execute();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/view/mainScreen.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) modCusSaveButt.getScene().getWindow();
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
    public void cancelModCus(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/mainScreen.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) modCusCancel.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * method that filters the first level divisions list by selected Country
     * @param actionEvent
     */
    public void onModCountry(ActionEvent actionEvent) {
        try {
            JDBC.getConnection();

            String selectedCountry = modCusCountry.getSelectionModel().getSelectedItem();

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

            /**
             * I learned that switch case statements are more viable than if else in certain instances
             */
            switch (selectedCountry) {
                case "U.S" -> modCusState.setItems(flDivisionUS);
                case "UK" -> modCusState.setItems(flDivisionUK);
                case "Canada" -> modCusState.setItems(flDivisionCanada);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onModState(ActionEvent actionEvent) {
    }

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            try {
                ObservableList<DBFirstLevel> firstLevelObservableList = DBFirstLevel.getAllFirstLevelDivisions();
                ObservableList<String> countryNames = FXCollections.observableArrayList();
                ObservableList<Country> clist = DBCountries.getAllCountries();
                ObservableList<String> firstLevelDivisionAllNames = FXCollections.observableArrayList();

                clist.stream().map(Country::getCountryName).forEach(countryNames::add);
                modCusCountry.setItems(countryNames);

                /**
                 * 1.) Lambda tha populates Customer first level divisions combo box
                 */
                firstLevelObservableList.forEach(FirstLevelDivision -> firstLevelDivisionAllNames.add(FirstLevelDivision.getDivisionName()));
                modCusState.setItems(firstLevelDivisionAllNames);



            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

