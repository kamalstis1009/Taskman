package com.subra.taskman.views.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.subra.taskman.R;
import com.subra.taskman.models.MeetingModel;
import com.subra.taskman.session.SharedPefManager;
import com.subra.taskman.views.adapters.CallAdapter;
import com.subra.taskman.views.adapters.MeetingAdapter;
import com.subra.taskman.views.adapters.TaskAdapter;
import com.subra.taskman.views.fragments.CallFragment;
import com.subra.taskman.views.fragments.MeetingFragment;
import com.subra.taskman.views.fragments.TaskFragment;

import java.util.ArrayList;
import java.util.Calendar;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;

public class HomeActivity extends AppCompatActivity implements MeetingAdapter.MyCallBackListener, MeetingFragment.BottomSheetListener {

    private RecyclerView mMeetingRecyclerView;
    private ArrayList<MeetingModel> mMeetingList = new ArrayList<>();
    private MeetingAdapter mMeetingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //-----------------------------------------------| CalendarView
        //Help: https://github.com/Mulham-Raee/Horizontal-Calendar
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);
        HorizontalCalendar mCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .build();
        mCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
            }
        });
        mCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
            }
            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView, int dx, int dy) {
            }
            @Override
            public boolean onDateLongClicked(Calendar date, int position) {
                return true;
            }
        });

        //-----------------------------------------------| Meeting
        mMeetingRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_meeting);
        initRecyclerView(mMeetingRecyclerView, mMeetingList);
        ((ImageButton) findViewById(R.id.add_meeting_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MeetingFragment mDialog = new MeetingFragment();
                //mDialog.setArguments(bundle);
                mDialog.setCancelable(false);
                mDialog.show(getSupportFragmentManager(), mDialog.getTag());
            }
        });

        //-----------------------------------------------| Task
        RecyclerView mRecyclerView2 = (RecyclerView) findViewById(R.id.recycler_view_task);
        initRecyclerView2(mRecyclerView2);
        ((ImageButton) findViewById(R.id.add_task_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskFragment mDialog = new TaskFragment();
                //mDialog.setArguments(bundle);
                mDialog.setCancelable(false);
                mDialog.show(getSupportFragmentManager(), mDialog.getTag());
            }
        });

        //-----------------------------------------------| Call
        RecyclerView mRecyclerView3 = (RecyclerView) findViewById(R.id.recycler_view_call);
        initRecyclerView3(mRecyclerView3);
        ((ImageButton) findViewById(R.id.add_call_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CallFragment mDialog = new CallFragment();
                //mDialog.setArguments(bundle);
                mDialog.setCancelable(false);
                mDialog.show(getSupportFragmentManager(), mDialog.getTag());
            }
        });
    }

    private void initRecyclerView(RecyclerView mRecyclerView, ArrayList<MeetingModel> arrayList) {
        if (arrayList != null && arrayList.size() > 0) {
            mMeetingAdapter = new MeetingAdapter(arrayList, this);
            mRecyclerView.setAdapter(mMeetingAdapter);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            //mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5 /*px spacing*/));
            mMeetingAdapter.notifyDataSetChanged();
        }
    }

    private void initRecyclerView2(RecyclerView mRecyclerView) {
        TaskAdapter mAdapter = new TaskAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5 /*px spacing*/));
        mAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView3(RecyclerView mRecyclerView) {
        CallAdapter mAdapter = new CallAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5 /*px spacing*/));
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRemoveItem(int position, MeetingModel model) {
        if (mMeetingList != null && mMeetingList.size() > 0) {
            //mArrayList.remove(model);
            mMeetingList.remove(position);
            mMeetingRecyclerView.removeViewAt(position);
            mMeetingAdapter.notifyItemRemoved(position);
            mMeetingAdapter.notifyItemRangeChanged(position, mMeetingList.size());
        }
    }

    @Override
    public void onAddItem(MeetingModel model) {
        mMeetingList.add(model);
        mMeetingAdapter.notifyItemInserted(mMeetingList.size());
    }
}