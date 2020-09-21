package com.subra.taskman.views.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.subra.taskman.R;
import com.subra.taskman.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

public class CallFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks {

    private static final int ACTION_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 4;
    private static final int REQUEST_CAMERA = 202;
    private static final int REQUEST_GALLERY = 203;

    private String[] CAMERA_PERMISSIONS = { android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, };
    private String[] GALLERY_PERMISSIONS = { android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,};

    private File mFile;
    private String currentPhotoPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        //------------------------------------------------| Get Bundle Data
        if (getArguments() != null && getArguments().getString("mDuration") != null) {}

        ((ImageButton) view.findViewById(R.id.back_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    //===============================================| Remove modal dialog background scrim grey color
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

    //====================================================|
    private void showContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_contact_person, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        builder.create();
        final AlertDialog dialog = builder.show();

        EditText fullName = (EditText) view.findViewById(R.id.full_name);
        EditText email = (EditText) view.findViewById(R.id.email);
        EditText company = (EditText) view.findViewById(R.id.company);
        EditText department = (EditText) view.findViewById(R.id.department);
        EditText designation = (EditText) view.findViewById(R.id.designation);
        EditText phone = (EditText) view.findViewById(R.id.phone);
        EditText description = (EditText) view.findViewById(R.id.description);

        ((CircleImageView) view.findViewById(R.id.person_photo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) view.findViewById(R.id.add_contact_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
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
        String mImgName = "IMG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        if (requestCode == ACTION_PICK_REQUEST_CODE && resultCode == getActivity().RESULT_OK && data != null) {
            Uri uri = data.getData();
            Bitmap bitmap = Utility.getInstance().getDownBitmap(getActivity(), uri, 250, 250);
            //imageView.setImageBitmap(bitmap);
            String mImagePath = Utility.getInstance().saveToInternalStorage(getActivity(), bitmap, mImgName);
            mFile = new File(mImagePath, mImgName);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && currentPhotoPath != null) {
            Uri uri = Uri.fromFile(new File(currentPhotoPath));
            Bitmap bitmap = Utility.getInstance().getDownBitmap(getActivity(), uri, 250, 250);
            //imageView.setImageBitmap(bitmap);
            String mImagePath = Utility.getInstance().saveToInternalStorage(getActivity(), bitmap, mImgName);
            mFile = new File(mImagePath, mImgName);
        }
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