package com.subra.taskman.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.subra.taskman.R;
import com.subra.taskman.models.MeetingModel;

import java.util.ArrayList;

public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MyViewHolder> {

    private FragmentActivity mActivity;
    ArrayList<MeetingModel> mArrayList;

    public MeetingAdapter(FragmentActivity mActivity, ArrayList<MeetingModel> arrayList) {
        this.mActivity = mActivity;
        this.mArrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meeting, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MeetingModel model = mArrayList.get(position);
        holder.title.setText(model.getTitle());
        holder.remarks.setText(model.getRemarks());
        holder.date.setText(model.getDate());
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, remarks, date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.meeting_title);
            remarks = (TextView) itemView.findViewById(R.id.meeting_remarks);
            date = (TextView) itemView.findViewById(R.id.meeting_date);
        }
    }

}
