package com.subra.taskman.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.subra.taskman.R;
import com.subra.taskman.models.FileModel;

import java.util.ArrayList;

public class AttachmentAdapter extends RecyclerView.Adapter<AttachmentAdapter.MyViewHolder> {

    //private FragmentActivity mActivity;
    private ArrayList<FileModel> mArrayList;
    private MyCallBackListener mListener;

    public interface MyCallBackListener {
        //void onAddAttachment(FileModel model);
        void onRemoveAttachment(int position, FileModel model);
        //void updateItem(int position, FileModel model);
    }

    public AttachmentAdapter(ArrayList<FileModel> arrayList, MyCallBackListener listener) {
        this.mArrayList = arrayList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FileModel model = mArrayList.get(position);
        holder.file.setText(model.getFileName());
        holder.path.setText(model.getFilePath());

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onRemoveAttachment(position, model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageButton button;
        TextView file, path;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            button = (ImageButton) itemView.findViewById(R.id.remove_item_button);
            file = (TextView) itemView.findViewById(R.id.file_name);
            path = (TextView) itemView.findViewById(R.id.file_path);
        }
    }
}
