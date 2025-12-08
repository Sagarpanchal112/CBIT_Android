/*
package com.tfb.cbit;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.models.dbmodel.UpcomingContestModel;
import com.tfb.cbit.receiver.AlarmReceiver;
import com.tfb.cbit.utility.CountDown;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DemoAlarmActivity extends AppCompatActivity {

    @BindView(R.id.edtDate)
    EditText edtDate;
    @BindView(R.id.edtTime)
    EditText edtTime;
    @BindView(R.id.tvTimer)
    TextView tvTimer;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;
    private Context context;
    private DatabaseHandler databaseHandler;
    private CountDownTimer countDownTimer;
    private CountDown cDown;
    private boolean isTimerRunning = false;

    private static final String BUNDLE_EXTRA = "bundle_extra";
    private static final String ALARM_KEY = "alarm_key";

    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_alarm);
        ButterKnife.bind(this);
        context = this;
        dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        timeFormat = new SimpleDateFormat("hh:mm a",Locale.getDefault());
        databaseHandler = new DatabaseHandler(context);

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);



        countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                isTimerRunning = true;
                Log.d("TIME", String.valueOf(millisUntilFinished/1000));
                tvTimer.setText(String.format("%02d",millisUntilFinished/1000));
                        //String.format("%02d",(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))));

            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                Log.d("TIME", "00");
                tvTimer.setText("00");
            }
        };

        cDown = new CountDown(30000,1000) {

            @Override
            public void onTick(final long remainingMillSec) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TIME", String.valueOf(remainingMillSec/1000));
                        tvTimer.setText(
                                String.format("%02d",remainingMillSec/1000));
                    }
                });

            }

            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("TIME", "00");
                        tvTimer.setText("00");
                    }
                });

            }
        };
    }

    @OnClick(R.id.btnTimer)
    protected void btnTimerClick(){
        if(!isTimerRunning){
            cDown.start();
        }
    }

    @OnClick(R.id.edtDate)
    protected void edtDateClick(){
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog startDatePickerDialog = new DatePickerDialog(this,R.style.DialogTheme,new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if(Utils.isInThePast(year,monthOfYear,dayOfMonth)){
                    edtDate.setText("");
                    Utils.showToast(context,"can not set past date");
                }else{
                    edtDate.setText(dateFormat.format(newDate.getTime()));
                }

            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }
        startDatePickerDialog.show();
    }

    @OnClick(R.id.edtTime)
    protected void edtTimeClick(){
        Calendar newCalendar = Calendar.getInstance();
        TimePickerDialog startTimePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        Calendar newTime = Calendar.getInstance();
                        newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        newTime.set(Calendar.MINUTE, minute);
                        edtTime.setText(timeFormat.format(newTime.getTime()));
                    }
                }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false);

        startTimePickerDialog.show();
    }

    @OnClick(R.id.btnStartAlarm)
    protected void btnStartAlarmClick(){
        if(MyValidator.isBlankETError(context,edtDate,"Enter Date",1,100)
        && MyValidator.isBlankETError(context,edtTime,"Enter Time",1,100)) {

            String contestDate = Utils.getyyyyMMddformat(edtDate.getText().toString().trim()) +
                    " " + get24HoursWithSecondTime(edtTime.getText().toString());

            Calendar calendar = Calendar.getInstance();

            String currentDate = "";

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());



            currentDate = sdf.format(calendar.getTime());

            UpcomingContestModel upcomingContestModel = new UpcomingContestModel(contestDate,currentDate,1,"","","");
            databaseHandler.addContest(upcomingContestModel);
            AlarmReceiver.setReminderAlarm(context, upcomingContestModel);
            Utils.showToast(context,"Alarm Set");

        }
    }


    public static String get24HoursWithSecondTime(String timeStr){
        String dateFormate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
            Date date = sdf.parse(timeStr);
            sdf.applyPattern("HH:mm:ss");
            dateFormate = sdf.format(date);
        }catch (Exception e){
            e.printStackTrace();
        }

        return dateFormate;
    }
}
*/
