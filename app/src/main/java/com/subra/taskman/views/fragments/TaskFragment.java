package com.subra.taskman.views.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.subra.taskman.R;
import com.subra.taskman.models.FileModel;
import com.subra.taskman.models.MeetingModel;
import com.subra.taskman.utils.ConstantKey;
import com.subra.taskman.utils.PermissionUtils;
import com.subra.taskman.utils.TimeCount;
import com.subra.taskman.utils.Utility;
import com.subra.taskman.views.adapters.AttachmentAdapter;
import com.subra.taskman.views.adapters.RecordAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class TaskFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks, AttachmentAdapter.MyCallBackListener, RecordAdapter.MyCallBackListener {

    private BottomSheetListener mListener;

    public interface BottomSheetListener {
        void onAddItem(MeetingModel model);
    }

    private static final int ACTION_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_CODE = 2;
    private static final int REQUEST_CODE_CAMERA = 3;
    private static final int REQUEST_IMAGE_CAPTURE = 4;
    private static final int REQUEST_RECORD = 5;

    private ArrayList<FileModel> mAttachList = new ArrayList<>();
    private RecyclerView mAttachRecyclerView;
    private AttachmentAdapter mAttachAdapter;

    private ArrayList<FileModel> mRecordList = new ArrayList<>();
    private RecyclerView mRecordRecyclerView;
    private RecordAdapter mRecordAdapter;

    //Attachment
    private File mFile;
    private String currentPhotoPath;

    //Record
    private String[] PERMISSIONS = {
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private String mFilePath;
    private boolean isStarted;
    private MediaRecorder mRecorder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        //------------------------------------------------| Get Bundle Data
        if (getArguments() != null && getArguments().getString("mDuration") != null) {}

        //------------------------------------------------| findViewById()
        EditText mTitle = (EditText) view.findViewById(R.id.task_title);
        EditText mDate = (EditText) view.findViewById(R.id.task_date);
        Spinner mPriority = (Spinner) view.findViewById(R.id.task_priority);
        Spinner mStatus = (Spinner) view.findViewById(R.id.task_status);
        EditText mDescription = (EditText) view.findViewById(R.id.task_description);
        ((ImageButton) view.findViewById(R.id.add_attachment_button)).setOnClickListener(new ActionEventHandler());
        ((ImageButton) view.findViewById(R.id.add_record_button)).setOnClickListener(new ActionEventHandler());
        ((Button) view.findViewById(R.id.add_task_button)).setOnClickListener(new ActionEventHandler());
        ((ImageButton) view.findViewById(R.id.back_button)).setOnClickListener(new ActionEventHandler());

        //-----------------------------------------------| Attachment
        mAttachRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_attachment);
        initRecyclerView1(mAttachRecyclerView, mAttachList);

        //-----------------------------------------------| Record
        mRecordRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_record);
        initRecyclerView2(mRecordRecyclerView, mRecordList);

        return view;
    }

    //===============================================| onAttach
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        //getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    //===============================================| Back pressed dismiss
    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

    //===============================================| Display all full size at first
    private void expandedFullLayout() {
        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private class ActionEventHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.back_button :
                    dismiss();
                    break;
                case R.id.add_attachment_button :
                    showDialog();
                    break;
                case R.id.add_record_button :
                    /*if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[] {Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD);
                    } else {
                        showRecordDialog();
                    }*/
                    requestPermissions();
                    break;
                case R.id.add_task_button :
                    //mListener.onAddItem(model);
                    //dismiss();
                    break;
            }
        }
    }

    public void onAddAttachment(FileModel model) {
        mAttachList.add(model);
        mAttachAdapter.notifyItemInserted(mAttachList.size());
    }

    @Override
    public void onRemoveAttachment(int position, FileModel model) {
        if (mAttachList != null && mAttachList.size() > 0) {
            //mArrayList.remove(model);
            mAttachList.remove(position);
            mAttachRecyclerView.removeViewAt(position);
            mAttachAdapter.notifyItemRemoved(position);
            mAttachAdapter.notifyItemRangeChanged(position, mAttachList.size());
        }
    }

    @Override
    public void onRemoveRecord(int position, FileModel model) {
        if (mRecordList != null && mRecordList.size() > 0) {
            //mArrayList.remove(model);
            mRecordList.remove(position);
            mRecordRecyclerView.removeViewAt(position);
            mRecordAdapter.notifyItemRemoved(position);
            mRecordAdapter.notifyItemRangeChanged(position, mRecordList.size());
        }
    }

    private void initRecyclerView1(RecyclerView mRecyclerView, ArrayList<FileModel> arrayList) {
        mAttachAdapter = new AttachmentAdapter(arrayList, this);
        mRecyclerView.setAdapter(mAttachAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5 /*px spacing*/));
        mAttachAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView2(RecyclerView mRecyclerView, ArrayList<FileModel> arrayList) {
        mRecordAdapter = new RecordAdapter(arrayList, this);
        mRecyclerView.setAdapter(mRecordAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5 /*px spacing*/));
        mRecordAdapter.notifyDataSetChanged();
    }

    //====================================================| For Record
    @AfterPermissionGranted(REQUEST_RECORD)
    private void requestPermissions() {
        if (EasyPermissions.hasPermissions(getActivity(), PERMISSIONS)) {
            // Already have permission, do the thing
            showRecordDialog();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", REQUEST_RECORD, PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        // Some permissions have been granted
        showRecordDialog();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // Some permissions have been denied
    }

    private void showRecordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_record, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        builder.create();
        final AlertDialog dialog = builder.show();

        TextView timer = (TextView) view.findViewById(R.id.record_timer);
        ImageButton recordBtn = (ImageButton) view.findViewById(R.id.record_button);
        ImageButton stopBtn = (ImageButton) view.findViewById(R.id.stop_button);
        ImageButton playBtn = (ImageButton) view.findViewById(R.id.play_button);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackTask().execute("start");
                recordBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                TimeCount.getInstance().getCounter(new TimeCount.ShowCounter() {
                    @Override
                    public void onCallback(int value) {
                        timer.setText("" + value + " Seconds");
                    }
                });
            }
        });
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new BackTask().execute("stop");
                recordBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                playBtn.setEnabled(true);
                TimeCount.getInstance().stopCounter();
                dialog.dismiss();
            }
        });
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaPlayer mPlayer = new MediaPlayer();
                try {
                    if (mFilePath != null) {
                        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mPlayer.setDataSource(getActivity(), Uri.parse(mFilePath));
                        mPlayer.prepare();
                        mPlayer.start();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        ((ImageButton) view.findViewById(R.id.close_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    //===============================================| Recording Task
    class BackTask extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... params) {
            if (params[0].equals("start")) {
                try {
                    String path = getActivity().getFilesDir().getPath();
                    File file = new File(path);
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

                    mRecorder = new MediaRecorder();
                    /*mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    mRecorder.setAudioChannels(1);
                    mRecorder.setAudioSamplingRate(8000);
                    mRecorder.setAudioEncodingBitRate(44100);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);*/

                    mRecorder.reset();
                    mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                    if (!file.exists()){
                        file.mkdirs();
                    }
                    mFilePath = file+"/" + "REC_" + timeStamp + ".3gp";
                    mRecorder.setOutputFile(mFilePath);

                    mRecorder.prepare();
                    mRecorder.start();
                    isStarted = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (params[0].equals("stop")) {
                if (isStarted && mRecorder != null) {
                    mRecorder.stop();
                    mRecorder.reset(); // You can reuse the object by going back to setAudioSource() step
                    mRecorder.release();
                    mRecorder = null;
                    isStarted = false;
                }
            }
            return null;
        }
    }


    //====================================================| For Image
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo_upload_option, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        builder.create();
        final AlertDialog dialog = builder.show();
        ((ImageButton) view.findViewById(R.id.camera_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
                } else {
                    getCamera();
                }
                dialog.dismiss();
            }
        });
        ((ImageButton) view.findViewById(R.id.gallery_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
                } else {
                    getImage();
                }
                dialog.dismiss();
            }
        });
    }

    private void getCamera() {
        if (getActivity() != null) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                try {
                    File photoFile = createImageFile();
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", photoFile); //BuildConfig.APPLICATION_ID || getActivity().getOpPackageName()
                        //Log.d(TAG, "Image Uri: " + photoURI);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    private File createImageFile() throws IOException {
        if (getActivity() != null) {
            String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
            File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName,/* prefix */".jpg",/* suffix */storageDir/* directory */);
            currentPhotoPath = image.getAbsolutePath();
            return image;
        }
        return null;
    }

    private void getImage() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), ACTION_PICK_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_PICK_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            Bitmap bitmap = Utility.getInstance().getDownBitmap(getActivity(), uri, 250, 250);
            //imageView.setImageBitmap(bitmap);
            String mImagePath = Utility.getInstance().saveToInternalStorage(getActivity(), bitmap, ConstantKey.IMAGE_NAME);
            mFile = new File(mImagePath, ConstantKey.IMAGE_NAME);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && currentPhotoPath != null) {
            Uri uri = Uri.fromFile(new File(currentPhotoPath));
            Bitmap bitmap = Utility.getInstance().getDownBitmap(getActivity(), uri, 250, 250);
            //imageView.setImageBitmap(bitmap);
            String mImagePath = Utility.getInstance().saveToInternalStorage(getActivity(), bitmap, ConstantKey.IMAGE_NAME);
            mFile = new File(mImagePath, ConstantKey.IMAGE_NAME);
        }
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getImage();
        }
        if (requestCode == REQUEST_CODE_CAMERA) {
            getCamera();
        }
        if (requestCode == REQUEST_RECORD) {
            showRecordDialog();
        }
    }*/


}