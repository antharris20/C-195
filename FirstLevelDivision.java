package model;

/**
 * public class for First Level Divisions these are used after a Country is selected to help filter the selection pool
 * Author: Anthony Harris
 * DocDate: 9/30/23
 */
public class FirstLevelDivision {

        private int divisionID;
        private String divisionName;
        public int country_ID;

        /**
         *
         * @param divisionID
         * constructor for FirstLevelDivisions includes setters and getters
         * @param country_ID
         * @param divisionName
         */
        public FirstLevelDivision(int divisionID, String divisionName, int country_ID) {
            this.divisionID = divisionID;
            this.divisionName = divisionName;
            this.country_ID = country_ID;
        }

        /**
         *
         * @return divisionID
         */
        public int getDivisionID() {

            return divisionID;
        }

        /**
         *
         * @return divisionName
         */
        public String getDivisionName() {

            return divisionName;
        }

        /**
         *
         * @return country_ID
         */
        public int getCountry_ID() {

            return country_ID;
        }

    }

