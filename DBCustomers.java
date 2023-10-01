package DBAccess;

/**
 * public class for Customers that pulls necessary data from database access
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customers;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBCustomers extends Customers{
    public DBCustomers(int customerId, String cusName, String address, String postalCode, String phone, int divisionID, String divisionName, int countryId, String countryName) {
        super(customerId, cusName, address, postalCode, phone, divisionID, divisionName, countryId, countryName);
    }

    /**
     * ObservableList that queries and filters the Customers, Countries and First Level Division tables
     * to gather necessary Data for the TableView
     * @throws SQLException
     */
    public static ObservableList<Customers> getAllCustomers() throws SQLException {

        String sql = "SELECT * FROM customers, first_level_divisions, countries WHERE " +
                "customers.Division_ID = first_level_divisions.Division_ID AND " +
                "first_level_divisions.Country_ID = countries.Country_ID";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        ObservableList<Customers> customersObservableList = FXCollections.observableArrayList();

        while (rs.next()) {
            int customerId = rs.getInt("Customer_ID");
            String cusName = rs.getString("Customer_Name");
            String address = rs.getString("Address");
            String postalCode = rs.getString("Postal_Code");
            String phone = rs.getString("Phone");
            int divisionID = rs.getInt("Division_ID");
            String divisionName = rs.getString("Division");
            int countryId = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");

            Customers customer = new Customers(customerId, cusName, address, postalCode, phone, divisionID, divisionName, countryId, countryName);
            customersObservableList.add(customer);
        }
        return customersObservableList;
    }


}
