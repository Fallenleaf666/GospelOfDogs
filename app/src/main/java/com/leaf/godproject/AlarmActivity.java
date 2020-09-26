package com.leaf.godproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.leaf.godproject.alarm.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;


public class AlarmActivity extends AppCompatActivity {
    private RecyclerView mList;
    private SimpleAdapter mAdapter;
    private Toolbar mToolbar;
    private TextView mNoReminderView;
    private FloatingActionButton mAddReminderButton;
    private int mTempPost;
    private LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();
    private ReminderDatabase rb;
    private MultiSelector mMultiSelector = new MultiSelector();
    private AlarmReceiver mAlarmReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        // Initialize reminder database
        rb = new ReminderDatabase(getApplicationContext());

        // Initialize views
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
//        getSupportActionBar().setTitle(R.string.title_activity_alarm);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);
        mAddReminderButton = (FloatingActionButton) findViewById(R.id.add_reminder);
        mList = (RecyclerView) findViewById(R.id.reminder_list);
        mNoReminderView = (TextView) findViewById(R.id.no_reminder_text);

        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        }

        // Create recycler view
        mList.setLayoutManager(getLayoutManager());
        registerForContextMenu(mList);
        mAdapter = new SimpleAdapter();
        mAdapter.setItemCount(getDefaultItemCount());
        mList.setAdapter(mAdapter);

        // Setup toolbar
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(R.string.title_activity_alarm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        // On clicking the floating action button
        mAddReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
                startActivity(intent);
            }
        });

        // Initialize alarm
        mAlarmReceiver = new AlarmReceiver();
    }

    // Create context menu for long press actions
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.menu_add_reminder2, menu);
    }

    // Multi select items in recycler view
    private androidx.appcompat.view.ActionMode.Callback mDeleteMode = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(androidx.appcompat.view.ActionMode actionMode, Menu menu) {
            getMenuInflater().inflate(R.menu.menu_add_reminder2, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(androidx.appcompat.view.ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {

                // On clicking discard reminders
                case R.id.discard_reminder:
                    // Close the context menu
                    actionMode.finish();

                    // Get the reminder id associated with the recycler view item
                    for (int i = IDmap.size(); i >= 0; i--) {
                        if (mMultiSelector.isSelected(i, 0)) {
                            int id = IDmap.get(i);

                            // Get reminder from reminder database using id
                            Reminder temp = rb.getReminder(id);
                            // Delete reminder
                            rb.deleteReminder(temp);
                            // Remove reminder from recycler view
                            mAdapter.removeItemSelected(i);
                            // Delete reminder alarm
                            mAlarmReceiver.cancelAlarm(getApplicationContext(), id);
                        }
                    }

                    // Clear selected items in recycler view
                    mMultiSelector.clearSelections();
                    // Recreate the recycler items
                    // This is done to remap the item and reminder ids
                    mAdapter.onDeleteItem(getDefaultItemCount());

                    // Display toast to confirm delete
                    Toast.makeText(getApplicationContext(),
                            "刪除提醒",
                            Toast.LENGTH_SHORT).show();

                    // To check is there are saved reminders
                    // If there are no reminders display a message asking the user to create reminders
                    List<Reminder> mTest = rb.getAllReminders();

                    if (mTest.isEmpty()) {
                        mNoReminderView.setVisibility(View.VISIBLE);
                    } else {
                        mNoReminderView.setVisibility(View.GONE);
                    }

                    return true;

                // On clicking save reminders
                case R.id.save_reminder:
                    // Close the context menu
                    actionMode.finish();
                    // Clear selected items in recycler view
                    mMultiSelector.clearSelections();
                    return true;

                default:
                    break;
            }
            return false;
        }
    };

    // On clicking a reminder item
    private void selectReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);

        // Create intent to edit the reminder
        // Put reminder id as extra
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        後來補的
        super.onActivityResult(requestCode, resultCode, data);
        mAdapter.setItemCount(getDefaultItemCount());
    }

    // Recreate recycler view
    // This is done so that newly created reminders are displayed
    @Override
    public void onResume(){
        super.onResume();

        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        } else {
            mNoReminderView.setVisibility(View.GONE);
        }

        mAdapter.setItemCount(getDefaultItemCount());
    }

    // Layout manager for recycler view
    protected RecyclerView.LayoutManager getLayoutManager() {
//        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        return new LinearLayoutManager(this, mList.VERTICAL, false);

    }

    protected int getDefaultItemCount() {
        return 100;
    }

    // Create menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.alarm_menu, menu);
        return true;
    }

    // Setup menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // start licenses activity
            case R.id.action_licenses:
//                Intent intent = new Intent(this, LicencesActivity.class);
//                startActivity(intent);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Adapter class for recycler view
    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.VerticalItemHolder> {
        private ArrayList<ReminderItem> mItems;

        public SimpleAdapter() {
            mItems = new ArrayList<>();
        }

        public void setItemCount(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
            notifyDataSetChanged();
        }

        public void onDeleteItem(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
        }

        public void removeItemSelected(int selected) {
            if (mItems.isEmpty()) return;
            mItems.remove(selected);
            notifyItemRemoved(selected);
        }

        // View holder for recycler view items
        @Override
        public VerticalItemHolder onCreateViewHolder(ViewGroup container, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View root = inflater.inflate(R.layout.reminder_items, container, false);

            return new VerticalItemHolder(root, this);
        }

        @Override
        public void onBindViewHolder(VerticalItemHolder itemHolder, int position) {
            ReminderItem item = mItems.get(position);
            itemHolder.setReminderTitle(item.mTitle);
            itemHolder.setAlarmimg(item.mAlarmtype);
            itemHolder.setReminderDateTime(item.mDateTime);
            itemHolder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);
//            itemHolder.setActiveImage(item.mActive);
            itemHolder.setActiveImage(item.mAlarmstat);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        // Class for recycler view items
        public  class ReminderItem {
            public String mTitle;
            //            不確定要不要加 加左邊圖片
            public String mAlarmtype;
            public String mAlarmstat;
            public String mDateTime;
            public String mRepeat;
            public String mRepeatNo;
            public String mRepeatType;
            public String mActive;

            public ReminderItem(String Title,String Alarmtype,String Alarmstat, String DateTime, String Repeat, String RepeatNo, String RepeatType, String Active) {
                this.mTitle = Title;
                this.mAlarmtype = Alarmtype;
                this.mAlarmstat = Alarmstat;
                this.mDateTime = DateTime;
                this.mRepeat = Repeat;
                this.mRepeatNo = RepeatNo;
                this.mRepeatType = RepeatType;
                this.mActive = Active;
            }
        }

        // Class to compare date and time so that items are sorted in ascending order
        public class DateTimeComparator implements Comparator {
//            DateFormat f = new SimpleDateFormat("dd/mm/yyyy hh:mm");
            DateFormat f = new SimpleDateFormat("yyyy/mm/dd hh:mm");

            public int compare(Object a, Object b) {
                String o1 = ((DateTimeSorter)a).getDateTime();
                String o2 = ((DateTimeSorter)b).getDateTime();

                try {
                    return f.parse(o1).compareTo(f.parse(o2));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        }

        // UI and data class for recycler view items
        public  class VerticalItemHolder extends SwappingHolder
                implements View.OnClickListener, View.OnLongClickListener {
            private TextView mTitleText, mDateAndTimeText, mRepeatInfoText;
            private ImageView mActiveImage , mThumbnailImage;
//            private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
//            private TextDrawable mDrawableBuilder;
            private SimpleAdapter mAdapter;

            public VerticalItemHolder(View itemView, SimpleAdapter adapter) {
                super(itemView, mMultiSelector);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
                itemView.setLongClickable(true);

                // Initialize adapter for the items
                mAdapter = adapter;

                // Initialize views
                mTitleText = (TextView) itemView.findViewById(R.id.recycle_title);
                mDateAndTimeText = (TextView) itemView.findViewById(R.id.recycle_date_time);
                mRepeatInfoText = (TextView) itemView.findViewById(R.id.recycle_repeat_info);
                mActiveImage = (ImageView) itemView.findViewById(R.id.active_image);
                mThumbnailImage = (ImageView) itemView.findViewById(R.id.thumbnail_image);
            }

            // On clicking a reminder item
            @Override
            public void onClick(View v) {
                if (!mMultiSelector.tapSelection(this)) {
                    mTempPost = mList.getChildAdapterPosition(v);

                    int mReminderClickID = IDmap.get(mTempPost);
                    selectReminder(mReminderClickID);

                } else if(mMultiSelector.getSelectedPositions().isEmpty()){
                    mAdapter.setItemCount(getDefaultItemCount());
                }
            }

            // On long press enter action mode with context menu
            @Override
            public boolean onLongClick(View v) {
                AppCompatActivity activity = AlarmActivity.this;
                activity.startSupportActionMode(mDeleteMode);
                mMultiSelector.setSelected(this, true);
                return true;
            }

            // Set reminder title view
            public void setReminderTitle(String title) {
                mTitleText.setText(title);
//                String letter = "A";
//                if(title != null && !title.isEmpty()) {
//                    letter = title.substring(0, 1);
//                }
//                int color = mColorGenerator.getRandomColor();
//                // Create a circular icon consisting of  a random background colour and first letter of title
//                mDrawableBuilder = TextDrawable.builder()
//                        .buildRound(letter, color);
//                mThumbnailImage.setImageDrawable(mDrawableBuilder);
            }

            // Set date and time views
            public void setReminderDateTime(String datetime) {
                mDateAndTimeText.setText(datetime);
            }

            // Set repeat views
            public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
                if(repeat.equals("true")){
                    mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
                }else if (repeat.equals("false")) {
                    mRepeatInfoText.setText("Repeat Off");
                }
            }

            // Set active image as on or off

            public void setActiveImage(String alarmstat){
                if(alarmstat.equals("true")){
                    mActiveImage.setImageResource(R.drawable.ic_notifications_on_black_24dp);
                }else if (alarmstat.equals("false")) {
                    mActiveImage.setImageResource(R.drawable.ic_notifications_off_grey600_24dp);
                }
            }
            // Set active image as on or off
            public void setAlarmimg(String alarmtype){
                if(alarmtype.equals("洗澡")){
                    mThumbnailImage.setImageResource(R.drawable.alarmwash);
                }else if (alarmtype.equals("驅蟲")) {
                    mThumbnailImage.setImageResource(R.drawable.alarmbug2);
                }else if (alarmtype.equals("剪指甲")) {
                    mThumbnailImage.setImageResource(R.drawable.alarmnail);
                }else if (alarmtype.equals("接種疫苗")) {
                    mThumbnailImage.setImageResource(R.drawable.alarmvaccine);
                }else if (alarmtype.equals("餵食")) {
                    mThumbnailImage.setImageResource(R.drawable.alarmfeed);
                }else if (alarmtype.equals("美容")) {
                    mThumbnailImage.setImageResource(R.drawable.alarmsaloon);
                }else if (alarmtype.equals("健康檢查")) {
                    mThumbnailImage.setImageResource(R.drawable.alarmexaming);
                }else if (alarmtype.equals("聚會")) {
                    mThumbnailImage.setImageResource(R.drawable.alarmpetparty);
                }else if (alarmtype.equals("刷牙")) {
                    mThumbnailImage.setImageResource(R.drawable.alarmbrush);
                }else if (alarmtype.equals("溜狗")) {
                    mThumbnailImage.setImageResource(R.drawable.alarmwalking);
                }else if (alarmtype.equals("其他")) {
                    mThumbnailImage.setImageResource(R.drawable.alarmother2);
                }



            }
        }

        // Generate random test data
        public  ReminderItem generateDummyData() {
            return new ReminderItem("1", "2", "3", "4", "5", "6","7","8");
        }

        // Generate real data for each item
        public List<ReminderItem> generateData(int count) {
            ArrayList<ReminderItem> items = new ArrayList<>();

            // Get all reminders from the database
            List<Reminder> reminders = rb.getAllReminders();

            // Initialize lists
            List<String> Titles = new ArrayList<>();
            List<String> Alarmtype = new ArrayList<>();
            List<String> Alarmstat = new ArrayList<>();
            List<String> Repeats = new ArrayList<>();
            List<String> RepeatNos = new ArrayList<>();
            List<String> RepeatTypes = new ArrayList<>();
            List<String> Actives = new ArrayList<>();
            List<String> DateAndTime = new ArrayList<>();
            List<Integer> IDList= new ArrayList<>();
            List<DateTimeSorter> DateTimeSortList = new ArrayList<>();

            // Add details of all reminders in their respective lists
            for (Reminder r : reminders) {
                Titles.add(r.getTitle());
                Alarmtype.add(r.getAlarmtype());
                Alarmstat.add(r.getAlarmstat());
                DateAndTime.add(r.getDate() + " " + r.getTime());
                Repeats.add(r.getRepeat());
                RepeatNos.add(r.getRepeatNo());
                RepeatTypes.add(r.getRepeatType());
                Actives.add(r.getActive());
                IDList.add(r.getID());
            }

            int key = 0;

            // Add date and time as DateTimeSorter objects
            for(int k = 0; k<Titles.size(); k++){
                DateTimeSortList.add(new DateTimeSorter(key, DateAndTime.get(k)));
                key++;
            }

            // Sort items according to date and time in ascending order
            Collections.sort(DateTimeSortList, new DateTimeComparator());

            int k = 0;

            // Add data to each recycler view item
            for (DateTimeSorter item:DateTimeSortList) {
                int i = item.getIndex();

                items.add(new SimpleAdapter.ReminderItem(Titles.get(i),Alarmtype.get(i),Alarmstat.get(i),DateAndTime.get(i), Repeats.get(i),
                        RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
                IDmap.put(k, IDList.get(i));
                k++;
            }
          return items;
        }
    }
}
