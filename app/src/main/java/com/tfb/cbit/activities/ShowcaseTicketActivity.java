package com.tfb.cbit.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tfb.cbit.R;
import com.tfb.cbit.adapter.TicketSelectionAdapter;
import com.tfb.cbit.api.NewApiCall;
import com.tfb.cbit.models.contestdetails.ContestDetailsModel;
import com.tfb.cbit.models.contestdetails.Ticket;
import com.tfb.cbit.utility.SessionUtil;

import java.util.ArrayList;
import java.util.List;


public class ShowcaseTicketActivity extends AppCompatActivity {
    private static final String TAG = "TicketSelectionActivity";
    private Context context;
   private TicketSelectionAdapter ticketSelectionAdapter;
    public static final String CONTEST_ID = "contestid";
    public static final String CONTEST_NAME = "contestname";
    public static final String CONTEST_RTIME = "RTime";
    public static final String CONTEST_MinRange = "MinRange";
    public static final String CONTEST_MaxRange = "MaxRange";
    private String contestId = "";
    private SessionUtil sessionUtil;
    private List<Ticket> ticketList = new ArrayList<>();
    private NewApiCall newApiCall;
    ContestDetailsModel gtm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showcase_ticket);
    }
}