package model;

/**
 * public class for Reports Apoointments by Month includes appointmentMonth and appointmentTotal parameters
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */
public class ReportMonth {
    public String appointmentMonth;
    public int appointmentTotal;

    /**
     * @param appointmentMonth
     * constructor for reports by month includes setters and getters
     * @param appointmentTotal
     */
    public ReportMonth(String appointmentMonth, int appointmentTotal) {
        this.appointmentMonth = appointmentMonth;
        this.appointmentTotal = appointmentTotal;
    }

    /**
     * @return appointmentMonth
     */
    public String getAppointmentMonth() {

        return appointmentMonth;
    }

    /**
     * @return appointmentTotal
     */
    public int getAppointmentTotal() {

        return appointmentTotal;
    }
}
