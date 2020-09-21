package com.subra.taskman.views.fragments;

import android.app.Dialog;
import android.content.Context;
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
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.subra.taskman.R;
import com.subra.taskman.models.CallModel;
import com.subra.taskman.models.ContactModel;
import com.subra.taskman.session.SharedPefManager;
import com.subra.taskman.utils.Utility;
import com.subra.taskman.views.adapters.CustomDropDownAdapter;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.EasyPermissions;

public class CallFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks {

    private BottomSheetListener mListener;

    public interface BottomSheetListener {
        void onAddItem(CallModel model);
    }

    private static final int ACTION_PICK_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 4;
    private static final int REQUEST_CAMERA = 202;
    private static final int REQUEST_GALLERY = 203;

    private String[] CAMERA_PERMISSIONS = { android.Manifest.permission.CAMERA, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, };
    private String[] GALLERY_PERMISSIONS = { android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,};

    private File mFile;
    private String currentPhotoPath;
    private ArrayList<ContactModel> mArrayList = new ArrayList<>();
    private ArrayList<String> mContacts = new ArrayList<>();
    private EditText callDate;
    private Spinner callContact;
    private CircleImageView contactPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        //------------------------------------------------| Get Bundle Data
        if (getArguments() != null && getArguments().getString("mDuration") != null) {}

        //------------------------------------------------| findViewById
        Spinner callStatus = (Spinner) view.findViewById(R.id.call_status);
        callContact = (Spinner) view.findViewById(R.id.call_contact);
        Spinner callType = (Spinner) view.findViewById(R.id.call_type);
        callDate = (EditText) view.findViewById(R.id.call_date);
        EditText callSubject = (EditText) view.findViewById(R.id.call_subject);
        Spinner callPurpose = (Spinner) view.findViewById(R.id.call_purpose);
        Spinner callResult = (Spinner) view.findViewById(R.id.call_result);
        getSpinnerData(callContact);
        callDate.setOnClickListener(new ActionEventHandler());
        ((ImageButton) view.findViewById(R.id.add_contact_button)).setOnClickListener(new ActionEventHandler());
        ((ImageButton) view.findViewById(R.id.back_button)).setOnClickListener(new ActionEventHandler());

        //------------------------------------------------| Add Call
        ((Button) view.findViewById(R.id.add_call_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String status = callStatus.getSelectedItem().toString();
                String contact = callContact.getSelectedItem().toString();
                String type = callType.getSelectedItem().toString();
                String date = callDate.getText().toString().trim();
                String subject = callSubject.getText().toString().trim();
                String purpose = callPurpose.getSelectedItem().toString();
                String result = callResult.getSelectedItem().toString();

                ContactModel mContact = null;
                for(ContactModel cm : mArrayList) {
                    if(cm.getFullName().equals(contact)) mContact = cm;
                }

                CallModel model = new CallModel();
                model.setStatus(status);
                model.setContact(mContact);
                model.setDate(date);
                model.setSubject(type);
                model.setType(subject);
                model.setPurpose(purpose);
                model.setResult(result);
                mListener.onAddItem(model);
                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }

    private class ActionEventHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.back_button :
                    dismiss();
                    break;
                case R.id.call_date :
                    Utility.getInstance().getDateTimePickerDialog(getActivity(), callDate);
                    break;
                case R.id.add_contact_button :
                    showContactDialog();
                    break;
            }
        }
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

    private void getSpinnerData(Spinner spinner) {
        ArrayList<ContactModel> list = SharedPefManager.getInstance(getActivity()).getContactList();
        if (mArrayList != null && mArrayList.size() > 0) {
            mArrayList.addAll(list);
            for (ContactModel pc : mArrayList) {
                mContacts.add(pc.getFullName());
            }
        }
        CustomDropDownAdapter mAdapter = new CustomDropDownAdapter(getActivity(), mContacts);
        spinner.setAdapter(mAdapter);

        //ArrayList<String> mUnits = ConstantKey.getProductCategory();
        //ArrayAdapter mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mNames);
        //mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //CustomDropDownAdapter mAdapter = new CustomDropDownAdapter(getActivity(), mUnits);
        //productCategory.setAdapter(mAdapter);
        // Set default value from preference
        //mSpinner.setSelection(2); //by position
        //districts.setSelection(mAdapter.getPosition(name)); //by value
    }

    //====================================================|
    private void showContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_contact_person, null, false);
        builder.setView(view);
        builder.setCancelable(true);
        builder.create();
        final AlertDialog dialog = builder.show();

        contactPhoto = (CircleImageView) view.findViewById(R.id.person_photo);
        EditText fullName = (EditText) view.findViewById(R.id.full_name);
        EditText email = (EditText) view.findViewById(R.id.email);
        EditText company = (EditText) view.findViewById(R.id.company);
        EditText department = (EditText) view.findViewById(R.id.department);
        EditText designation = (EditText) view.findViewById(R.id.designation);
        EditText phone = (EditText) view.findViewById(R.id.phone);
        EditText description = (EditText) view.findViewById(R.id.description);

        contactPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        ((Button) view.findViewById(R.id.add_contact_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = fullName.getText().toString().trim();
                String mail = email.getText().toString().trim();
                String com = company.getText().toString().trim();
                String dept = department.getText().toString().trim();
                String design = designation.getText().toString().trim();
                String mobile = phone.getText().toString().trim();
                String desc = description.getText().toString().trim();
                ContactModel model = new ContactModel();
                model.setId(UUID.randomUUID().toString());
                model.setFullName(name);
                model.setEmail(mail);
                model.setCompany(com);
                model.setDepartment(dept);
                model.setDesignation(design);
                model.setPhone(mobile);
                model.setDescription(desc);
                model.setImageName(mFile.getName());
                model.setImagePath(mFile.getPath());
                SharedPefManager.getInstance(getActivity()).saveContact(model);
                getSpinnerData(callContact);
                dialog.dismiss();
                //Snackbar.make(getActivity().findViewById(android.R.id.content), "Your vendor's photo did not set", Snackbar.LENGTH_SHORT).show();
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
            contactPhoto.setImageBitmap(bitmap);
            String mImagePath = Utility.getInstance().saveToInternalStorage(getActivity(), bitmap, mImgName, "persons");
            mFile = new File(mImagePath, mImgName);
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && currentPhotoPath != null) {
            Uri uri = Uri.fromFile(new File(currentPhotoPath));
            Bitmap bitmap = Utility.getInstance().getDownBitmap(getActivity(), uri, 250, 250);
            contactPhoto.setImageBitmap(bitmap);
            String mImagePath = Utility.getInstance().saveToInternalStorage(getActivity(), bitmap, mImgName, "persons");
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