package com.subra.taskman.views.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.subra.taskman.R;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class CallFragment extends BottomSheetDialogFragment {

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

    //====================================================| Camera and Gallery Dialog
    private void showDialog() {
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
}