package model;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * public class for Customers
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

public class Customers {
    private int customerId;
    private String cusName;
    private String address;
    private String postalCode;
    private String phone;
    private LocalDateTime creation;
    private String createdBy;
    private Timestamp updated;
    private String upBy;
    private int divisionId;
    private String divisionName;

    private int countryId;
    private String countryName;

    /**
     * constructor for Customer includes setters and getters
     * @param customerId
     * @param cusName
     * @param address
     * @param postalCode
     * @param phone
     * @param divisionId
     * @param divisionName
     * @param countryId
     * @param countryName
     */


    public Customers(int customerId, String cusName, String address, String postalCode,
                     String phone, int divisionId, String divisionName, int countryId, String countryName) {

        this.customerId = customerId;
        this.cusName = cusName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
        this.divisionName = divisionName;


        this.countryId = countryId;
        this.countryName = countryName;
    }

    /**
     * gets customerId
     * @return customerId
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * set customerId
     * @param customerId
     */
    public void setCusId(int customerId) {
        this.customerId = customerId;
    }

    /**
     * get customer name
     * @return cusName
     */
    public String getCusName() {
        return cusName;
    }

    public void setCusName(String cusName) {
        this.cusName = cusName;
    }

    /**
     * get customer address
     * @return address
     */
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *  get customer postal code
     * @return postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * get customer phone
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * gets creation date
     * @return creation
     */
    public LocalDateTime getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation;
    }

    /**
     * get created by
     * @return createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * gets update date
     * @return updated
     */
    public Timestamp getUpdated() {
        return updated;
    }

    public void setUpdated(Timestamp updated) {
        this.updated = updated;
    }

    /**
     * gets who updated
     * @return upBy
     */
    public String getUpBy() {
        return upBy;
    }

    public void setUpBy(String upBy) {
        this.upBy = upBy;
    }

    /**
     * gets division Id
     * @return divisionId
     */
    public int getDivisionId() {
        return divisionId;
    }

    public void setDivisionId(int divisionId) {
        this.divisionId = divisionId;
    }

    /**
     * get division name
     * @return divisionName
     */
    public String getDivisionName() {

        return divisionName;
    }

    /**
     * Method that converts hashcode to string to help correctly populate combo boxes
     */
    @Override
    public String toString() {

        return ("[" + Integer.toString(countryId) + "] " + countryName);


   }

    /**
     * gets country id
     * @return countryId
     */
    public int getCountryId() {
        return countryId;
    }

    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }

    /**
     * get country name
     * @return countryName
     */
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
