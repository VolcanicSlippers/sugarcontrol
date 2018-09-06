package com.karimtimer.sugarcontrol.userAccount;


/**
 * This class will contain several methods that will check the regisrtation fields to see if the correct detaisl has been entered.
 */
public class RegistrationValidationChecks {

    public boolean isEmpty(String field) {
        if (field.length() == 0) {
            return true;
        } else {
            return false;
        }
    }
}
