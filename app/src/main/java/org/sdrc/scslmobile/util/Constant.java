package org.sdrc.scslmobile.util;



/**
 *  Created by Ratikanta Pradhan
 */

public class Constant {
    public class Result {


        public static final int SUCCESS = 1;
        public static final int NO_INTERNET = 2;
        public static final int ERROR = 3;
        public static final int SERVER_ERROR = 4;
        public static final int INVALID_CREDENTIALS = 5;
        public static final int REQUEST_TIMEOUT = 6;
        public static final int NO_DATA_TO_SYNC = 7;
        public static final int SUCCESS_AFTER_SERVER_HIT = 8;
    }

    public class Type {
        public static final int COREAREA_TYPE_ID = 3;
    }

    public class TypeDetail {
        public static final int INDICATOR_TYPE_PROCESS = 14;
        public static final int INDICATOR_TYPE_INTERMEDIADTE = 17;
        public static final int INDICATOR_TYPE_OUTCOME = 18;
    }

    public class ErrorMessage {
        public static final String NUMERATOR_CAN_NOT_GREATER_THAN_DENOMINATOR = "Numerator should not be greater than denominator";
        public static final String DENOMINATOR_VALUE_MUST_EQUAL_TO = "Denominator value must equals to";
        public static final String value_mismatch_with_previous_input_for_no_of_deliveries = "Value mismatch with previous input for 'Number of deliveries'";
        public static final String value_mismatch_with_previous_input_for_no_of_live_births = "Value mismatch with previous input for 'Number of live births'";
        public static final String number_of_NICU_admissions_should_not_less_than_number_of_inborn_neonates_admitted_to_the_NICU = "Number of NICU admissions should not be less than Number of inborn neonates admitted to the NICU";
        public static final String value_mismatch_with_previous_input_for_no_of_NICU_admissions = "Value mismatch with previous input for 'Number of NICU admissions'";
        public static final String number_of_inborn_neonates_admitted_to_the_NICU_should_not_be_more_than_number_of_NICU_admissions = "Number of inborn neonates admitted to the NICU should not be more than Number of NICU admissions";
        public static final String value_mismatch_with_previous_input_for_no_of_high_risk_deliveries = "Value mismatch with previous input for 'Number of high-risk deliveries'";
        public static final String value_mismatch_with_previous_input_for_no_of_inborn_neonates_admitted_to_the_NICU = "Value mismatch with previous input for 'Number of inborn neonates admitted to the NICU'";
        public static final String value_mismatch_with_previous_input_for_no_of_neonatal_deaths = "Value mismatch with previous input for 'Number of neonatal deaths'";
        public static final String DATA_SUBMITTED_SUCCESSFULLY = "Data submitted Successfully";
        public static final String DENOMINATOR_SHOULD_NOT_GREATER_THAN = "Denominator should not be greater than ";
        public static final String DENOMINATOR_SHOULD_NOT_LESS_THAN = "Denominator should not be less than ";
        public static final String LIVE_BIRTH_CANT_MORE_THAN_DELIVERIES = "'Number of live births' should not more than 'Number of deliveries'";
        public static final String DELIVERIES_CANT_MORE_THAN_LIVE_BIRTH = "'Number of deliveries' should not less than 'Number of live births'";
        public static final String value_mismatch_with_previous_input_for_new_born_less_than_2000gms = "Value mismatch with previous input for 'Total number of new born less than 2000 gms who were given KMC at least four hours a day during stay in SNCUs'";
        public static final String value_mismatch_with_total_admissions = "Value mismatch with previous input for 'Numbers of admissions'";
        public static final String value_should_not_total_admissions = "Value should not be greater than 'Numbers of admissions'";
    }

    public class AreaLevel {
        public static final int BLOCK_AREA_LEVEL = 4;
    }

    public class Table {
        public class TXNSNCUNICUData{
            public static final String ID = "txnIndicatorId";
            public static final String IS_SYNCED = "isSynced";
            public static final String CREATED_DATE = "createdDate";
            public static final String IFTM = "indicatorFacilityTimeperiodMapping";
        }
        public class Area {
            public static final String ID = "areaId";
        }
        public class IndicatorFacilityTimeperiodMapping {
            public static final String ID = "indFacilityTpId";
            public static final String CREATED_DATE = "createdDate";
            public static final String TIME_PERIOD_ID = "timePeriod";
            public static final String INDICATOR_ID = "indicator";
            public static final String IS_NEW = "isNew";

        }
        public class Indicator {
            public static final String ID = "indicatorId";
        }
        public class TimePeriod {
            public static final String ID = "timePeriodId";
            public static final String START_DATE = "startDate";
        }
        public class MSTEngagementScore {
            public static final String ID = "mstEngagementScoreId";
        }
        public class TypeDetail {
            public static final String ID = "typeDetailId";
        }
        public class Type {
            public static final String ID = "typeId";
        }
        public class TXNEngagementScore {
            public static final String CREATED_DATE = "createdDate";
            public static final String IS_SYNCED = "isSynced";
        }
    }

    public class ProfilePageIndicator {
        public static final int NO_OF_INBORN_ADMISSION = 34;
        public static final int NO_OF_OUTBORN_ADMISSION = 35;
        public static final int NO_OF_TOTAL_ADMISSION = 36;
        public static final int PERCENT_OF_INBORN_BABIES = 37;
        public static final int PERCENT_OF_OUTBORN_BABIES = 44;
        public static final int NO_OF_CSECTION_DELIVERY = 38;
        public static final int NO_OF_NORMAL_DELIVERY = 39;
        public static final int NO_OF_TOTAL_DELIVERY = 40;
        public static final int PERCENT_OF_CSECTION_DELIVERIES = 41;
        public static final int PERCENT_OF_NORMAL_DELIVERIES = 42;
        public static final int NO_OF_LIVE_BIRTH = 43;
    }

    public static int SUP_DEAD_LINE_DATE =20;
    public static int MNE_DEAD_LINE_DATE =25;
    public static int DEO_DEAD_LINE_DATE =25;
    public static int ENGAGEMENTSCORE_STARTING_TIME_PERIOD_ID = 27;
    public static String API_VERSION = "2.0.0";

}
