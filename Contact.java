package model;


/**
 * public class for Contacts
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

public class Contact {
    public int contactID;
    public String name;
    public String email;

    /**
     * contructor for contacts includes setters an getters
     * @param contactID
     * @param name
     * @param email
     */

    public Contact(int contactID, String name, String email) {
        this.contactID = contactID;
        this.name = name;
        this.email = email;
    }


    /**
     * gets contactID
     * @return contactID
     */
    public int getContactID() {
        return contactID;
    }

    public void setContactID(int contactId) {
        this.contactID = contactId;
    }

    /**
     * gets Contact Name
     * @return name
     */

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * gets email
     * @return email
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Method to turn hashcode into string helps populate combo boxes
     */
    public String toString() {
        return (getName());

    }
}