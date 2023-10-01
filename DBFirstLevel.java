package DBAccess;

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.FirstLevelDivision;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBFirstLevel extends FirstLevelDivision {


        public DBFirstLevel(int divisionID, String divisionName, int country_ID) {
            super(divisionID, divisionName, country_ID);
        }

        /**
         * ObservableList that queries the Database to get all First Level Division information.
         * @throws SQLException
         * @return DBFirstLevel
         */
        public static ObservableList<DBFirstLevel> getAllFirstLevelDivisions() throws SQLException {
            ObservableList<DBFirstLevel> firstLevelDivisionsObservableList = FXCollections.observableArrayList();
            String sql = "SELECT * from first_level_divisions";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int divisionID = rs.getInt("Division_ID");
                String divisionName = rs.getString("Division");
                int country_ID = rs.getInt("Country_ID");
                DBFirstLevel firstLevelDivision = new DBFirstLevel(divisionID, divisionName, country_ID);
                firstLevelDivisionsObservableList.add(firstLevelDivision);
            }
            return firstLevelDivisionsObservableList;
        }

    /**
     * method that helps convert hashcode to string and helps populate combo boxes
     */
    @Override
        public String toString(){
            return (getDivisionName());
        }


    }
