package model;

/**
 * public class for Reports Appointment by Country filters appointment totals for each Country this is my custom report for the project
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */

public class Reports {

    private int countryCount;
    private String countryName;
    public String appointmentMonth;
    public int appointmentTotal;

    /**
     * @param countryName
     * constructor for Custom report includes countryName and CountryCount
     * @param countryCount
     */
    public Reports(String countryName, int countryCount) {
        this.countryCount = countryCount;
        this.countryName = countryName;

    }


    /**
     * Returns country name for custom report.
     * @return countryName
     */
    public String getCountryName() {

        return countryName;
    }

    /**
     * Total for each country.
     * @return countryCount
     */
    public int getCountryCount() {

        return countryCount;
    }

}
