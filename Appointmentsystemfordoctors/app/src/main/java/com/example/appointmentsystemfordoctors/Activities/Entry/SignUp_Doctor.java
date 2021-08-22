package com.example.appointmentsystemfordoctors.Activities.Entry;
/**
 * This activity create a new Doctor account and save it in the DB
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appointmentsystemfordoctors.R;
import com.example.appointmentsystemfordoctors.Activities.Tools.validation_Tools;
import com.example.appointmentsystemfordoctors.Activities.dataObjects.Address;
import com.example.appointmentsystemfordoctors.Activities.dataObjects.ClientDoctors;
import com.example.appointmentsystemfordoctors.Activities.dataObjects.LockedAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SignUp_Doctor extends AppCompatActivity {

    private EditText Doctor_nameInput, DoctorID_Input, emailInput, phone_numberInput;
    private EditText passwordInput, cityInput, streetInput, building_numberInput;

    private Button registerDoctor_button;
    private ProgressBar progressBar;

    private FirebaseDatabase dataBase;
    private DatabaseReference myDataBase;
    private FirebaseAuth fAuto;

    private static final String Doctors = "Doctors";

    private ClientDoctors costumer_details_Doctor;
    private Address DoctorAddress;
    private LockedAccount lockedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_doctor);

        Doctor_nameInput = findViewById(R.id.user_first_name_input_register_Doctor);
        DoctorID_Input = findViewById(R.id.user_ID_input_register_Doctor);
        emailInput = findViewById(R.id.user_email_input_register_Doctor);
        phone_numberInput = findViewById(R.id.user_phone_input_register_Doctor);
        passwordInput = findViewById(R.id.user_password_input_register_Doctor);
        cityInput = findViewById(R.id.user_living_city_input_register_Doctor);
        streetInput = findViewById(R.id.user_living_street_input_register_Doctor);
        building_numberInput = findViewById(R.id.user_house_number_input_register_Doctor);

        /*Buttons_connection*/
        registerDoctor_button = findViewById(R.id.registr_new_Doctor_button);
        progressBar = findViewById(R.id.user_progress_bar_Doctor);
        /*end_Buttons_connection*/

        /*FireBase_connection*/
        dataBase = FirebaseDatabase.getInstance();
        myDataBase = dataBase.getReference(Doctors);
        fAuto=FirebaseAuth.getInstance();
        /*end_FireBase_connection*/


        //*************************************************************//

        registerDoctor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String DoctorName = Doctor_nameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String phone = phone_numberInput.getText().toString().trim();
                String DoctorID = DoctorID_Input.getText().toString().trim();
                String cityLiving = cityInput.getText().toString().trim();

                /*checking if the inputs is valid inputs*/
                if (!validation_Tools.CheckIfNumber(DoctorID, DoctorID_Input)) { return; }
                if (!validation_Tools.CheckIfNumber(phone, phone_numberInput)) { return; }
                if (!validation_Tools.isDoctorIsValidInput(DoctorName, DoctorID, cityLiving,
                        cityInput, Doctor_nameInput, DoctorID_Input)) { return; }
                if (!validation_Tools.isAllCostumersNeedfulInputIsValid(email, password, phone,
                        emailInput, passwordInput, phone_numberInput)) { return; }
                /*end_validation_checking*/

                /*checking if the user is already exist, if not added the user*/
                Query IDCheckingExistence = myDataBase.orderByChild("DoctorID").equalTo(DoctorID);
                IDCheckingExistence.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() > 0) {
                            Toast.makeText(SignUp_Doctor.this, "This username is already used", Toast.LENGTH_LONG).show();
                            openLogin_Activity();
                        } else {
                            /*continue with other inputs after validation*/
                            String streetLiving = streetInput.getText().toString().trim();
                            String buildingNumber = building_numberInput.getText().toString().trim();
                            /*end_all_inputs*/

                            /*create a new Patient*/
                            DoctorAddress = new Address(cityLiving, streetLiving, buildingNumber);
                            lockedAccount  =new LockedAccount("false","0");
                            costumer_details_Doctor = new ClientDoctors(email, phone,
                                    password, DoctorAddress,lockedAccount, DoctorName, "i:"+DoctorID);

                            /**all the needful details are enters, can move to register
                             *the user in fireBase
                             */
                            registerDoctor();
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
    }

    /*Adding patient to our Firebase DataBase - not real DB*/
    private void registerDoctor() {
        fAuto.createUserWithEmailAndPassword(this.costumer_details_Doctor.getEmail(), this.costumer_details_Doctor.getPassword())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            registerPatientToRealDB();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = fAuto.getCurrentUser();
                            update_Authentication(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(SignUp_Doctor.this, "something is not right in the email address, try again", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), Login_Activity.class));
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent open_login = new Intent(this, Main_Activity.class);
        open_login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(open_login);
    }

    /************private function************/
    private void update_Authentication(FirebaseUser currentDoctorUser) {
        Intent open_email_verification = new Intent(this, EMail_Verification_Activity.class);
        open_email_verification.putExtra("DoctorUser", currentDoctorUser);
        open_email_verification.putExtra("userName_ID",this.costumer_details_Doctor.getDoctorID());
        startActivity(open_email_verification);
    }

    /*Activate login activity*/
    private void openLogin_Activity() {
        Intent open_login = new Intent(this, Login_Activity.class);
        startActivity(open_login);
    }

    /*Adding patient to our Firebase DataBase*/
    private void registerPatientToRealDB() {
        myDataBase.child(costumer_details_Doctor.getDoctorID()).setValue(this.costumer_details_Doctor);
        myDataBase.child(costumer_details_Doctor.getDoctorID()).child("Availability").setValue("True");
    }
}