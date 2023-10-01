/**
 * public class for Users allows access to User Ids, Names and Passwords
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */


package model;

public class Users {
    public int userId;
    public String userName;
    public String passWord;


    /**
     *
     *
     * @param userId
     * @param userName constructor for User class includes setters and getters for mentioned parameters
     * @param passWord
     */
    public Users(int userId, String userName, String passWord) {
        this.userId = userId;
        this.userName = userName;
        this.passWord = passWord;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }


}
