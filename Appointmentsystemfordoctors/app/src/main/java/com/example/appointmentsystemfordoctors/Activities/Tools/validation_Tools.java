package com.example.appointmentsystemfordoctors.Activities.Tools;
/**
 * This class contains help tools validation
 * functions to the registers aka Patient or Doctor
 */

import android.text.TextUtils;
import android.widget.EditText;

public class validation_Tools {

    /**
     * This function check if the input at Login Activity is valid and correct
     *
     * @param userName
     * @param password
     * @param userName_Input
     * @param password_input
     * @return
     */
    public static boolean isLoginInputValid(String userName, String password, EditText userName_Input,
                                            EditText password_input) {

        if (TextUtils.isEmpty(userName)) {
            userName_Input.setError("User Name is a mandatory field");
            return false;
        }

        if(userName.length() != 10){
            userName_Input.setError("User Name needs to be 10 chars");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            password_input.setError("Password is a mandatory field");
            return false;
        }

        if (password.length() != 9) {
            password_input.setError("Password length need to be 9 digits");
            return false;
        }
        return true;
    }

    /**
     * This function make sure the patient fill his first and last name and id.
     *
     * @param firstName
     * @param lastName
     * @param patientId
     * @param first_nameInput
     * @param last_nameInput
     * @param patientID_input
     * @return
     */
    public static boolean isPatientIsValidIInput(String firstName, String lastName,
                                                 String patientId, String age, EditText first_nameInput,
                                                 EditText last_nameInput, EditText patientID_input,
                                                 EditText ageInput) {

        if (TextUtils.isEmpty(firstName)) {
            first_nameInput.setError("Private name is a mandatory field");
            return false;
        }

        if (TextUtils.isEmpty(lastName)) {
            last_nameInput.setError("Last name is a mandatory field");
            return false;
        }

        if (TextUtils.isEmpty(patientId)) {
            patientID_input.setError("Id is a mandatory field");
            return false;
        }

        return true;
    }

    /**
     * This function make sure the patient fill his Doctor name, DoctorID,
     * what city provided the services currently
     *
     * @param DoctorName
     * @param DoctorID
     * @param cityLiving
     * @param cityInput
     * @param Doctor_nameInput
     * @param DoctorID_Input
     * @return
     */
    public static boolean isDoctorIsValidInput(String DoctorName, String DoctorID,
                                                  String cityLiving,EditText cityInput,
                                                  EditText Doctor_nameInput, EditText DoctorID_Input) {

        if (TextUtils.isEmpty(DoctorName)) {
            Doctor_nameInput.setError("Doctor name is a mandatory field");
            return false;
        }

        if (TextUtils.isEmpty(DoctorID)) {
            DoctorID_Input.setError("Doctor id is a mandatory field");
            return false;
        }

        if (TextUtils.isEmpty(cityLiving)) {
            cityInput.setError("Doctors clinic address is a mandatory field");
            return false;
        }
        return true;
    }

    /**
     * This function make sure the patient and the Doctor fill there email, password and
     * phone number currently
     * This function
     *
     * @param email
     * @param password
     * @param phone
     * @param emailInput
     * @param passwordInput
     * @param phone_numberInput
     * @return
     */
    public static boolean isAllCostumersNeedfulInputIsValid(String email, String password, String phone,
                                                            EditText emailInput, EditText passwordInput,
                                                            EditText phone_numberInput) {
        if (TextUtils.isEmpty(email)) {
            emailInput.setError("Email is a mandatory field");
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            passwordInput.setError("Password is a mandatory field");
            return false;
        }

        if (TextUtils.isEmpty(phone)) {
            phone_numberInput.setError("Phone number is a mandatory field");
            return false;
        }


        if (password.length() != 9) {
            passwordInput.setError("Password must be 9 digits");
            return false;
        }

        return true;
    }

    /**
     * Checking if input of the password is valid or NOT
     * @param password
     * @param password_input
     * @return
     */
    public static boolean isForgetPasswordInputValid(String password, EditText password_input){
        if (TextUtils.isEmpty(password)) {
            password_input.setError("This is a mandatory field");
            return false;
        }

        if (password_input.length() != 9) {
            password_input.setError("Password must be 9 digits");
            return false;
        }

        return true;
    }

    /**
     * Checking if input email and userName is valid
     * @param userName_ID
     * @param email
     * @param userName_ID_input
     * @param email_input
     * @return
     */
    public static boolean isForgetPasswordInputValid_User_email(String userName_ID, String email,
                                                                EditText userName_ID_input ,EditText email_input){
        if (TextUtils.isEmpty(userName_ID)) {
            userName_ID_input.setError("This is a mandatory field");
            return false;
        }

        if (TextUtils.isEmpty(email)) {
            email_input.setError("This is a mandatory field");
            return false;
        }

        return true;
    }

    /**
     * Checking if input string is a long Or integer or not
     * @param Num
     * @param input_Num
     * @return
     */
    public static boolean CheckIfNumber(String Num, EditText input_Num) {
        if (TextUtils.isEmpty(Num)) {
            input_Num.setError("This is a mandatory field");
            return false;
        }

        try {
            Long.parseLong(Num);
        } catch (NumberFormatException ex) {
            input_Num.setError("Must be a number");
            return false;
        }
        return true;
    }
}