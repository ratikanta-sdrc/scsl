package org.sdrc.scslmobile.util;

import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Amit Kumar Sahoo(amit@sdrc.co.in) on 29-04-2017.
 * Ratikanta Pradhan
 */

public class Validation {

    //Check for Numerator should not be greater than denominator
    public boolean validateIsNominatorSmallerThanDenominator(float numerator, float denominator) {
        return (numerator <= denominator);
    }

    //check for Validation2
    public String validateForValidation2(String denominatorString, int indicatorId) {
        List<Integer> indicatorIdsForValidation2 = SCSL.getInstance().getIndicatorIdsForValidation2();
        Map<Integer, Float> indicatorValuesMapForValidation2 = SCSL.getInstance().getIndicatorValuesMapForValidation2();

        if (denominatorString != null && !denominatorString.isEmpty() && !denominatorString.equals("null")) {
            float denominatorValues = Float.parseFloat(denominatorString);
            if (indicatorIdsForValidation2.contains(indicatorId)) {
                if (indicatorValuesMapForValidation2.size() > 0) {
                    if (indicatorValuesMapForValidation2.size() == 1
                            && (Integer) indicatorValuesMapForValidation2.keySet().toArray()[0] == indicatorId) {
                        indicatorValuesMapForValidation2.put(indicatorId, denominatorValues);
                        SCSL.getInstance().setIndicatorValuesMapForValidation2(indicatorValuesMapForValidation2);
                        return null;
                    } else {
                        float valueToCompare = (float) indicatorValuesMapForValidation2.values().toArray()[0];
                        if (denominatorValues == valueToCompare) {
                            indicatorValuesMapForValidation2.put(indicatorId, denominatorValues);
                            SCSL.getInstance().setIndicatorValuesMapForValidation2(indicatorValuesMapForValidation2);
                            return null;
                        } else {
                            indicatorValuesMapForValidation2.remove(indicatorId);
                            SCSL.getInstance().setIndicatorValuesMapForValidation2(indicatorValuesMapForValidation2);
                            return Constant.ErrorMessage.value_mismatch_with_previous_input_for_no_of_deliveries + " (" +  + (int)Math.round(valueToCompare) + ")";
                        }
                    }

                } else {
                    indicatorValuesMapForValidation2.put(indicatorId, denominatorValues);
                    SCSL.getInstance().setIndicatorValuesMapForValidation2(indicatorValuesMapForValidation2);
                    return null;
                }
            } else {
                return null;
            }

        } else {
            // here if we remove one value that id present in check list
            // then that value also must be removed from the map
            if (indicatorIdsForValidation2.contains(indicatorId) &&
                    indicatorValuesMapForValidation2.containsKey(indicatorId)) {
                indicatorValuesMapForValidation2.remove(indicatorId);
                SCSL.getInstance().setIndicatorValuesMapForValidation2(indicatorValuesMapForValidation2);
                return null;
            } else {
                return null;
            }

        }
    }

    //check for Validation3
    public String validateForValidation3(String denominatorString, int indicatorId) {
        List<Integer> indicatorIdsForValidation3 = SCSL.getInstance().getIndicatorIdsForValidation3();
        Map<Integer, Float> indicatorValuesMapForValidation3 = SCSL.getInstance().getIndicatorValuesMapForValidation3();

        if (denominatorString != null && !denominatorString.isEmpty() && !denominatorString.equals("null")) {
            float denominatorValues = Float.parseFloat(denominatorString);
            if (indicatorIdsForValidation3.contains(indicatorId)) {
                if (indicatorValuesMapForValidation3.size() > 0) {
                    if (indicatorValuesMapForValidation3.size() == 1
                            && (Integer) indicatorValuesMapForValidation3.keySet().toArray()[0] == indicatorId) {
                        indicatorValuesMapForValidation3.put(indicatorId, denominatorValues);
                        SCSL.getInstance().setIndicatorValuesMapForValidation3(indicatorValuesMapForValidation3);
                        return null;
                    } else {
                        float valueToCompare = (float) indicatorValuesMapForValidation3.values().toArray()[0];
                        if (denominatorValues == valueToCompare) {
                            indicatorValuesMapForValidation3.put(indicatorId, denominatorValues);
                            SCSL.getInstance().setIndicatorValuesMapForValidation3(indicatorValuesMapForValidation3);
                            return null;
                        } else {
                            indicatorValuesMapForValidation3.remove(indicatorId);
                            SCSL.getInstance().setIndicatorValuesMapForValidation3(indicatorValuesMapForValidation3);
                            return Constant.ErrorMessage.value_mismatch_with_previous_input_for_no_of_live_births + " (" + (int)Math.round(valueToCompare) + ")";
                        }
                    }

                } else {
                    indicatorValuesMapForValidation3.put(indicatorId, denominatorValues);
                    SCSL.getInstance().setIndicatorValuesMapForValidation3(indicatorValuesMapForValidation3);
                    return null;
                }
            } else {
                return null;
            }

        } else {
            // here if we remove one value that id present in check list
            // then that value also must be removed from the map
            if (indicatorIdsForValidation3.contains(indicatorId) &&
                    indicatorValuesMapForValidation3.containsKey(indicatorId)) {
                indicatorValuesMapForValidation3.remove(indicatorId);
                SCSL.getInstance().setIndicatorValuesMapForValidation3(indicatorValuesMapForValidation3);
                return null;
            } else {
                return null;
            }

        }
    }

    public String validateForValidation4(String denominatorString, int indicatorId) {
        List<Integer> indicatorIdsForValidation3 = SCSL.getInstance().getIndicatorIdsForValidation3();
        List<Integer> indicatorIdsForValidation2 = SCSL.getInstance().getIndicatorIdsForValidation2();
        if (denominatorString != null && !denominatorString.isEmpty() && !denominatorString.equals("null")) {
            float denominatorValues = Float.parseFloat(denominatorString);
            if (indicatorIdsForValidation3.contains(indicatorId)) {
                if (SCSL.getInstance().getIndicatorValuesMapForValidation2().size() > 0) {
                    float maxValue = (float) SCSL.getInstance().getIndicatorValuesMapForValidation2().values().toArray()[0];
                    if (maxValue < denominatorValues) {
                        Map<Integer, Float> indicatorValuesMapForValidation3 = SCSL.getInstance().getIndicatorValuesMapForValidation3();
                        indicatorValuesMapForValidation3.remove(indicatorId);
                        SCSL.getInstance().setIndicatorValuesMapForValidation3(indicatorValuesMapForValidation3);
                        return Constant.ErrorMessage.LIVE_BIRTH_CANT_MORE_THAN_DELIVERIES + " (" + (int)Math.round(maxValue) + ")";
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else if (indicatorIdsForValidation2.contains(indicatorId)) {
                if (SCSL.getInstance().getIndicatorValuesMapForValidation3().size() > 0) {
                    float minValue = (float) SCSL.getInstance().getIndicatorValuesMapForValidation3().values().toArray()[0];
                    if (minValue > denominatorValues) {
                        Map<Integer, Float> indicatorValuesMapForValidation2 = SCSL.getInstance().getIndicatorValuesMapForValidation2();
                        indicatorValuesMapForValidation2.remove(indicatorId);
                        SCSL.getInstance().setIndicatorValuesMapForValidation2(indicatorValuesMapForValidation2);
                        return Constant.ErrorMessage.DELIVERIES_CANT_MORE_THAN_LIVE_BIRTH + " (" + (int)Math.round(minValue) + ")";
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    //check for Validation5
    public String validateForValidation5(String denominatorString, int indicatorId) {
        List<Integer> indicatorIdsForValidation5 = SCSL.getInstance().getIndicatorIdsForValidation5();
        Map<Integer, Float> indicatorValuesMapForValidation5 = SCSL.getInstance().getIndicatorValuesMapForValidation5();

        Map<Integer, Float> indicatorValuesMapForValidation7 = SCSL.getInstance().getIndicatorValuesMapForValidation7();
        float minValue = -1;
        if (indicatorValuesMapForValidation7.size() > 0) {
            minValue = (float) indicatorValuesMapForValidation7.values().toArray()[0];
            for (Map.Entry<Integer, Float> entry : indicatorValuesMapForValidation7.entrySet()) {
                if (minValue < entry.getValue()) {
                    minValue = entry.getValue();
                }
            }
        }

        if (denominatorString != null && !denominatorString.isEmpty() && !denominatorString.equals("null")) {
            float denominatorValues = Float.parseFloat(denominatorString);
            if (indicatorIdsForValidation5.contains(indicatorId)) {
                if (indicatorValuesMapForValidation5.size() > 0) {
                    if (indicatorValuesMapForValidation5.size() == 1
                            && (Integer) indicatorValuesMapForValidation5.keySet().toArray()[0] == indicatorId) {
                        if (minValue != -1 && minValue > denominatorValues) {
                            indicatorValuesMapForValidation5.remove(indicatorId);
                            SCSL.getInstance().setIndicatorValuesMapForValidation5(indicatorValuesMapForValidation5);
                            return Constant.ErrorMessage.number_of_NICU_admissions_should_not_less_than_number_of_inborn_neonates_admitted_to_the_NICU + " (" + (int)Math.round(minValue) + ")";
                        } else {
                            indicatorValuesMapForValidation5.put(indicatorId, denominatorValues);
                            SCSL.getInstance().setIndicatorValuesMapForValidation5(indicatorValuesMapForValidation5);
                            return null;
                        }
                    } else {
                        float valueToCompare = (float) indicatorValuesMapForValidation5.values().toArray()[0];
                        if (denominatorValues == valueToCompare) {
                            if (minValue != -1 && minValue > denominatorValues) {
                                indicatorValuesMapForValidation5.remove(indicatorId);
                                SCSL.getInstance().setIndicatorValuesMapForValidation5(indicatorValuesMapForValidation5);
                                return Constant.ErrorMessage.number_of_NICU_admissions_should_not_less_than_number_of_inborn_neonates_admitted_to_the_NICU + " (" + (int)Math.round(minValue) + ")";
                            } else {
                                indicatorValuesMapForValidation5.put(indicatorId, denominatorValues);
                                SCSL.getInstance().setIndicatorValuesMapForValidation5(indicatorValuesMapForValidation5);
                                return null;
                            }
                        } else {
                            indicatorValuesMapForValidation5.remove(indicatorId);
                            SCSL.getInstance().setIndicatorValuesMapForValidation5(indicatorValuesMapForValidation5);
                            return Constant.ErrorMessage.value_mismatch_with_previous_input_for_no_of_NICU_admissions + " (" + (int)Math.round(valueToCompare) + ")";
                        }
                    }

                } else {
                    if (minValue != -1 && minValue > denominatorValues) {
                        indicatorValuesMapForValidation5.remove(indicatorId);
                        SCSL.getInstance().setIndicatorValuesMapForValidation5(indicatorValuesMapForValidation5);
                        return Constant.ErrorMessage.number_of_NICU_admissions_should_not_less_than_number_of_inborn_neonates_admitted_to_the_NICU + " (" + (int)Math.round(minValue) + ")";
                    } else {
                        indicatorValuesMapForValidation5.put(indicatorId, denominatorValues);
                        SCSL.getInstance().setIndicatorValuesMapForValidation5(indicatorValuesMapForValidation5);
                        return null;
                    }
                }
            } else {
                return null;
            }

        } else {
            // here if we remove one value that id present in check list
            // then that value also must be removed from the map
            if (indicatorIdsForValidation5.contains(indicatorId) &&
                    indicatorValuesMapForValidation5.containsKey(indicatorId)) {
                indicatorValuesMapForValidation5.remove(indicatorId);
                SCSL.getInstance().setIndicatorValuesMapForValidation5(indicatorValuesMapForValidation5);
                return null;
            } else {
                return null;
            }

        }
    }

    //check for Validation7
    public String validateForValidation7(String denominatorString, int indicatorId) {
        List<Integer> indicatorIdsForValidation7 = SCSL.getInstance().getIndicatorIdsForValidation7();
        Map<Integer, Float> indicatorValuesMapForValidation5 = SCSL.getInstance().getIndicatorValuesMapForValidation5();
        Map<Integer, Float> indicatorValuesMapForValidation7 = SCSL.getInstance().getIndicatorValuesMapForValidation7();

        if (denominatorString != null && !denominatorString.isEmpty() && !denominatorString.equals("null")) {
            float denominatorValues = Float.parseFloat(denominatorString);
            if (indicatorIdsForValidation7.contains(indicatorId)) {
                if (indicatorValuesMapForValidation5.size() > 0) {
                    float maxValue = (float) indicatorValuesMapForValidation5.values().toArray()[0];
                    if (maxValue < denominatorValues) {
                        indicatorValuesMapForValidation7.remove(indicatorId);
                        SCSL.getInstance().setIndicatorValuesMapForValidation7(indicatorValuesMapForValidation7);
                        return Constant.ErrorMessage.number_of_inborn_neonates_admitted_to_the_NICU_should_not_be_more_than_number_of_NICU_admissions + " (" + (int)Math.round(maxValue) + ")";
                    } else {
                        indicatorValuesMapForValidation7.put(indicatorId, denominatorValues);
                        SCSL.getInstance().setIndicatorValuesMapForValidation7(indicatorValuesMapForValidation7);
                        return null;
                    }

                } else {
                    indicatorValuesMapForValidation7.put(indicatorId, denominatorValues);
                    SCSL.getInstance().setIndicatorValuesMapForValidation7(indicatorValuesMapForValidation7);
                    return null;
                }
            } else {
                return null;
            }

        } else {
            // here if we remove one value that id present in check list
            // then that value also must be removed from the map
            if (indicatorIdsForValidation7.contains(indicatorId) &&
                    indicatorValuesMapForValidation7.containsKey(indicatorId)) {
                indicatorValuesMapForValidation7.remove(indicatorId);
                SCSL.getInstance().setIndicatorValuesMapForValidation7(indicatorValuesMapForValidation7);
                return null;
            } else {
                return null;
            }

        }
    }

    //check for NumberOfHighRiskDeliveries
    public String validateForValidationForNumberOfHighRiskDeliveries(String denominatorString, int indicatorId) {
        List<Integer> indicatorIdsForNumberOfHighRiskDeliveries = SCSL.getInstance().getIndicatorIdsNumberOfHighRiskDeliveries();
        Map<Integer, Float> indicatorValuesMapForNumberOfHighRiskDeliveries = SCSL.getInstance().getIndicatorValuesMapForNumberOfHighRiskDeliveries();

        if (denominatorString != null && !denominatorString.isEmpty() && !denominatorString.equals("null")) {
            float denominatorValues = Float.parseFloat(denominatorString);
            if (indicatorIdsForNumberOfHighRiskDeliveries.contains(indicatorId)) {
                if (indicatorValuesMapForNumberOfHighRiskDeliveries.size() > 0) {
                    if (indicatorValuesMapForNumberOfHighRiskDeliveries.size() == 1
                            && (Integer) indicatorValuesMapForNumberOfHighRiskDeliveries.keySet().toArray()[0] == indicatorId) {

                        indicatorValuesMapForNumberOfHighRiskDeliveries.put(indicatorId, denominatorValues);
                        SCSL.getInstance().setIndicatorValuesMapForNumberOfHighRiskDeliveries(indicatorValuesMapForNumberOfHighRiskDeliveries);
                        return null;

                    } else {
                        float valueToCompare = (float) indicatorValuesMapForNumberOfHighRiskDeliveries.values().toArray()[0];
                        if (denominatorValues == valueToCompare) {

                            indicatorValuesMapForNumberOfHighRiskDeliveries.put(indicatorId, denominatorValues);
                            SCSL.getInstance().setIndicatorValuesMapForNumberOfHighRiskDeliveries(indicatorValuesMapForNumberOfHighRiskDeliveries);
                            return null;

                        } else {
                            indicatorValuesMapForNumberOfHighRiskDeliveries.remove(indicatorId);
                            SCSL.getInstance().setIndicatorValuesMapForNumberOfHighRiskDeliveries(indicatorValuesMapForNumberOfHighRiskDeliveries);
                            return Constant.ErrorMessage.value_mismatch_with_previous_input_for_no_of_high_risk_deliveries + " (" + (int)Math.round(valueToCompare) + ")";
                        }
                    }

                } else {

                    indicatorValuesMapForNumberOfHighRiskDeliveries.put(indicatorId, denominatorValues);
                    SCSL.getInstance().setIndicatorValuesMapForNumberOfHighRiskDeliveries(indicatorValuesMapForNumberOfHighRiskDeliveries);
                    return null;

                }
            } else {
                return null;
            }

        } else {
            // here if we remove one value that id present in check list
            // then that value also must be removed from the map
            if (indicatorIdsForNumberOfHighRiskDeliveries.contains(indicatorId) &&
                    indicatorValuesMapForNumberOfHighRiskDeliveries.containsKey(indicatorId)) {
                indicatorValuesMapForNumberOfHighRiskDeliveries.remove(indicatorId);
                SCSL.getInstance().setIndicatorValuesMapForNumberOfHighRiskDeliveries(indicatorValuesMapForNumberOfHighRiskDeliveries);
                return null;
            } else {
                return null;
            }

        }
    }

    //check for NumberOfInbornNeonatesAdmittedToTheNICU
    public String validateForValidationForNumberOfInbornNeonatesAdmittedToTheNICU(String denominatorString, int indicatorId) {
        List<Integer> indicatorIdsForNumberOfInbornNeonatesAdmittedToTheNICU = SCSL.getInstance().getIndicatorIdsNumberOfInbornNeonatesAdmittedToTheNICU();
        Map<Integer, Float> indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU = SCSL.getInstance().getIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU();

        if (denominatorString != null && !denominatorString.isEmpty() && !denominatorString.equals("null")) {
            float denominatorValues = Float.parseFloat(denominatorString);
            if (indicatorIdsForNumberOfInbornNeonatesAdmittedToTheNICU.contains(indicatorId)) {
                if (indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.size() > 0) {
                    if (indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.size() == 1
                            && (Integer) indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.keySet().toArray()[0] == indicatorId) {

                        indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.put(indicatorId, denominatorValues);
                        SCSL.getInstance().setIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU(indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU);
                        return null;

                    } else {
                        float valueToCompare = (float) indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.values().toArray()[0];
                        if (denominatorValues == valueToCompare) {

                            indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.put(indicatorId, denominatorValues);
                            SCSL.getInstance().setIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU(indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU);
                            return null;

                        } else {
                            indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.remove(indicatorId);
                            SCSL.getInstance().setIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU(indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU);
                            return Constant.ErrorMessage.value_mismatch_with_previous_input_for_no_of_inborn_neonates_admitted_to_the_NICU + " (" + (int)Math.round(valueToCompare) + ")";
                        }
                    }

                } else {
                    indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.put(indicatorId, denominatorValues);
                    SCSL.getInstance().setIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU(indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU);
                    return null;

                }
            } else {
                return null;
            }

        } else {
            // here if we remove one value that id present in check list
            // then that value also must be removed from the map
            if (indicatorIdsForNumberOfInbornNeonatesAdmittedToTheNICU.contains(indicatorId) &&
                    indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.containsKey(indicatorId)) {
                indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.remove(indicatorId);
                SCSL.getInstance().setIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU(indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU);
                return null;
            } else {
                return null;
            }

        }
    }

    //check for NumberOfNeonatalDeaths
    public String validateForValidationForNumberOfNeonatalDeaths(String denominatorString, int indicatorId) {
        List<Integer> indicatorIdsNumberOfNeonatalDeathsDinominator = SCSL.getInstance().getIndicatorIdsNumberOfNeonatalDeathsDinominator();
        List<Integer> indicatorIdsNumberOfNeonatalDeathsNumerator = SCSL.getInstance().getIndicatorIdsNumberOfNeonatalDeathsNumerator();
        Map<Integer, Float> indicatorValuesMapForNumberOfNeonatalDeaths = SCSL.getInstance().getIndicatorValuesMapForNumberOfNeonatalDeaths();

        if (denominatorString != null && !denominatorString.isEmpty() && !denominatorString.equals("null")) {
            float denominatorValues = Float.parseFloat(denominatorString);
            if (indicatorIdsNumberOfNeonatalDeathsDinominator.contains(indicatorId) || indicatorIdsNumberOfNeonatalDeathsNumerator.contains(indicatorId)) {
                if (indicatorValuesMapForNumberOfNeonatalDeaths.size() > 0) {
                    if (indicatorValuesMapForNumberOfNeonatalDeaths.size() == 1
                            && (Integer) indicatorValuesMapForNumberOfNeonatalDeaths.keySet().toArray()[0] == indicatorId) {

                        indicatorValuesMapForNumberOfNeonatalDeaths.put(indicatorId, denominatorValues);
                        SCSL.getInstance().setIndicatorValuesMapForNumberOfNeonatalDeaths(indicatorValuesMapForNumberOfNeonatalDeaths);
                        return null;

                    } else {
                        float valueToCompare = (float) indicatorValuesMapForNumberOfNeonatalDeaths.values().toArray()[0];
                        if (denominatorValues == valueToCompare) {
                            indicatorValuesMapForNumberOfNeonatalDeaths.put(indicatorId, denominatorValues);
                            SCSL.getInstance().setIndicatorValuesMapForNumberOfNeonatalDeaths(indicatorValuesMapForNumberOfNeonatalDeaths);
                            return null;

                        } else {
                            indicatorValuesMapForNumberOfNeonatalDeaths.remove(indicatorId);
                            SCSL.getInstance().setIndicatorValuesMapForNumberOfNeonatalDeaths(indicatorValuesMapForNumberOfNeonatalDeaths);
                            return Constant.ErrorMessage.value_mismatch_with_previous_input_for_no_of_neonatal_deaths + " (" + (int)Math.round(valueToCompare) + ")";
                        }
                    }

                } else {
                    indicatorValuesMapForNumberOfNeonatalDeaths.put(indicatorId, denominatorValues);
                    SCSL.getInstance().setIndicatorValuesMapForNumberOfNeonatalDeaths(indicatorValuesMapForNumberOfNeonatalDeaths);
                    return null;

                }
            } else {
                return null;
            }

        } else {
            // here if we remove one value that id present in check list
            // then that value also must be removed from the map
            if (indicatorIdsNumberOfNeonatalDeathsDinominator.contains(indicatorId) || indicatorIdsNumberOfNeonatalDeathsNumerator.contains(indicatorId)) {
                indicatorValuesMapForNumberOfNeonatalDeaths.remove(indicatorId);
                SCSL.getInstance().setIndicatorValuesMapForNumberOfNeonatalDeaths(indicatorValuesMapForNumberOfNeonatalDeaths);
                return null;
            } else {
                return null;
            }

        }
    }

    //check for Total number of new born less than 2000 gms in indicator no 29(N) and 31(D)
    public String validateForValidationForNoOfNewBornLessThan2000gms(String denominatorString, int indicatorId) {
        List<Integer> noOfNewBornLessThan2000gmsInDinominators = SCSL.getInstance().getNoOfNewBornLessThan2000gmsInDinominators();
        List<Integer> noOfNewBornLessThan2000gmsInNumerators = SCSL.getInstance().getNoOfNewBornLessThan2000gmsInNumerators();
        Map<Integer, Float> indicatorValuesMapForNoOfNewBornLessThan2000gms = SCSL.getInstance().getIndicatorValuesMapForNoOfNewBornLessThan2000gms();

        if (denominatorString != null && !denominatorString.isEmpty() && !denominatorString.equals("null")) {
            float denominatorValues = Float.parseFloat(denominatorString);
            if (noOfNewBornLessThan2000gmsInDinominators.contains(indicatorId) || noOfNewBornLessThan2000gmsInNumerators.contains(indicatorId)) {
                if (indicatorValuesMapForNoOfNewBornLessThan2000gms.size() > 0) {
                    if (indicatorValuesMapForNoOfNewBornLessThan2000gms.size() == 1
                            && (Integer) indicatorValuesMapForNoOfNewBornLessThan2000gms.keySet().toArray()[0] == indicatorId) {

                        indicatorValuesMapForNoOfNewBornLessThan2000gms.put(indicatorId, denominatorValues);
                        SCSL.getInstance().setIndicatorValuesMapForNoOfNewBornLessThan2000gms(indicatorValuesMapForNoOfNewBornLessThan2000gms);
                        return null;

                    } else {
                        float valueToCompare = (float) indicatorValuesMapForNoOfNewBornLessThan2000gms.values().toArray()[0];
                        if (denominatorValues == valueToCompare) {
                            indicatorValuesMapForNoOfNewBornLessThan2000gms.put(indicatorId, denominatorValues);
                            SCSL.getInstance().setIndicatorValuesMapForNoOfNewBornLessThan2000gms(indicatorValuesMapForNoOfNewBornLessThan2000gms);
                            return null;

                        } else {
                            indicatorValuesMapForNoOfNewBornLessThan2000gms.remove(indicatorId);
                            SCSL.getInstance().setIndicatorValuesMapForNoOfNewBornLessThan2000gms(indicatorValuesMapForNoOfNewBornLessThan2000gms);
                            return Constant.ErrorMessage.value_mismatch_with_previous_input_for_new_born_less_than_2000gms + " (" + (int)Math.round(valueToCompare) + ")";
                        }
                    }

                } else {
                    indicatorValuesMapForNoOfNewBornLessThan2000gms.put(indicatorId, denominatorValues);
                    SCSL.getInstance().setIndicatorValuesMapForNoOfNewBornLessThan2000gms(indicatorValuesMapForNoOfNewBornLessThan2000gms);
                    return null;

                }
            } else {
                return null;
            }

        } else {
            // here if we remove one value that id present in check list
            // then that value also must be removed from the map
            if (noOfNewBornLessThan2000gmsInDinominators.contains(indicatorId) || noOfNewBornLessThan2000gmsInNumerators.contains(indicatorId)) {
                indicatorValuesMapForNoOfNewBornLessThan2000gms.remove(indicatorId);
                SCSL.getInstance().setIndicatorValuesMapForNoOfNewBornLessThan2000gms(indicatorValuesMapForNoOfNewBornLessThan2000gms);
                return null;
            } else {
                return null;
            }

        }
    }

    public String validateTotalNoOfAdmission(String textValue, float totalAdmissionInPR){
        if (!textValue.isEmpty()){
            float value = Float.parseFloat(textValue);
            if (totalAdmissionInPR < value){
                return Constant.ErrorMessage.value_should_not_total_admissions + " (" + (int)Math.round(totalAdmissionInPR) + ")";
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * This following method will validate username and password whether they area empty strings or not
     *
     * @param username The username
     * @param password The password
     * @return True if neither of username and password is blank string, False if any of username or password is blank string
     * Ratikanta Pradhan
     */
    public boolean validateCredentials(String username, String password) {
        return username.equals("") || password.equals("");
    }



    public String formatDate(String date) {
        if (date != null) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                Date d = format.parse(date);
                SimpleDateFormat serverFormat = new SimpleDateFormat("yyyy.MM.dd");
                return serverFormat.format(d);
            } catch (Exception e) {
                e.printStackTrace();
                return "";
            }
        }
        return "";

    }

    public void removeIndicatorFromAllValidationMap(int id){
        Map<Integer, Float> indicatorValuesMapForValidation2 = SCSL.getInstance().getIndicatorValuesMapForValidation2();
        indicatorValuesMapForValidation2.remove(id);
        SCSL.getInstance().setIndicatorValuesMapForValidation2(indicatorValuesMapForValidation2);

        Map<Integer, Float> indicatorValuesMapForValidation3 = SCSL.getInstance().getIndicatorValuesMapForValidation3();
        indicatorValuesMapForValidation3.remove(id);
        SCSL.getInstance().setIndicatorValuesMapForValidation3(indicatorValuesMapForValidation3);

        Map<Integer, Float> indicatorValuesMapForValidation5 = SCSL.getInstance().getIndicatorValuesMapForValidation5();
        indicatorValuesMapForValidation5.remove(id);
        SCSL.getInstance().setIndicatorValuesMapForValidation5(indicatorValuesMapForValidation5);

        Map<Integer, Float> indicatorValuesMapForValidation7 = SCSL.getInstance().getIndicatorValuesMapForValidation7();
        indicatorValuesMapForValidation7.remove(id);
        SCSL.getInstance().setIndicatorValuesMapForValidation7(indicatorValuesMapForValidation7);

        Map<Integer, Float> indicatorValuesMapForNumberOfHighRiskDeliveries = SCSL.getInstance().getIndicatorValuesMapForNumberOfHighRiskDeliveries();
        indicatorValuesMapForNumberOfHighRiskDeliveries.remove(id);
        SCSL.getInstance().setIndicatorValuesMapForNumberOfHighRiskDeliveries(indicatorValuesMapForNumberOfHighRiskDeliveries);

        Map<Integer, Float> indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU = SCSL.getInstance().getIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU();
        indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU.remove(id);
        SCSL.getInstance().setIndicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU(indicatorValuesMapForNumberOfInbornNeonatesAdmittedToTheNICU);

        Map<Integer, Float> indicatorValuesMapForNumberOfNeonatalDeaths = SCSL.getInstance().getIndicatorValuesMapForNumberOfNeonatalDeaths();
        indicatorValuesMapForNumberOfNeonatalDeaths.remove(id);
        SCSL.getInstance().setIndicatorValuesMapForNumberOfNeonatalDeaths(indicatorValuesMapForNumberOfNeonatalDeaths);

        Map<Integer, Float> indicatorValuesMapForNoOfNewBornLessThan2000gms = SCSL.getInstance().getIndicatorValuesMapForNoOfNewBornLessThan2000gms();
        indicatorValuesMapForNoOfNewBornLessThan2000gms.remove(id);
        SCSL.getInstance().setIndicatorValuesMapForNoOfNewBornLessThan2000gms(indicatorValuesMapForNoOfNewBornLessThan2000gms);
    }

    public String checkForLiveBirthAndDelivery(String noOfLiveBirth, String noOfNormalDelivery){
        if( noOfLiveBirth.length()>0
                && noOfNormalDelivery.length()>0){
            int noOfLivebirth = Integer.parseInt(noOfLiveBirth);
            int noOfnormaldelivery = Integer.parseInt(noOfNormalDelivery);
            if(noOfLivebirth >noOfnormaldelivery){
                return "No of live birth ("+noOfLivebirth+") should not be greater than Number of total deliveries (" +noOfnormaldelivery+")";
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
