package com.subra.taskman.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subra.taskman.R;
import com.subra.taskman.models.CallModel;
import com.subra.taskman.utils.Utility;

import java.util.ArrayList;

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.MyViewHolder> implements Filterable {

    //private FragmentActivity mActivity;
    private ArrayList<CallModel> mArrayList;
    private ArrayList<CallModel> mArrayList1;
    private MyCallBackListener mListener;

    public interface MyCallBackListener {
        //void onAddItem(CallModel model);
        void onRemoveItem(int position, CallModel model);
        //void updateItem(int position, CallModel model);
    }

    public CallAdapter(ArrayList<CallModel> arrayList, MyCallBackListener listener) {
        this.mArrayList = arrayList;
        this.mArrayList1 = arrayList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meeting, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CallModel model = mArrayList.get(position);
        holder.title.setText(model.getSubject());
        holder.remarks.setText(model.getPurpose());
        holder.date.setText(model.getDate());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.button.isChecked()) {
                    mListener.onRemoveItem(position, model);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton button;
        TextView title, remarks, date;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            button = (RadioButton) itemView.findViewById(R.id.radio_button);
            title = (TextView) itemView.findViewById(R.id.meeting_title);
            remarks = (TextView) itemView.findViewById(R.id.meeting_remarks);
            date = (TextView) itemView.findViewById(R.id.meeting_date);
        }
    }

    //http://programmingroot.com/android-recyclerview-search-filter-tutorial-with-example/
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()){
                    mArrayList = mArrayList1;
                } else {
                    ArrayList<CallModel> filterList = new ArrayList<>();
                    for (CallModel model : mArrayList1){
                        if (Utility.getInstance().getDateFromTimestamp(model.getCreatedAt()).contains(charString)){
                            filterList.add(model);
                        }
                    }
                    mArrayList = filterList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mArrayList = (ArrayList<CallModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }

}
