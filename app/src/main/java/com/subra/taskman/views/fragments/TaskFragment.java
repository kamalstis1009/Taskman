package com.subra.taskman.views.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.subra.taskman.models.TaskModel;
import com.subra.taskman.services.RecordForegroundService;
import com.subra.taskman.utils.ConstantKey;
import com.subra.taskman.utils.TimeCount;
import com.subra.taskman.utils.Utility;
import com.subra.taskman.views.adapters.AttachmentAdapter;
import com.subra.taskman.views.adapters.RecordAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

import static android.app.Activity.RESULT_OK;

public class TaskFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks, AttachmentAdapter.MyCallBackListener, RecordAdapter.MyCallBackListener {

    private static final String TAG = "TaskFragment";
    private BottomSheetListener mListener;

    public interface BottomSheetListener {
        void onAddItem(TaskModel model);
    }

    private static final int ACTION_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 4;
    private static final int REQUEST_RECORD = 101;
    private static final int REQUEST_CAMERA = 102;
    private static final int REQUEST_GALLERY = 103;

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
    private String[] RECORD_PERMISSIONS = { android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, };
    private String[] CAMERA_PERMISSIONS = { android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, };
    private String[] GALLERY_PERMISSIONS = { android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,};

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                int resultCode = bundle.getInt("RECORD_RESULT");
                if (resultCode == RESULT_OK) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getInternalStorageFiles();
                        }
                    });
                }
            }
        }
    };

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
        getInternalStorageFiles();

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
        getActivity().registerReceiver(receiver, new IntentFilter(ConstantKey.BROADCAST_RECEIVER));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(receiver);
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
                    recordRequestPermissions();
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
        File file = new File(model.getFilePath()+"/"+model.getFileName());
        if (file.exists()) {
            if (file.delete()) {
                Toast.makeText(getActivity(), "Deleted the file successfully", Toast.LENGTH_SHORT).show();
                if (mRecordList != null && mRecordList.size() > 0) {
                    //mArrayList.remove(model);
                    mRecordList.remove(position);
                    mRecordRecyclerView.removeViewAt(position);
                    mRecordAdapter.notifyItemRemoved(position);
                    mRecordAdapter.notifyItemRangeChanged(position, mRecordList.size());
                }
            } else {
                Toast.makeText(getActivity(), "Did not delete this file " + model.getFileName(), Toast.LENGTH_SHORT).show();
            };
        }
    }

    //====================================================| RecyclerView
    private void initRecyclerView1(RecyclerView mRecyclerView, ArrayList<FileModel> arrayList) {
        mAttachAdapter = new AttachmentAdapter(arrayList, this);
        mRecyclerView.setAdapter(mAttachAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5 /*px spacing*/));
        mAttachAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView2(RecyclerView mRecyclerView, ArrayList<FileModel> arrayList) {
        mRecordAdapter = new RecordAdapter(getActivity(), arrayList, this);
        mRecyclerView.setAdapter(mRecordAdapter);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //mRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(5 /*px spacing*/));
        mRecordAdapter.notifyDataSetChanged();
    }

    //====================================================| Check Permissions for Mic, Camera, Gallery
    //@AfterPermissionGranted(REQUEST_RECORD)
    private void recordRequestPermissions() {
        if (EasyPermissions.hasPermissions(getActivity(), RECORD_PERMISSIONS)) {
            // Already have permission, do the thing
            showRecordDialog();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This app needs access to your mic to make record voice", REQUEST_RECORD, RECORD_PERMISSIONS);
        }
    }

    //@AfterPermissionGranted(REQUEST_CAMERA)
    private void cameraRequestPermissions() {
        if (EasyPermissions.hasPermissions(getActivity(), CAMERA_PERMISSIONS)) {
            // Already have permission, do the thing
            getCamera();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This app needs access to your camera to capture photo", REQUEST_CAMERA, CAMERA_PERMISSIONS);
        }
    }

    //@AfterPermissionGranted(REQUEST_GALLERY)
    private void galleryRequestPermissions() {
        if (EasyPermissions.hasPermissions(getActivity(), GALLERY_PERMISSIONS)) {
            // Already have permission, do the thing
            getGallery();
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, "This app needs access to your gallery to access images", REQUEST_GALLERY, GALLERY_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> permissions) {
        // Some permissions have been granted
        if (Arrays.equals(permissions.toArray(new String[0]), RECORD_PERMISSIONS) && requestCode == REQUEST_RECORD) {
            showRecordDialog();
        }
        if (Arrays.equals(permissions.toArray(new String[0]), CAMERA_PERMISSIONS) && requestCode == REQUEST_CAMERA) {
            getCamera();
        }
        if (Arrays.equals(permissions.toArray(new String[0]), GALLERY_PERMISSIONS) && requestCode == REQUEST_GALLERY) {
            getGallery();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        // Some permissions have been denied
    }

    //====================================================| Permissions Result for Camera, Gallery
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

    //====================================================| Record Dialog
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
        stopBtn.setEnabled(false);
        //playBtn.setEnabled(false);

        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //new BackTask().execute("start");
                startMyService(getActivity(), ConstantKey.RECORDING, "Nothing");
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
                //new BackTask().execute("stop");
                stopMyService(getActivity());
                recordBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                //playBtn.setEnabled(true);
                TimeCount.getInstance().stopCounter();
                dialog.dismiss();
            }
        });
        ((ImageButton) view.findViewById(R.id.close_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void startMyService(Context context, String key, String value) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            ContextCompat.startForegroundService(context, new Intent(context, RecordForegroundService.class).putExtra(key, value)); //ForegroundService
        } else {
            context.startService(new Intent(context, RecordForegroundService.class).putExtra(key, value)); //BackgroundService
        }
    }

    private void stopMyService(Context context) {
        context.stopService(new Intent(context, RecordForegroundService.class)); //ForegroundService
    }

    private void getInternalStorageFiles() {
        //String path = Environment.getExternalStorageDirectory().toString() + "/Testing"; //getExternalFilesDir(), getExternalCacheDir(), or getExternalMediaDir()
        //String path = this.getApplicationContext().getFilesDir() + "/system_sound"; //file.getAbsolutePath()
        //String[] listOfFiles = Environment.getExternalStoragePublicDirectory (Environment.DIRECTORY_DOWNLOADS).list();

        String path = getActivity().getFilesDir().getPath() + "/records/";
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                mRecordList.add(new FileModel(file.getName(), path));
            }
        }
        if (mRecordList != null && mRecordList.size() > 0) {
            initRecyclerView2(mRecordRecyclerView, mRecordList);
        }
    }


    //====================================================| Camera and Gallery Dialog
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
                cameraRequestPermissions();
                dialog.dismiss();
            }
        });
        ((ImageButton) view.findViewById(R.id.gallery_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryRequestPermissions();
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

    private void getGallery() {
        startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI), ACTION_PICK_REQUEST_CODE);
    }

}