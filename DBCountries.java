package DBAccess;

/**
 * public class for Countries that pulls necessary data from database access
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Country;


import java.sql.*;

public class DBCountries extends Country {
    public DBCountries(int countryID, String countryName) {
        super(countryID, countryName);
    }

    /**
     * ObservableList that queries database for necessary information from the Countries for use throughout the Application
     *
     */
    public static ObservableList<Country> getAllCountries(){
        ObservableList<Country> clist = FXCollections.observableArrayList();
        try{
            String sql = "SELECT * FROM countries";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                int country_ID = rs.getInt("Country_ID");
                String countryName = rs.getString("Country");
                Country c = new Country(country_ID, countryName);
                clist.add(c);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        return clist;
    }

    /**
     * test method from WGU Webinar
     */
    public static void checkDateConversion(){
        System.out.println("CREATE DATE TEST");
            String sql = "Select Create_Date from countries";
            try{
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                while(rs.next()){
                    Timestamp ts = rs.getTimestamp("CREATE DATE");
                    System.out.println("CD: " + ts.toLocalDateTime().toString());
                }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}

