package com.subra.taskman.views.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.subra.taskman.R;
import com.subra.taskman.models.MeetingModel;
import com.subra.taskman.session.SharedPefManager;

import java.util.ArrayList;
import java.util.Arrays;

public class MeetingFragment extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meeting, container, false);

        //------------------------------------------------| Get Bundle Data
        if (getArguments() != null && getArguments().getString("mDuration") != null) {}

        //------------------------------------------------| findViewById()
        EditText mTitle = (EditText) view.findViewById(R.id.meeting_title);
        Spinner mClient = (Spinner) view.findViewById(R.id.meeting_client);
        EditText mDate = (EditText) view.findViewById(R.id.meeting_date);
        ChipGroup mChipGroup = (ChipGroup) view.findViewById(R.id.meeting_participants);
        EditText mLocation = (EditText) view.findViewById(R.id.meeting_location);
        EditText mDescription = (EditText) view.findViewById(R.id.meeting_description);

        //------------------------------------------------| Participants
        ArrayList<String> mArrayList = new ArrayList( Arrays.asList( new String[]{"Jasim", "Hasan", "Mamun", "Monir", "Yousuf"} ) );
        for (int index = 0; index < mArrayList.size(); index++) {
            String tagName = mArrayList.get(index);
            Chip chip = new Chip(getActivity());
            int paddingDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
            chip.setText(tagName);
            chip.setCloseIconResource(R.drawable.ic_baseline_close_24);
            chip.setCloseIconEnabled(true);
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

        ((Button) view.findViewById(R.id.add_product_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = mTitle.getText().toString().trim();
                String client = mClient.getSelectedItem().toString();
                String date = mDate.getText().toString().trim();
                String location = mLocation.getText().toString().trim();
                String description = mDescription.getText().toString().trim();

                MeetingModel model = new MeetingModel();
                model.setTitle(title);
                model.setClient(client);
                model.setFromDate(date);
                model.setLocation(location);
                model.setRemarks(description);
                model.setParticipants(mArrayList);

                SharedPefManager.getInstance(getActivity()).saveMeetingModels(model);
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
}