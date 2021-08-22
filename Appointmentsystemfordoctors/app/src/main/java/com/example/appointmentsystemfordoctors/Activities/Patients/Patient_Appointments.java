

package com.example.appointmentsystemfordoctors.Activities.Patients;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.appointmentsystemfordoctors.Activities.Comperators.PlaceComperator;
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
import java.util.Hashtable;

public class Patient_Appointments extends AppCompatActivity {

    Button goBack;
    ListView AppList;
    ListView Show_wl_list;
    TextView no_app;

    ArrayList<String> Apps;
    ArrayList<String> Show_WL = new ArrayList<>();
    ArrayList<ClientPatient> Pat_obj = new ArrayList<>();
    Hashtable<String,Long> id_n_place = new Hashtable<String, Long>();
    ArrayList<String> Docs_Ids=new ArrayList<>();
    Hashtable<String,Long> Pat_Ids = new Hashtable<String, Long>();

    DatabaseReference Waiting_List_DB = FirebaseDatabase.getInstance().getReference().child("Doc_Waiting_List");
    DatabaseReference Docs_DB = FirebaseDatabase.getInstance().getReference().child("Doctors");
    DatabaseReference Pats_DB = FirebaseDatabase.getInstance().getReference().child("Patients");

    Context context = this;

    String patient_id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointments);

        patient_id = getIntent().getStringExtra("patient_id");

        no_app = (TextView) findViewById(R.id.No_App_Pat);
        goBack = (Button) findViewById(R.id.Go_Back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, com.example.appointmentsystemfordoctors.Activities.Patients.Patient_Main.class);
                intent.putExtra("patient_id", patient_id);
                startActivity(intent);
            }
        });
        AppList = (ListView) findViewById(R.id.Appointment_list);

        Show_My_Apps();

    }

    /**
     * Shows The current appointment the patient is waiting to
     * param Apps stores the appointments as a string to show on the list , param id_n_place is a hush stores
     * the id of the doctors that the patient is waiting for and the place in the waiting list for that doctor. param Docs_ids stores only the ids of the doctors for furter
     * use to show the waiting list
     * Notice that we use Docs_Ids AND id_n_place because hush is not sorted by position and we need that for when the user clicks
     * on something on the list.
     */
    private void Show_My_Apps() {
        Apps = new ArrayList<>();
        id_n_place = new Hashtable<String, Long>();
        Docs_Ids=new ArrayList<>();

        Waiting_List_DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot doc : snapshot.getChildren()
                ) {
                    for (DataSnapshot patient : doc.getChildren()
                    ) {
                        if (patient.getKey().equals(patient_id)) {
                            id_n_place.put(doc.getKey(), (long) patient.child("Place in waiting list").getValue());
                            Docs_Ids.add(doc.getKey());
                        }
                    }
                }
                Docs_DB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot doc : snapshot.getChildren()
                        ) {
                            for (String id : id_n_place.keySet()
                            ) {
                                if (doc.getKey().equals(id)) {
                                    Apps.add("Dr.  " + (String) doc.child("doctor_name").getValue() + "         Place in waiting list: " + id_n_place.get(id));
                                }
                            }
                        }
                        if(Apps.size() == 0) // there is not appointment the patient is waiting for
                            no_app.setText("currently, you arent in any waiting list");
                        ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.simple_list_view, R.id.textView, Apps);
                        AppList.setAdapter(adapter);
                        AppList.setClickable(true);
                        AppList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("What it will be?")
                                        .setNegativeButton("cancel appointment", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Cancel_Appointment(position, patient_id);
                                            }
                                        })
                                        .setPositiveButton("Show the waiting list", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Show_Waiting_List(position, patient_id);
                                            }
                                        });
                                builder.create();
                                builder.show();
                                ;
                            }
                        });
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
     * Shows the waiting list to this appointment sorted by place in the waiting list.
     * @param position the position where the user clicked on the list
     * @param patient_id current patient_id
     * using layout inflater for the popup
     */
    private void Show_Waiting_List(int position, String patient_id) {
        Waiting_List_DB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot doc : snapshot.getChildren()
                ) {
                    if (doc.getKey().equals(Docs_Ids.get(position))) {
                        for (DataSnapshot patient : doc.getChildren()
                        )
                            Pat_Ids.put(patient.getKey(), (Long) patient.child("Place in waiting list").getValue());
                    }
                }
                Pats_DB.addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot patient : snapshot.getChildren()
                        ) {
                            for (String id : Pat_Ids.keySet()
                            ) {
                                if (patient.getKey().equals(id)) {
                                    String first_name = (String) patient.child("first_name").getValue();
                                    String second_name = (String) patient.child("second_name").getValue();
                                    Long place_in_wl = Pat_Ids.get(id);
                                    ClientPatient patient_obj = new ClientPatient(first_name, second_name, place_in_wl);
                                    Pat_obj.add(patient_obj);
                                }
                            }
                        }

                            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.activity_show_wl, null);
                            Show_wl_list = (ListView) view.findViewById(R.id.Show_wl_list);

                            Collections.sort(Pat_obj,  new PlaceComperator());//sorting using place comparator
                            for (ClientPatient patient2: Pat_obj
                                 ) {
                                Show_WL.add(patient2.TostringWithcurrPlaceInWl());
                            }
                            ArrayAdapter adapter = new ArrayAdapter<String>(context, R.layout.simple_list_view, R.id.textView, Show_WL);
                            AlertDialog.Builder builder_PopUp = new AlertDialog.Builder(context);
                            Show_wl_list.setAdapter(adapter);
                            builder_PopUp.setView(view);
                            builder_PopUp.setPositiveButton("Go back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(context, com.example.appointmentsystemfordoctors.Activities.Patients.Patient_Appointments.class);
                                    intent.putExtra("patient_id", patient_id);
                                    startActivity(intent);
                                }
                            });
                            builder_PopUp.create();
                            builder_PopUp.show();
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
     * remove patient from the waiting list
     * @param position the position where the user clicked on the list
     * @param patient_id current patient_id
     * calling function forwardWL if needed to update the waiting list
     */
    private void Cancel_Appointment(int position, String patient_id) {
        //the position is the number of the element in Doc_Ids arraylist
        Waiting_List_DB.addListenerForSingleValueEvent(new ValueEventListener() {
            long place;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Doc_id = Docs_Ids.get(position);
                for (DataSnapshot doc : snapshot.getChildren()
                ) {
                    if (doc.getKey().equals(Docs_Ids.get(position))) {
                        for (DataSnapshot patient : doc.getChildren()
                        ) {
                            if (patient.getKey().equals(patient_id))
                                place = (long) patient.child("Place in waiting list").getValue();
                        }
                        Waiting_List_DB.child(doc.getKey()).child(patient_id).removeValue();
                    }
                }
                Waiting_List_DB.child(Doc_id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int Waiting_ppl_num = 1;
                        for (DataSnapshot patient : snapshot.getChildren()
                        ) {
                            Waiting_ppl_num++;
                        }
                        if (Waiting_ppl_num == 1) //the patient is the only one on the waiting list
                            Docs_DB.child(Doc_id).child("Availability").setValue("True");
                         else // the waiting list needs to be updated
                            ForwardWL(Doc_id, place);
                        Toast toast = Toast.makeText(context, "Canceled successfully", Toast.LENGTH_LONG);
                        toast.show();
                        Show_My_Apps();
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
     * updating the waiting list when patient cancel his appointment
     * @param Doc_id the id of the doc we want to update the waiting list for his appointment
     * @param place the place of the patient that canceled his appointment in the waiting list
     */
    public void ForwardWL(String Doc_id, long place) {
        Waiting_List_DB.child(Doc_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot patient : snapshot.getChildren()
                ) {
                    long curr_place = (long) patient.child("Place in waiting list").getValue();
                    if (place < curr_place)
                        Waiting_List_DB.child(Doc_id).child(patient.getKey()).child("Place in waiting list").setValue(curr_place - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
