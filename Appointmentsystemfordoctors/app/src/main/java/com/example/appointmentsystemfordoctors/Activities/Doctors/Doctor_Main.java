package com.example.appointmentsystemfordoctors.Activities.Doctors;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.appointmentsystemfordoctors.Activities.Comperators.ArrivelsComperator;
import com.example.appointmentsystemfordoctors.Activities.dataObjects.ClientPatient;
import com.example.appointmentsystemfordoctors.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;

public class Doctor_Main extends AppCompatActivity {

    Context context = this;

    DatabaseReference Pat_DB = FirebaseDatabase.getInstance().getReference().child("Patients");
    DatabaseReference Doc_DB = FirebaseDatabase.getInstance().getReference().child("Doctors");
    DatabaseReference Waiting_List_DB = FirebaseDatabase.getInstance().getReference().child("Doc_Waiting_List");

    String doctor_id;

    ListView waiting_lst;
    TextView pat_det;
    TextView no_app_doc;
    TextView no_app_wl;
    TextView Time_Rem;
    TextView pls_ntc;
    TextView Waiting_lst_empty;
    TextView ArrivalTime;
    TextView Pat_Name;


    long Time_Left = 0;

    private CountDownTimer countDownTimer;

    static final double _APP_TIME = 0.1; // X time for an appointment. modify as you want by MINUTES. By default set to 15 minutes
    //For debug , you can modify this variable to 0.1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_main);

        //doctor_id= "i:111111111"; //DEBUG
        doctor_id = getIntent().getStringExtra("doctor_id"); //REAL - TIME

        Time_Rem = (TextView) findViewById(R.id.Time_Rem);
        pls_ntc =  (TextView) findViewById(R.id.pls_notice);
        pat_det = (TextView) findViewById(R.id.Patient_det);
        no_app_doc = (TextView) findViewById(R.id.No_App_Doc);
        no_app_doc.setVisibility(View.INVISIBLE);
        no_app_wl = (TextView) findViewById(R.id.No_App_wl);
        waiting_lst = (ListView) findViewById (R.id.Waiting_List2);
        Waiting_lst_empty = (TextView) findViewById (R.id.Waiting_list_empty);
        Waiting_lst_empty.setVisibility(View.INVISIBLE);
        Pat_Name = (TextView) findViewById(R.id.Pat_Name);
        ArrivalTime = (TextView) findViewById(R.id.ArrivalTime);

        ShowCurrApp();
    }

    /**
     * Shows The current appointment of the doctor
     * **/
    private void ShowCurrApp() {
        Doc_DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(doctor_id).child("Current Appointment").exists())// if the doctor even have patient now
                {
                    if (snapshot.child(doctor_id).child("Current Appointment").child("Appointment start time").exists()) // another check
                    {
                        String patient_id = (String) snapshot.child(doctor_id).child("Current Appointment").child("Patient_Id").getValue();

                        Pat_DB.child(patient_id).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String first_name = (String) snapshot.child("first_name").getValue();
                                if (first_name == null) {
                                    noAPP(); // there is no appointment at this time
                                } else {
                                    String second_name = (String) snapshot.child("second_name").getValue();
                                    String age = (String) snapshot.child("age").getValue();
                                    pat_det.setText(first_name + " " + second_name + "  ,  " + age);
                                    ShowWaitingList();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {
                        noAPP();
                    }
                }
                else
                {
                    noAPP();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    /**
     * Shows The current waiting list
     * param Patients_obj holds patients as object, Patients holds patients as strings for showing in the list
     * and param Patients_Ids is a hush containing the patient id as key and time of entering the waiting list as value
     */
    private void ShowWaitingList() {
        ArrayList<ClientPatient> Patients_Obj = new ArrayList<>();
        ArrayList<String> Patients = new ArrayList<>();
        Hashtable<String,Object> Patients_Ids = new Hashtable<>();//Hash table where key = Patient id

        Waiting_List_DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot doc:snapshot.getChildren()
                ) {
                    if(doc.getKey().equals(doctor_id))
                    {
                        for (DataSnapshot patient:doc.getChildren()
                        ) {
                            Patients_Ids.put(patient.getKey(),patient.child("Arrival Time").getValue());
                        }
                    }
                }
                Pat_DB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot patient:snapshot.getChildren()
                        ) {
                            for (String id:Patients_Ids.keySet()
                            ) {
                                if (id.equals(patient.getKey()))
                                {
                                    String firstName = (String) patient.child("first_name").getValue();
                                    String lastname = (String) patient.child("second_name").getValue();
                                    String Phone_Number = (String) patient.child("phone_number").getValue();
                                    String Age = (String) patient.child("age").getValue();
                                    HashMap<String,Long> Arrival_Time = (HashMap<String, Long>) Patients_Ids.get(id);
                                    long Hour = Arrival_Time.get("hour");
                                    long Minute = Arrival_Time.get("minute");
                                    ClientPatient patient_obj = new ClientPatient(Phone_Number,
                                            firstName,lastname,Age,id,Hour,Minute);
                                    Patients_Obj.add(patient_obj);
                                }
                            }
                        }
                        if(Patients_Obj.size() == 0)
                        {
                            no_app_wl.setVisibility(View.INVISIBLE);
                            Waiting_lst_empty.setVisibility(View.VISIBLE);
                            ArrivalTime.setVisibility(View.INVISIBLE);
                            Waiting_lst_empty.setText("Waiting list empty");
                            Pat_Name.setVisibility(View.INVISIBLE);
                        }
                        else {
                            Collections.sort(Patients_Obj, new ArrivelsComperator()); // sorting arrivals comparator
                            for (ClientPatient patient : Patients_Obj
                            ) {
                                Patients.add(patient.TostringWithArrival());
                            }
                            ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.simple_list_view, R.id.textView, Patients);
                            waiting_lst.setAdapter(adapter);
                        }
                        startTicking(Time_Left);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    /**
     * Sets the screen where there is no current patient
     **/
    private void noAPP()
    {
        no_app_doc.setVisibility(View.VISIBLE);
        no_app_doc.setText("Look like you don't have any appointment right now");
        pat_det.setVisibility(View.INVISIBLE);
        Time_Rem.setVisibility(View.INVISIBLE);
        pls_ntc.setVisibility(View.INVISIBLE);
        no_app_wl.setVisibility(View.INVISIBLE);
    }

    /**
     * starts the count down. when finish, update the waiting list as needed
     * @param time_Left is how much time left in milliseconds
     **/
    private void startTicking(long time_Left) {
        time_Left = (long) (_APP_TIME * 60000);
        countDownTimer = new CountDownTimer(time_Left,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Time_Left = millisUntilFinished;
                int minute = (int) Time_Left / 60000;
                int seconds = (int) Time_Left % 60000 / 1000;
                String time_string = "" ;
                time_string += minute + ":";
                if(seconds < 10)
                    time_string += "0" + seconds;
                else time_string += seconds;
                Time_Rem.setText(time_string);
            }

            @Override
            public void onFinish() {
                ForwardWL(doctor_id,0);
            }
        }.start();
    }


    /**
     * Updates the waiting list as needed.
     * @param Doc_id the id of the doc we want to update the waiting list for his appointment
     * @param place the place of the patient that canceled his appointment in the waiting list
     **/
    public void ForwardWL(String Doc_id, long place) {
        Waiting_List_DB.child(Doc_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String First_Pat = " ";
                for (DataSnapshot patient : snapshot.getChildren()
                ) {
                    long curr_place = (long) patient.child("Place in waiting list").getValue();
                    if (curr_place == 1)
                        First_Pat = patient.getKey();
                    else if (place < curr_place) {
                        Waiting_List_DB.child(Doc_id).child(patient.getKey()).child("Place in waiting list").setValue(curr_place - 1);
                    }
                }

                if(First_Pat.equals(" ")) { // no people at waiting list
                    Doc_DB.child(Doc_id).child("Current Appointment").child("Appointment start time").removeValue();
                    Doc_DB.child(Doc_id).child("Availability").setValue("True");
                }
                    Waiting_List_DB.child(Doc_id).child(First_Pat).removeValue();
                    Doc_DB.child(Doc_id).child("Current Appointment").child("Appointment start time").setValue(LocalTime.now());
                    Doc_DB.child(Doc_id).child("Current Appointment").child("Patient_Id").setValue(First_Pat);

                ShowCurrApp();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}