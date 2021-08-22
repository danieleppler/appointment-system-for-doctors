package com.example.appointmentsystemfordoctors.Activities.Patients;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.appointmentsystemfordoctors.Activities.dataObjects.Address;
import com.example.appointmentsystemfordoctors.Activities.dataObjects.ClientDoctors;
import com.example.appointmentsystemfordoctors.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.time.LocalTime;
import java.util.ArrayList;


public class Patient_Main extends AppCompatActivity {

    Context context = this;

    ArrayList<String> Docs = new ArrayList<>();
    ArrayList<ClientDoctors> DocObj = new ArrayList<>();

    DatabaseReference Doc_DB = FirebaseDatabase.getInstance().getReference().child("Doctors");
    DatabaseReference Waiting_List_DB = FirebaseDatabase.getInstance().getReference().child("Doc_Waiting_List");

    ListView DocList;
    Button my_app;
    CheckBox filter;

    String patient_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        patient_id = "p:111111111";  //DEBUG
        //patient_id = getIntent().getStringExtra("patient_id");  //REAL Time
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_main);


        my_app =(Button)findViewById(R.id.My_appointments);

        my_app.setOnClickListener(new View.OnClickListener() //send to Patient_Appointments activity
        {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, com.example.appointmentsystemfordoctors.Activities.Patients.Patient_Appointments.class);
                intent.putExtra("patient_id", patient_id);
                startActivity(intent);
            }
        });

        filter = (CheckBox) findViewById(R.id.filter_by_av);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Docs = new ArrayList<>();
                DocObj = new ArrayList<>();
                if(filter.isChecked())
                    ShowList(true);
                else ShowList(false);
            }
        });

        DocList = (ListView) findViewById(R.id.DocList);
        ShowList(false);
    }

    /**
     * Show the Doctors
     * @param isFiltered if true,  show filtered doctor list by availability
     * param DcOBJ hold doctors as objects , param  Docs hold strings such that each string represent doctor.
     */
    private void ShowList(boolean isFiltered) {
        Docs = new ArrayList<>(); // reset the list
        DocObj = new ArrayList<>();

        Doc_DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()
                ) {
                    String availability = (String) data.child("Availability").getValue();
                    String name = (String) data.child("doctor_name").getValue();
                    String phone_num = (String) data.child("phone_number").getValue();
                    DataSnapshot addr = data.child("address");
                    Address address = new Address((String) addr.child("city_Name").getValue(), (String) addr.child("street_Name").getValue()
                            , (String) addr.child("house_Number").getValue());
                    String Id = (String) data.child("doctorID").getValue();
                    String curr_pat = " ";
                    if(data.child("Current Appointment").exists())
                        curr_pat = (String) data.child("Current Appointment").child("Patient_Id").getValue();
                    ClientDoctors doc_obj = new ClientDoctors(phone_num, address, name, Id, availability,curr_pat);
                    if(isFiltered) {
                        if (availability.equals("True")) {
                            DocObj.add(doc_obj);
                            Docs.add(doc_obj.ToString());
                        }
                    }
                    else
                        {
                            DocObj.add(doc_obj);
                            Docs.add("Dr." + " " + doc_obj.ToString());
                        }
                }
                ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.simple_list_view, R.id.textView, Docs);
                DocList.setAdapter(adapter);
                DocList.setClickable(true);
                DocList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        showPopup(position);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    /**
     * Show Popup screen where we will need to confirm appointment booking
     * @param position the position in the doctors list we clicked
     */
    private void showPopup(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String doc_name = DocObj.get(position).getDoctor_name();
        String doc_id = DocObj.get(position).getDoctorID();
        builder.setTitle("Are you sure you want to make appointment with doctor " + doc_name)
                .setNegativeButton("On second thought", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(context, com.example.appointmentsystemfordoctors.Activities.Patients.Patient_Main.class);
                        intent.putExtra("patient_id", patient_id);
                        startActivity(intent);
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (DocObj.get(position).getAvailability().equals("True")) { // if available
                            Doc_DB.child(doc_id).child("Availability").setValue("False");
                            Doc_DB.child(doc_id).child("Current Appointment").child("Patient_Id").setValue(patient_id);
                            Doc_DB.child(doc_id).child("Current Appointment").child("Appointment start time").setValue(java.time.LocalTime.now());
                            Toast toast = Toast.makeText(context, "your appointment is booked! you will get a notification to proceed soon", Toast.LENGTH_LONG);
                            toast.show();
                            sendNotificationToUser(doc_name, context);
                        } else {
                           //Check if the patient is the treated patient ,if not - Add to Waiting list
                            if(DocObj.get(position).getCurrPat().equals(patient_id)) {
                                Toast toast = Toast.makeText(context, "Well... you cant make an appointment when you are already in the appointment", Toast.LENGTH_LONG);
                                toast.show();
                            }
                            else {
                                Waiting_List_DB.child(doc_id).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        int Waiting_ppl_num = 1; // to check which place the patient will be
                                        boolean isOnList = false;
                                        for (DataSnapshot patient : snapshot.getChildren()
                                        ) {
                                            if (patient.getKey().equals(patient_id))
                                                isOnList = true; // the patient is already in the list
                                            Waiting_ppl_num++;
                                        }
                                        if (!isOnList) {
                                            Waiting_List_DB.child(doc_id).child(patient_id).child("Place in waiting list").setValue(Waiting_ppl_num);
                                            Waiting_List_DB.child(doc_id).child(patient_id).child("Arrival Time").setValue(LocalTime.now());
                                            Toast toast = Toast.makeText(context, "The doctor is occupied in this moment, you had been added to the waiting list", Toast.LENGTH_LONG);
                                            toast.show();
                                        } else {
                                            Toast toast = Toast.makeText(context, "Oops, seems you are already on the waiting list", Toast.LENGTH_LONG);
                                            toast.show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }
                    }
                });
        builder.create();
        builder.show();
    }

    /**
     * send notification to the user that the appointment is booked. Using notificationCompat  builder
     * @param doc_name the name of the doc which the patient booked appointment for
     * @param context activity context
     */
    public void sendNotificationToUser(String doc_name, Context context) {
        FirebaseDatabase.getInstance().getReference().child("Patients").child(patient_id).child("token").
                addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                        {
                            NotificationChannel ntc = new NotificationChannel("My Notifcation","My Notifcation", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager notificationManager = getSystemService(NotificationManager.class);
                            notificationManager.createNotificationChannel(ntc);
                        }
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "My Notifcation")
                                .setSmallIcon(R.drawable.ic_launcher_background)
                                .setContentTitle("Its time for your appointment !")
                                .setContentText("Please proceed to see doctor : " + doc_name)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                        notificationManager.notify(1, builder.build());
                        ShowList(false);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}