package DBAccess;

/**
 * public class for User database access
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUsers extends Users {
    public DBUsers(int userId, String userName, String passWord) {
        super(userId, userName, passWord);
    }

    /**
     * ObservableList that queries the database to get User Information
     * @return
     * @throws SQLException
     */

    public static ObservableList<DBUsers> getAllUsers() throws SQLException {
        ObservableList<DBUsers> usersObservableList = FXCollections.observableArrayList();
        String sql = "SELECT * from users";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int userID = rs.getInt("User_ID");
            String userName = rs.getString("User_Name");
            String userPassword = rs.getString("Password");
            DBUsers user = new DBUsers(userID, userName, userPassword);
            usersObservableList.add(user);
        }
        return usersObservableList;
    }

    /**
     * method that validates user name and password on attempted logins
     * @param userName
     * @param passWord
     */
    public static int validateUser(String userName, String passWord)
    {
        try
        {
            String sqlQuery = "SELECT * FROM users WHERE user_name = '" + userName + "' AND password = '" + passWord +"'";

            PreparedStatement ps = JDBC.getConnection().prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            rs.next();
            if (rs.getString("User_Name").equals(userName))
            {
                if (rs.getString("Password").equals(passWord))
                {
                    return rs.getInt("User_ID");

                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return -1;
    }
}
