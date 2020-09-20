package com.subra.taskman.views.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.subra.taskman.R;
import com.subra.taskman.models.MeetingModel;
import com.subra.taskman.models.UserModel;
import com.subra.taskman.session.SharedPefManager;
import com.subra.taskman.utils.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MeetingFragment extends BottomSheetDialogFragment {

    private LatLng mLatLng;
    private EditText mLocation;
    private BottomSheetListener mListener;

    public interface BottomSheetListener {
        void onAddItem(MeetingModel model);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting, container, false);

        //------------------------------------------------| Session
        UserModel mUser = SharedPefManager.getInstance(getActivity()).getUser();
        String mCurrentLocation = SharedPefManager.getInstance(getActivity()).getDeviceLocation();
        if (mCurrentLocation != null) {
            String[] arr = mCurrentLocation.split(",");
            mLatLng = new LatLng(Double.parseDouble(arr[0]), Double.parseDouble(arr[1]));
        }

        //------------------------------------------------| Get Bundle Data
        if (getArguments() != null && getArguments().getString("mDuration") != null) {
            //
        }

        //------------------------------------------------| findViewById()
        EditText mTitle = (EditText) view.findViewById(R.id.meeting_title);
        Spinner mClient = (Spinner) view.findViewById(R.id.meeting_client);
        EditText mFromDate = (EditText) view.findViewById(R.id.meeting_from_date);
        EditText mToDate = (EditText) view.findViewById(R.id.meeting_to_date);
        ChipGroup mChipGroup = (ChipGroup) view.findViewById(R.id.meeting_participants);
        mLocation = (EditText) view.findViewById(R.id.meeting_location);
        EditText mDescription = (EditText) view.findViewById(R.id.meeting_description);

        //------------------------------------------------| Participants
        ArrayList<String> mArrayList = new ArrayList<>(); //new ArrayList<>(Arrays.asList( new String[]{"kamalstis1009@gmail.com"} ));
        for (int index = 0; index < mArrayList.size(); index++) {
            String tagName = mArrayList.get(index);
            Chip chip = addChip(tagName);
            //Added click listener on close icon to remove tag from ChipGroup
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mArrayList.remove(tagName);
                    mChipGroup.removeView(chip);
                }
            });
            mChipGroup.addView(chip);
        }

        ((ImageButton) view.findViewById(R.id.add_participant_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.BorderRoundAlertDialog);
                View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_chip, null, false);
                builder.setView(view);
                builder.setCancelable(true);
                builder.create();
                AlertDialog dialog = builder.show();
                EditText pEmail = (EditText) view.findViewById(R.id.participant_email);
                ((Button) view.findViewById(R.id.add_button)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email = pEmail.getText().toString().trim();
                        if (!TextUtils.isEmpty(email)) {
                            Chip chip = addChip(email);
                            mChipGroup.addView(chip);
                            mArrayList.add(email);
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        mFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.getInstance().getDateTimePickerDialog(getActivity(), mFromDate);
            }
        });

        mToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.getInstance().getDateTimePickerDialog(getActivity(), mToDate);
            }
        });

        ((ImageButton) view.findViewById(R.id.location_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMapsDialog();
            }
        });

        ((Button) view.findViewById(R.id.add_product_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mUser != null) {
                    //
                }
                String title = mTitle.getText().toString().trim();
                String client = mClient.getSelectedItem().toString();
                String fromDate = mFromDate.getText().toString().trim();
                String toDate = mToDate.getText().toString().trim();
                //String location = mLocation.getText().toString().trim();
                String description = mDescription.getText().toString().trim();

                MeetingModel model = new MeetingModel();
                model.setTitle(title);
                model.setClient(client);
                model.setFromDate(fromDate);
                model.setToDate(toDate);
                model.setLocation(mLatLng.latitude + "," + mLatLng.longitude);
                model.setParticipants(mArrayList);
                model.setRemarks(description);
                //model.setUserId(mUser.getUserId());

                mListener.onAddItem(model);
                dismiss();
            }
        });

        ((ImageButton) view.findViewById(R.id.back_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }

    private Chip addChip(String tagName) {
        Chip chip = new Chip(getActivity());
        int paddingDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(tagName);
        chip.setCloseIconResource(R.drawable.ic_baseline_close_24);
        chip.setCloseIconEnabled(true);
        return chip;
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

    //====================================================| Google Maps Dialog
    private void showMapsDialog() {
        Dialog dialog = new Dialog(getActivity()); //new Dialog(PostAdActivity.this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); //make map clear
        dialog.setContentView(R.layout.dialog_maps);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setCancelable(true); //dismiss by clicking outside
        dialog.show();

        MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(getActivity());
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();

        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap googleMap) {
                if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                } else {
                    googleMap.setMyLocationEnabled(true);
                }

                googleMap.getUiSettings().setZoomControlsEnabled(true);
                googleMap.getUiSettings().setAllGesturesEnabled(true);
                googleMap.getUiSettings().setZoomGesturesEnabled(true);

                if (mLatLng != null) {
                    //Utility.getInstance().changeCurrentLocationIcon(mMapView);
                    Utility.getInstance().moveToLocation(googleMap, mLatLng);
                }

                googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
                    @Override
                    public void onCameraIdle() {
                        mLatLng = googleMap.getCameraPosition().target; //float mZoom = mMap.getCameraPosition().zoom; //double lat =  mMap.getCameraPosition().target.latitude; //double lng =  mMap.getCameraPosition().target.longitude;
                        //String address = Utility.getInstance().getAddress(getActivity(), mLatLng);
                    }
                });

                ((Button) dialog.findViewById(R.id.pick_point_ok)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        if (mLatLng != null) {
                            mLocation.setText(Utility.getInstance().getAddress(getActivity(), mLatLng));
                        } else {
                            Snackbar.make(getActivity().findViewById(android.R.id.content), "Latitude and longitude are missing.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });

                /*if (mLatLng != null) {
                    Utility.moveToLocation(googleMap, mLatLng);
                    googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            String address = Utility.getAddress(PostAdActivity.this, mLatLng);
                            Log.d(TAG, "Address: "+address);
                            Log.d(TAG, "LatLng: "+mLatLng);
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(latLng.latitude, latLng.longitude)).title(address)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_house2));//position:(lat,lng).title(address).setIcon(drawable)

                            addr.setVisibility(View.VISIBLE);
                            addr.setText(address);
                            dialog.dismiss();
                        }
                    });
                }*/
            }
        });
    }
}