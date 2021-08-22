package com.example.appointmentsystemfordoctors.Activities.Entry;
/**
 * This activity will lead the user to patient register
 * or to institute register
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appointmentsystemfordoctors.R;

public class SignUp_Main extends AppCompatActivity {

    private Button main_register_patient_button;
    private Button main_register_institute_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_main);

        /*patient register button will start to work*/
        main_register_patient_button = (Button) findViewById(R.id.patient_register_button);
        main_register_patient_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPatientRegister_Activity();
            }
        });
        /*end_patient_register_button*/

        /*institute register button will start to work*/
        main_register_institute_button = (Button) findViewById(R.id.institute_register_button);
        main_register_institute_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openInstituteRegister_Activity();
            }
        });
        /*end_institute_register_button*/

    }

    @Override
    public void onBackPressed() {
        Intent open_login = new Intent(this, Main_Activity.class);
        open_login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(open_login);
    }

    /************private function************/
    /*Activate patient register activity*/
    private void openPatientRegister_Activity() {
        Intent open_patient_register = new Intent(this, SignUp_Patient.class);
        startActivity(open_patient_register);
    }

    /*Activate institute register activity*/
    private void openInstituteRegister_Activity() {
        Intent open_institute_register = new Intent(this, SignUp_Doctor.class);
        startActivity(open_institute_register);
    }
}