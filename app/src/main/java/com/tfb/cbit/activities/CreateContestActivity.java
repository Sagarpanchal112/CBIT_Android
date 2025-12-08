package com.tfb.cbit.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.tfb.cbit.R;
import com.tfb.cbit.databases.DatabaseHandler;
import com.tfb.cbit.databinding.ActivityAboutUsBinding;
import com.tfb.cbit.databinding.ActivityCreateContestBinding;
import com.tfb.cbit.event.UnAuthorizedEvent;
import com.tfb.cbit.utility.MyValidator;
import com.tfb.cbit.utility.SessionUtil;
import com.tfb.cbit.utility.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class CreateContestActivity extends BaseAppCompactActivity {
    private SimpleDateFormat dateFormatter;
    private SimpleDateFormat timeFormatter;
    private Context context;
    private List<String> answerRange = new ArrayList<>();
    private SessionUtil sessionUtil;
    private ActivityCreateContestBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateContestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        context = this;
        sessionUtil = new SessionUtil(context);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        timeFormatter = new SimpleDateFormat("hh:mm a", Locale.US);

        binding.edtContestName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    Utils.hideKeyboard(CreateContestActivity.this);
                    edtDateClick();
                    return true;
                }
                return false;
            }
        });

        binding.rgGameMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                ArrayAdapter<String> sa;
                if (checkedId == R.id.rbEasy) {
                    answerRange.clear();
                    answerRange.add("-100 to 100");
                    answerRange.add(" -10 to  10");
                    answerRange.add("   0 to   9");
                    sa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
                            answerRange);
                    sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerAnswer.setAdapter(sa);
                } else if (checkedId == R.id.rbMedium || checkedId == R.id.rbPro) {
                    answerRange.clear();
                    answerRange.add("-100 to 100");
                    answerRange.add(" -10 to  10");
                    answerRange.add("   0 to   9");
                    sa = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item,
                            answerRange);
                    sa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerAnswer.setAdapter(sa);
                }
            }
        });

        binding.rbEasy.setChecked(true);
        binding.rbFlexi.setChecked(true);
        binding.ivBack.setOnClickListener(view -> {
            onBackPressed();
        });
        binding.linearNoTickets.setOnClickListener(view -> {
            binding.spinnerNoTickets.performClick();
        });
        binding.linearMaxWinners.setOnClickListener(view -> {
            binding.spinnerMaxWinners.performClick();
        });
        binding.linearAnswer.setOnClickListener(view -> {
            binding.spinnerAnswer.performClick();
        });
        binding.edtDate.setOnClickListener(view -> {
            edtDateClick();
        });
        binding.edtTime.setOnClickListener(view -> {
            Calendar newCalendar = Calendar.getInstance();
            newCalendar.add(Calendar.MINUTE, 11);
            TimePickerDialog startTimePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {

                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            Calendar newTime = Calendar.getInstance();
                            newTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            newTime.set(Calendar.MINUTE, minute);
                            binding.edtTime.setText(timeFormatter.format(newTime.getTime()));
                        }
                    }, newCalendar.get(Calendar.HOUR_OF_DAY), newCalendar.get(Calendar.MINUTE), false);

            startTimePickerDialog.show();
        });
        binding.btnNext.setOnClickListener(view -> {
            if (isValidForm()) {
                Bundle bundle = new Bundle();
                bundle.putString("name", binding.edtContestName.getText().toString());
                bundle.putString("startDate", binding.edtDate.getText().toString());
                bundle.putString("startTime", binding.edtTime.getText().toString());
                if (binding.rgGameMode.getCheckedRadioButtonId() == R.id.rbEasy) {
                    bundle.putInt("level", Utils.EASY);
                } else if (binding.rgGameMode.getCheckedRadioButtonId() == R.id.rbMedium) {
                    bundle.putInt("level", Utils.MODERATE);

                } else if (binding.rgGameMode.getCheckedRadioButtonId() == R.id.rbPro) {
                    bundle.putInt("level", Utils.PRO);

                }

                if (binding.rgType.getCheckedRadioButtonId() == R.id.rbFlexi) {
                    bundle.putInt("type", Utils.FLEXIBAR);
                } else if (binding.rgType.getCheckedRadioButtonId() == R.id.rbFixbar) {
                    bundle.putInt("type", Utils.FIXEDSLOT);

                }


                int noofTickets = Integer.parseInt(String.valueOf(binding.spinnerNoTickets.getSelectedItem()));
                bundle.putInt("nooftickets", noofTickets);

                String maxWinner = String.valueOf(binding.spinnerMaxWinners.getSelectedItem());
                maxWinner = maxWinner.substring(0, maxWinner.length() - 1);
                bundle.putString("maxWinner", maxWinner);

                bundle.putBoolean("isNotify", binding.chkNotify.isChecked());

                bundle.putString("rangeMin", answerRange.get(binding.spinnerAnswer.getSelectedItemPosition()).split("to")[0].trim());
                bundle.putString("rangeMax", answerRange.get(binding.spinnerAnswer.getSelectedItemPosition()).split("to")[1].trim());

                Intent intent = new Intent(context, TicketDetailsActivity.class);
                intent.putExtras(bundle);
                startActivityForResult(intent, 123);
            }
        });
    }

    private void edtDateClick() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog startDatePickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                if (Utils.isInThePast(year, monthOfYear, dayOfMonth)) {
                    binding.edtDate.setText("");
                    Utils.showToast(context, "can not set past date");
                } else {
                    binding.edtDate.setText(dateFormatter.format(newDate.getTime()));
                }

            }

        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startDatePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        }
        startDatePickerDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    private boolean isValidForm() {
        if (!MyValidator.isBlankETError(context, binding.edtContestName, "Enter Contest Name", 1, 100)) {
            return false;
        } else if (!MyValidator.isBlankETError(context, binding.edtDate, "Enter Date", 1, 100)) {
            return false;
        } else if (!MyValidator.isBlankETError(context, binding.edtTime, "Enter Time", 1, 100)) {
            return false;
        } else if (!isValidTime(binding.edtDate.getText().toString(), binding.edtTime.getText().toString())) {
            Utils.showToast(context, "Contest add grater than 10 minutes from current time");
            return false;
        }
        return true;
    }

    private boolean isValidTime(String date, String time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
            Date date1 = simpleDateFormat.parse(date + " " + time);
            Calendar calendar = Calendar.getInstance();
            long currentMill = calendar.getTimeInMillis();
            calendar.setTime(date1);
            if (Utils.isToday(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))) {
                if (calendar.getTimeInMillis() > (currentMill + (1000 * 60 * 10))) {
                    return true;
                } else {
                    return false;
                }

            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                Utils.hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onUnAuthorizedEvent(UnAuthorizedEvent unAuthorizedEvent) {
        Utils.showToast(this, unAuthorizedEvent.getMessage());
        DatabaseHandler databaseHandler = new DatabaseHandler(this);
        databaseHandler.deleteTable();
        String fcmToken = sessionUtil.getFcmtoken();
        sessionUtil.logOut();
        sessionUtil.setFCMToken(fcmToken);
        Intent intent = new Intent(this, LoginSignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
