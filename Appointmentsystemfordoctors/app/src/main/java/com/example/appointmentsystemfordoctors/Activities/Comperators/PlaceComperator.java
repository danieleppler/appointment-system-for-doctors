package com.example.appointmentsystemfordoctors.Activities.Comperators;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.example.appointmentsystemfordoctors.Activities.dataObjects.ClientPatient;

import java.util.Comparator;

public class PlaceComperator implements Comparator<ClientPatient> {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int compare(ClientPatient o1, ClientPatient o2) {
        if (o1.getCurrPlaecInWl() < o2.getCurrPlaecInWl()) {
            return -1;
        }
        else if (o1.getCurrPlaecInWl() > o2.getCurrPlaecInWl()) {
            return 1;
        }
        else
        {
            return 0;
        }
    }
}
