package com.leaf.godproject.alarm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.leaf.godproject.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;


public class ReminderAddActivity extends AppCompatActivity implements
        TimePickerDialog.OnTimeSetListener,
        DatePickerDialog.OnDateSetListener{

    private Toolbar mToolbar;
    private EditText mTitleText;
    private TextView mDateText, mTimeText, mRepeatText, mRepeatNoText, mRepeatTypeText;
    private TextView mAlarmtypeText,mAlarmstatText;
    private Calendar mCalendar;
    private int mYear, mMonth, mHour, mMinute, mDay;
    private long mRepeatTime;
    private String mTitle;
    private String mTime;
    private String mAlarmtype;
    private String mAlarmstat;
    private String mDate;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;

    // Values for orientation change
    private static final String KEY_TITLE = "title_key";
    private static final String KEY_ALARMTYPE = "alarmtype_key";
    private static final String KEY_ALARMSTAT = "alarmstat_key";
    private static final String KEY_TIME = "time_key";
    private static final String KEY_DATE = "date_key";
    private static final String KEY_REPEAT = "repeat_key";
    private static final String KEY_REPEAT_NO = "repeat_no_key";
    private static final String KEY_REPEAT_TYPE = "repeat_type_key";
    private static final String KEY_ACTIVE = "active_key";

    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        // Initialize Views
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mTitleText = (EditText) findViewById(R.id.reminder_title);
        mAlarmtypeText = (TextView) findViewById(R.id.set_alarmtype);
        mAlarmstatText = (TextView) findViewById(R.id.set_alarmstat);
        mDateText = (TextView) findViewById(R.id.set_date);
        mTimeText = (TextView) findViewById(R.id.set_time);
        mRepeatText = (TextView) findViewById(R.id.set_repeat);
        mRepeatNoText = (TextView) findViewById(R.id.set_repeat_no);
        mRepeatTypeText = (TextView) findViewById(R.id.set_repeat_type);

        // Setup Toolbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("新增提醒");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // Initialize default values
        mActive = "true";
        mAlarmtype ="洗澡";
        mAlarmstat  = "true";
        mRepeat = "true";
        mRepeatNo = Integer.toString(1);
        mRepeatType = "Hour";

        mCalendar = Calendar.getInstance();
        mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = mCalendar.get(Calendar.MINUTE);
        mYear = mCalendar.get(Calendar.YEAR);
        mMonth = mCalendar.get(Calendar.MONTH) + 1;
        mDay = mCalendar.get(Calendar.DATE);

//        mDate = mDay + "/" + mMonth + "/" + mYear;
        mDate =  mYear + "/" + mMonth + "/" +mDay;

        mTime = mHour + ":" + mMinute;

        // Setup Reminder Title EditText
        mTitleText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTitle = s.toString().trim();
                mTitleText.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Setup TextViews using reminder values
        mAlarmtypeText.setText(mAlarmtype);
        mAlarmstatText.setText("On");
        mDateText.setText(mDate);
        mTimeText.setText(mTime);
        mRepeatNoText.setText(mRepeatNo);
        mRepeatTypeText.setText(mRepeatType);
        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");

        // To save state on device rotation
        if (savedInstanceState != null) {
            String savedTitle = savedInstanceState.getString(KEY_TITLE);
            mTitleText.setText(savedTitle);
            mTitle = savedTitle;

            String savedAlarmtype = savedInstanceState.getString(KEY_ALARMTYPE);
            mAlarmtypeText.setText(savedAlarmtype);
            mAlarmtype = savedAlarmtype;

            String savedAlarmstat = savedInstanceState.getString(KEY_ALARMSTAT);
            mAlarmstatText.setText(savedAlarmstat);
            mAlarmstat = savedAlarmstat;

            String savedTime = savedInstanceState.getString(KEY_TIME);
            mTimeText.setText(savedTime);
            mTime = savedTime;

            String savedDate = savedInstanceState.getString(KEY_DATE);
            mDateText.setText(savedDate);
            mDate = savedDate;

            String saveRepeat = savedInstanceState.getString(KEY_REPEAT);
            mRepeatText.setText(saveRepeat);
            mRepeat = saveRepeat;

            String savedRepeatNo = savedInstanceState.getString(KEY_REPEAT_NO);
            mRepeatNoText.setText(savedRepeatNo);
            mRepeatNo = savedRepeatNo;

            String savedRepeatType = savedInstanceState.getString(KEY_REPEAT_TYPE);
            mRepeatTypeText.setText(savedRepeatType);
            mRepeatType = savedRepeatType;

            mActive = savedInstanceState.getString(KEY_ACTIVE);
        }

        // Setup up active buttons 到時候刪掉
//        if (mActive.equals("false")) {
//            mFAB1.setVisibility(View.VISIBLE);
//            mFAB2.setVisibility(View.GONE);
//
//        } else if (mActive.equals("true")) {
//            mFAB1.setVisibility(View.GONE);
//            mFAB2.setVisibility(View.VISIBLE);
//        }
    }

    // To save state on device rotation
    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putCharSequence(KEY_TITLE, mTitleText.getText());
        outState.putCharSequence(KEY_ALARMTYPE, mAlarmtypeText.getText());
        outState.putCharSequence(KEY_ALARMSTAT, mAlarmstatText.getText());
        outState.putCharSequence(KEY_TIME, mTimeText.getText());
        outState.putCharSequence(KEY_DATE, mDateText.getText());
        outState.putCharSequence(KEY_REPEAT, mRepeatText.getText());
        outState.putCharSequence(KEY_REPEAT_NO, mRepeatNoText.getText());
        outState.putCharSequence(KEY_REPEAT_TYPE, mRepeatTypeText.getText());
        outState.putCharSequence(KEY_ACTIVE, mActive);
    }

    // On clicking Time picker
    public void setTime(View v){
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        tpd.setThemeDark(false);
        tpd.show(getFragmentManager(), "Timepickerdialog");
    }

    // On clicking Date picker
    public void setDate(View v){
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }

    // Obtain time from time picker
    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {
        mHour = hourOfDay;
        mMinute = minute;
        if (minute < 10) {
            mTime = hourOfDay + ":" + "0" + minute;
        } else {
            mTime = hourOfDay + ":" + minute;
        }
        mTimeText.setText(mTime);
    }

    // Obtain date from date picker
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear ++;
        mDay = dayOfMonth;
        mMonth = monthOfYear;
        mYear = year;
//        mDate = dayOfMonth + "/" + monthOfYear + "/" + year;
        mDate = year + "/" + monthOfYear + "/" + dayOfMonth;
        mDateText.setText(mDate);
    }

    // On clicking the active button 註解調確定不用就刪掉
//    public void selectFab1(View v) {
//        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
//        mFAB1.setVisibility(View.GONE);
//        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
//        mFAB2.setVisibility(View.VISIBLE);
//        mActive = "true";
//    }

    // On clicking the inactive button  註解調確定不用就刪掉
//    public void selectFab2(View v) {
//        mFAB2 = (FloatingActionButton) findViewById(R.id.starred2);
//        mFAB2.setVisibility(View.GONE);
//        mFAB1 = (FloatingActionButton) findViewById(R.id.starred1);
//        mFAB1.setVisibility(View.VISIBLE);
//        mActive = "false";
//    }

    // On clicking the repeat switch
    public void onSwitchRepeat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mRepeat = "true";
            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
        } else {
            mRepeat = "false";
            mRepeatText.setText(R.string.repeat_off);
        }
    }

    // On clicking the Alarmstat switch
    public void onSwitchAlarmstat(View view) {
        boolean on = ((Switch) view).isChecked();
        if (on) {
            mAlarmstat = "true";
            mAlarmstatText.setText("On" );
        } else {
            mAlarmstat = "false";
            mAlarmstatText.setText(R.string.alarmstat_off);
        }
    }



    // On clicking repeat type button
    public void selectAlarmtype(View v){
        final String[] items = new String[11];
        items[0] = "餵食";
        items[1] = "刷牙";
        items[2] = "剪指甲";
        items[3] = "洗澡";
        items[4] = "美容";
        items[5] = "驅蟲";
        items[6] = "接種疫苗";
        items[7] = "健康檢查";
        items[8] = "聚會";
        items[9] = "溜狗";
        items[10] = "其他";
        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("選擇事件");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        mRepeatNo = Integer.toString(1);
                        mRepeatType = "Day";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                    case 1:
                        mRepeatNo = Integer.toString(1);
                        mRepeatType = "Day";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                    case 2:
                        mRepeatNo = Integer.toString(10);
                        mRepeatType = "Day";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                    case 3:
                        mRepeatNo = Integer.toString(7);
                        mRepeatType = "Day";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                    case 4:
                        mRepeatNo = Integer.toString(1);
                        mRepeatType = "Month";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                    case 5:
                        mRepeatNo = Integer.toString(3);
                        mRepeatType = "Month";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                    case 6:
                        mRepeatNo = Integer.toString(12);
                        mRepeatType = "Month";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                    case 7:
                        mRepeatNo = Integer.toString(12);
                        mRepeatType ="Month";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                    case 8:
                        mRepeatNo = Integer.toString(1);
                        mRepeatType = "Month";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                    case 9:
                        mRepeatNo = Integer.toString(1);
                        mRepeatType = "Month";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                    case 10:
                        mRepeatNo = Integer.toString(1);
                        mRepeatType = "Day";
                        mRepeatNoText.setText(mRepeatNo);
                        mRepeatTypeText.setText(mRepeatType);
                        mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        break;
                }
                mAlarmtype = items[item];
                mAlarmtypeText.setText(mAlarmtype);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    // On clicking repeat type button
    public void selectRepeatType(View v){
        final String[] items = new String[5];

        items[0] = "Minute";
        items[1] = "Hour";
        items[2] = "Day";
        items[3] = "Week";
        items[4] = "Month";

        // Create List Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("選擇重複週期");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                mRepeatType = items[item];
                mRepeatTypeText.setText(mRepeatType);
                mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // On clicking repeat interval button
    public void setRepeatNo(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("輸入數字");

        // Create EditText box to input repeat number
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(input);
        alert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                        if (input.getText().toString().length() == 0) {
                            mRepeatNo = Integer.toString(1);
                            mRepeatNoText.setText(mRepeatNo);
                            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                        else {
                            mRepeatNo = input.getText().toString().trim();
                            mRepeatNoText.setText(mRepeatNo);
                            mRepeatText.setText("Every " + mRepeatNo + " " + mRepeatType + "(s)");
                        }
                    }
                });
        alert.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // do nothing
            }
        });
        alert.show();
    }

    // On clicking the save button
    public void saveReminder(){
        ReminderDatabase rb = new ReminderDatabase(this);

        // Creating Reminder
        int ID = rb.addReminder(new Reminder(mTitle,mAlarmtype,mAlarmstat,mDate, mTime, mRepeat, mRepeatNo, mRepeatType, mActive));

        // Set up calender for creating the notification
        mCalendar.set(Calendar.MONTH, --mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
        mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
        mCalendar.set(Calendar.MINUTE, mMinute);
        mCalendar.set(Calendar.SECOND, 0);

        // Check repeat type
        if (mRepeatType.equals("Minute")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
        } else if (mRepeatType.equals("Hour")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
        } else if (mRepeatType.equals("Day")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
        } else if (mRepeatType.equals("Week")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
        } else if (mRepeatType.equals("Month")) {
            mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
        }

        // Create a new notification 記得改成alarmstat
//        if (mActive.equals("true")) {
        if (mAlarmstat.equals("true")) {
            if (mRepeat.equals("true")) {
                new AlarmReceiver().setRepeatAlarm(getApplicationContext(), mCalendar, ID, mRepeatTime);
            } else if (mRepeat.equals("false")) {
                new AlarmReceiver().setAlarm(getApplicationContext(), mCalendar, ID);
            }
        }

        // Create toast to confirm new reminder
        Toast.makeText(getApplicationContext(), "儲存成功",
                Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    // On pressing the back button
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    // Creating the menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_reminder, menu);
        return true;
    }

    // On clicking menu buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // On clicking the back arrow
            // Discard any changes
            case android.R.id.home:
                onBackPressed();
                return true;

            // On clicking save reminder button
            // Update reminder
            case R.id.save_reminder:
                mTitleText.setText(mTitle);

                if (mTitleText.getText().toString().length() == 0)
                    mTitleText.setError("提醒內容不能為空");

                else {
                    saveReminder();
                }
                return true;

            // On clicking discard reminder button
            // Discard any changes
            case R.id.discard_reminder:
                Toast.makeText(getApplicationContext(), "已放棄提醒",
                        Toast.LENGTH_SHORT).show();

                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}