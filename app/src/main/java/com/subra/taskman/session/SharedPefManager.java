package com.subra.taskman.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.subra.taskman.models.CallModel;
import com.subra.taskman.models.ContactModel;
import com.subra.taskman.models.MeetingModel;
import com.subra.taskman.models.TaskModel;
import com.subra.taskman.models.UserModel;

import java.util.ArrayList;

public class SharedPefManager {

    private Context mContext;
    private static SharedPefManager mManager;

    private SharedPefManager(Context context) {
        mContext = context;
    }

    public static SharedPefManager getInstance(Context context) {
        if (mManager == null) {
            mManager = new SharedPefManager(context);
        }
        return mManager;
    }

    private static final String SHARED_PREF_NAME = "shared_preferences";

    //===============================================| Token
    public void saveDeviceToken(String token){
        SharedPreferences ref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putString("TOKEN_KEY", token);
        editor.apply();
    }
    public String getDeviceToken(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString("TOKEN_KEY", null);
    }

    //===============================================| Current Location
    public void saveDeviceLocation(String location){
        SharedPreferences ref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = ref.edit();
        editor.putString("LOCATION_KEY", location);
        editor.apply();
    }
    public String getDeviceLocation(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return  sharedPreferences.getString("LOCATION_KEY", null);
    }

    //===============================================| User
    public void saveUser(UserModel model){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("UserModel", new Gson().toJson(model));
        editor.apply();
        editor.commit(); //for old version
    }
    public UserModel getUser(){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Gson().fromJson(pref.getString("UserModel", null), UserModel.class);
    }

    //===============================================| Contact Person
    public void saveContact(ContactModel model){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        ArrayList<ContactModel> mArrayList = new ArrayList<>();
        ArrayList<ContactModel> models = getContactList();
        if (models != null) {
            if (model != null) {
                models.add(model);
                editor.putString("ContactModel", new Gson().toJson(models));
            }
        } else {
            mArrayList.add(model);
            editor.putString("ContactModel", new Gson().toJson(mArrayList));
        }
        editor.apply();
        editor.commit(); //for old version
    }
    public ArrayList<ContactModel> getContactList(){
        SharedPreferences ref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Gson().fromJson(ref.getString("ContactModel", null), new TypeToken<ArrayList<ContactModel>>(){}.getType());
    }

    //===============================================| MeetingModel
    public void saveMeeting(MeetingModel model){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        ArrayList<MeetingModel> mArrayList = new ArrayList<>();
        ArrayList<MeetingModel> models = getMeetingList();
        if (models != null) {
            if (model != null) {
                models.add(model);
                editor.putString("MeetingModel", new Gson().toJson(models));
            }
        } else {
            mArrayList.add(model);
            editor.putString("MeetingModel", new Gson().toJson(mArrayList));
        }
        editor.apply();
        editor.commit(); //for old version
    }
    public ArrayList<MeetingModel> getMeetingList(){
        SharedPreferences ref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Gson().fromJson(ref.getString("MeetingModel", null), new TypeToken<ArrayList<MeetingModel>>(){}.getType());
    }
    public void removeMeeting(MeetingModel model, int position){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        ArrayList<MeetingModel> mArrayList = getMeetingList();
        if (mArrayList != null) {
            if (model != null) {
                mArrayList.remove(position);
                editor.putString("MeetingModel", new Gson().toJson(mArrayList));
                editor.apply();
                editor.commit(); //for old version
            }
        }
    }
    public void removeAllMeeting(){
        SharedPreferences pre = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pre.edit();
        editor.remove("MeetingModel");
        //editor.clear(); //Remove from login.xml file
        editor.apply();
        editor.commit(); //for old version
    }
    public void restoreMeetingModel(MeetingModel model){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        ArrayList<MeetingModel> mArrayList = getMeetingList();
        if (mArrayList != null) {
            if (model != null) {
                mArrayList.add(model);
                editor.putString("MeetingModel", new Gson().toJson(mArrayList));
                editor.apply();
                editor.commit(); //for old version
            }
        }
    }

    //===============================================| TaskModel
    public void saveTask(TaskModel model){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        ArrayList<TaskModel> mArrayList = new ArrayList<>();
        ArrayList<TaskModel> models = getTaskList();
        if (models != null) {
            if (model != null) {
                models.add(model);
                editor.putString("TaskModel", new Gson().toJson(models));
            }
        } else {
            mArrayList.add(model);
            editor.putString("TaskModel", new Gson().toJson(mArrayList));
        }
        editor.apply();
        editor.commit(); //for old version
    }
    public ArrayList<TaskModel> getTaskList(){
        SharedPreferences ref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Gson().fromJson(ref.getString("TaskModel", null), new TypeToken<ArrayList<TaskModel>>(){}.getType());
    }
    public void removeTask(TaskModel model, int position){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        ArrayList<TaskModel> mArrayList = getTaskList();
        if (mArrayList != null) {
            if (model != null) {
                mArrayList.remove(position);
                editor.putString("TaskModel", new Gson().toJson(mArrayList));
                editor.apply();
                editor.commit(); //for old version
            }
        }
    }

    //===============================================| CallModel
    public void saveCall(CallModel model){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        ArrayList<CallModel> mArrayList = new ArrayList<>();
        ArrayList<CallModel> models = getCallList();
        if (models != null) {
            if (model != null) {
                models.add(model);
                editor.putString("CallModel", new Gson().toJson(models));
            }
        } else {
            mArrayList.add(model);
            editor.putString("CallModel", new Gson().toJson(mArrayList));
        }
        editor.apply();
        editor.commit(); //for old version
    }
    public ArrayList<CallModel> getCallList(){
        SharedPreferences ref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Gson().fromJson(ref.getString("CallModel", null), new TypeToken<ArrayList<CallModel>>(){}.getType());
    }
    public void removeCall(CallModel model, int position){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        ArrayList<CallModel> mArrayList = getCallList();
        if (mArrayList != null) {
            if (model != null) {
                mArrayList.remove(position);
                editor.putString("CallModel", new Gson().toJson(mArrayList));
                editor.apply();
                editor.commit(); //for old version
            }
        }
    }

}
