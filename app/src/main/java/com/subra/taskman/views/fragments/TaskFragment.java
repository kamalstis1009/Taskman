package com.subra.taskman.views.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.subra.taskman.R;
import com.subra.taskman.models.FileModel;
import com.subra.taskman.models.MeetingModel;
import com.subra.taskman.views.adapters.AttachmentAdapter;
import com.subra.taskman.views.adapters.RecordAdapter;

import java.util.ArrayList;

public class TaskFragment extends BottomSheetDialogFragment implements AttachmentAdapter.MyCallBackListener, RecordAdapter.MyCallBackListener {

    private BottomSheetListener mListener;

    public interface BottomSheetListener {
        void onAddItem(MeetingModel model);
    }

    private ArrayList<FileModel> mAttachList = new ArrayList<>();
    private RecyclerView mAttachRecyclerView;
    private AttachmentAdapter mAttachAdapter;

    private ArrayList<FileModel> mRecordList = new ArrayList<>();
    private RecyclerView mRecordRecyclerView;
    private RecordAdapter mRecordAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);

        //------------------------------------------------| Get Bundle Data
        if (getArguments() != null && getArguments().getString("mDuration") != null) {}

        ((ImageButton) view.findViewById(R.id.back_button)).setOnClickListener(new ActionEventHandler());

        //-----------------------------------------------| Attachment
        mAttachRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_attachment);
        initRecyclerView1(mAttachRecyclerView, mAttachList);

        //-----------------------------------------------| Record
        mRecordRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_record);
        initRecyclerView2(mRecordRecyclerView, mRecordList);

        return view;
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

    private class ActionEventHandler implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.back_button :
                    dismiss();
                    break;
            }
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
}