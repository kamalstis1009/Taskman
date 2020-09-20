package com.subra.taskman.views.adapters;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.chibde.visualizer.LineVisualizer;
import com.subra.taskman.R;
import com.subra.taskman.models.FileModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder> {

    private FragmentActivity mActivity;
    private ArrayList<FileModel> mArrayList;
    private MyCallBackListener mListener;
    private MediaPlayer mPlayer;

    public interface MyCallBackListener {
        //void onAddRecord(FileModel model);
        void onRemoveRecord(int position, FileModel model);
        //void updateItem(int position, FileModel model);
    }

    public RecordAdapter(FragmentActivity activity, ArrayList<FileModel> arrayList, MyCallBackListener listener) {
        this.mActivity = activity;
        this.mArrayList = arrayList;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        FileModel model = mArrayList.get(position);
        holder.file.setText(model.getFileName());
        holder.path.setText(model.getFilePath());

        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //String path = Environment.getExternalStorageDirectory() + "/My Records/" + dates + "/" + number + "_" + times + ".mp4"  ;
                //File file = new File("/storage/emulated/0/Android/data/com.cobalttechnology.myfirstapplication/files/" + fileName);
                playRecord(model, holder.visualizer);
                //holder.play.setEnabled(false);
                //holder.stop.setEnabled(true);
            }
        });
        holder.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.play.setEnabled(true);
                //holder.stop.setEnabled(false);
                stopRecord();
            }
        });
        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopRecord();
                //holder.play.setEnabled(true);
                //holder.stop.setEnabled(false);
                mListener.onRemoveRecord(position, model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private LineVisualizer visualizer;
        ImageButton play, stop, remove;
        TextView file, path;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            visualizer = (LineVisualizer) itemView.findViewById(R.id.line_visualizer);
            play = (ImageButton) itemView.findViewById(R.id.play_button);
            stop = (ImageButton) itemView.findViewById(R.id.stop_button);
            remove = (ImageButton) itemView.findViewById(R.id.remove_item_button);
            file = (TextView) itemView.findViewById(R.id.file_name);
            path = (TextView) itemView.findViewById(R.id.file_path);
        }
    }

    private void playRecord(FileModel model, LineVisualizer lineVisualizer) {
        mPlayer = new MediaPlayer();
        try {
            FileInputStream mInputStream = new FileInputStream(model.getFilePath() + "/" + model.getFileName());
            mPlayer.setDataSource(mInputStream.getFD());
            mInputStream.close();
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mPlayer.start();
        /*mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
            }
        });*/

        lineVisualizer.setColor(ContextCompat.getColor(mActivity, R.color.colorDeepGrey));
        lineVisualizer.setStrokeWidth(4);
        lineVisualizer.setPlayer(mPlayer.getAudioSessionId());
    }

    private void stopRecord() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.seekTo(0);
        }
    }
}
