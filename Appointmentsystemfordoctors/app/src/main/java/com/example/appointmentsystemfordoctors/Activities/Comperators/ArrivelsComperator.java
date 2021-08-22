package com.example.appointmentsystemfordoctors.Activities.Comperators;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.appointmentsystemfordoctors.Activities.dataObjects.ClientPatient;

import java.util.Comparator;

public class ArrivelsComperator implements Comparator<ClientPatient> {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int compare(ClientPatient o1, ClientPatient o2) {
        if (o1.getArrivalHour() < o2.getArrivalHour()) {
            return -1;
        }
        else if (o1.getArrivalHour() > o2.getArrivalHour()) {
            return 1;
        }
        else
            {
                if (o1.getArrivalMinute() == o2.getArrivalMinute()) {
                    return 0;
                }
                else if (o1.getArrivalMinute() < o2.getArrivalMinute()) {
                    return -1;
                }
                else return 1;
            }
    }
}

