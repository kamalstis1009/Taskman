package com.subra.taskman.session;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.subra.taskman.models.ContactModel;
import com.subra.taskman.models.MeetingModel;
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

    //===============================================| Your Product
    public void saveContact(ContactModel model){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        ArrayList<ContactModel> mArrayList = new ArrayList<>();
        ArrayList<ContactModel> products = getContactList();
        if (products != null) {
            if (model != null) {
                products.add(model);
                editor.putString("ContactModel", new Gson().toJson(products));
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
    public void saveMeetingModel(ArrayList<MeetingModel> mArrayList){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("MeetingModel", new Gson().toJson(mArrayList));
        editor.apply();
        editor.commit(); //for old version
    }
    public ArrayList<MeetingModel> getMeetingModel(){
        SharedPreferences ref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new Gson().fromJson(ref.getString("MeetingModel", null), new TypeToken<ArrayList<MeetingModel>>(){}.getType());
    }
    public void removeMeetingModel(MeetingModel model, int position){
        SharedPreferences pref = mContext.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        ArrayList<MeetingModel> mArrayList = getMeetingModel();
        if (mArrayList != null) {
            if (model != null) {
                mArrayList.remove(position);
                editor.putString("MeetingModel", new Gson().toJson(mArrayList));
                editor.apply();
                editor.commit(); //for old version
            }
        }
    }
    public void removeAllMeetingModel(){
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
        ArrayList<MeetingModel> mArrayList = getMeetingModel();
        if (mArrayList != null) {
            if (model != null) {
                mArrayList.add(model);
                editor.putString("MeetingModel", new Gson().toJson(mArrayList));
                editor.apply();
                editor.commit(); //for old version
            }
        }
    }

}
