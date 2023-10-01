package model;

/**
 * public class for Reports by Type includes appointmentType and appointmentTotal parameters
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */
public class ReportType {
    public String appointmentType;
    public int appointmentTotal;

    /**
     *
     * @param appointmentTotal constructor for Reports filtered by Appointment Type includes setters and getters
     * @param appointmentType
     */
    public ReportType(String appointmentType, int appointmentTotal) {
        this.appointmentType = appointmentType;
        this.appointmentTotal = appointmentTotal;
    }


    public String getAppointmentType() {

        return appointmentType;
    }

    public int getAppointmentTotal() {

        return appointmentTotal;
    }

}
