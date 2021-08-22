package com.example.appointmentsystemfordoctors.Activities.Firebase;

import com.example.appointmentsystemfordoctors.Activities.dataObjects.Login_Input_Data;
import com.google.firebase.database.DataSnapshot;

public class DB_validation {

    private static final String PATIENTS = "Patients";
    private static final String Doctors = "Doctors";
    //*************************************************************//


    /**
     * check if the input user login details is correct
     * @param snapshot
     * @param userName_ID
     * @return
     */
    public static Login_Input_Data checkValidDetails(DataSnapshot snapshot, String PATIENTSorDOCTORS, String userName_ID) {
        String SingInUserName = "";
        if (PATIENTSorDOCTORS.equals(PATIENTS)) {
            SingInUserName = snapshot.child(PATIENTS).child(userName_ID).child("patientID").getValue().toString();
        } else {
            SingInUserName = snapshot.child(Doctors).child(userName_ID).child("doctorID").getValue().toString();
        }
        String SingInPassword = snapshot.child(PATIENTSorDOCTORS).child(userName_ID).child("password").getValue().toString();
        String SingInEmail = snapshot.child(PATIENTSorDOCTORS).child(userName_ID).child("email").getValue().toString();
        Login_Input_Data backFromDB_LOGINdata = new Login_Input_Data(SingInUserName, SingInPassword, SingInEmail);

        return backFromDB_LOGINdata;
    }
}
