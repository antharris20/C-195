package model;

/**
 * public class for Countries
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

public class Country {
    public int countryID;
    public String countryName;

    /**
     *  constructor for Country includes setters and getters
     * @param countryID
     * @param countryName
     */

    public Country(int countryID, String countryName) {
        this.countryID = countryID;
        this.countryName = countryName;
    }

    /**
     *  gets countryID
     * @return countryID
     */
    public int getCountryID() {
        return countryID;
    }

    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }

    /**
     * gets country name
     * @return countryName
     */
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }


    /**
     * Method that converts hashcode to string to help correctly populate combo boxes
     */
    @Override
    public String toString() {
        return (getCountryName());
    }
}
