package com.subra.taskman.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.subra.taskman.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Pattern;

public class Utility {

    private String TAG = this.getClass().getSimpleName();
    private static Utility mUtility;

    public static Utility getInstance() {
        if (mUtility == null) {
            mUtility = new Utility();
        }
        return mUtility;
    }

    //===============================================| Verify phone number
    public boolean phoneVerify(String phone) {
        String reg = "\\+8801\\d{9}"; //star with +8801 then 9 digit
        return phone.matches(reg);
    }

    //checking double input validation
    public boolean isDouble(String value) {
        final String DOUBLE_PATTERN = "^\\d+(\\.\\d+)?";
        return Pattern.matches(DOUBLE_PATTERN, value);
    }

    //===============================================| Random alphanumeric
    ////UUID.randomUUID().toString()
    public String getRandomString(int length) {
        final String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJLMNOPQRSTUVWXYZ1234567890!@#$%^&*()_+";
        StringBuilder result = new StringBuilder();
        while(length > 0) {
            Random rand = new Random();
            result.append(characters.charAt(rand.nextInt(characters.length())));
            length--;
        }
        return result.toString();
    }

    public String getRandomNumber() {
        return String.valueOf(new Random().nextInt(999999)+1);
    }

    //===============================================| ProgressDialog
    public ProgressDialog showProgressDialog(Context mActivity, final String message, boolean isCancelable) {
        ProgressDialog mProgress = new ProgressDialog(mActivity);
        mProgress.show();
        mProgress.setCancelable(isCancelable); //setCancelable(false); = invisible clicking the outside
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.setMessage(message);
        return mProgress;
    }

    public void dismissProgressDialog(ProgressDialog mProgress) {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }
    //private ProgressDialog mProgress = Utility.getInstance().showProgressDialog(this, getResources().getString( R.string.progress), true);
    //Utility.getInstance().dismissProgressDialog(mProgress);

    //====================================================| Round a double to 2 decimal
    public double roundTwoDecimal(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public String getDecimalFormat(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setGroupingUsed(true);
        df.setGroupingSize(2);
        return df.format(value);
    }

    //====================================================| Remove first character zero from phone number
    public String removeZero(String str) {
        //int n = Character.getNumericValue(str.charAt(0)
        if(Character.getNumericValue(str.charAt(0)) == 0) {
            return str.substring(1); //remove first character
        } else {
            return str;
        }
    }

    //====================================================| Get Time from Timestamp
    public String getTimeFromTimestamp(String input) {
        java.sql.Timestamp ts = java.sql.Timestamp.valueOf(input);
        return String.valueOf(java.text.DateFormat.getTimeInstance().format(ts.getTime())); //java.text.DateFormat.getDateTimeInstance().format(ts.getTime())
    }

    public String getDateFromTimestamp(String input) {
        java.sql.Timestamp ts = java.sql.Timestamp.valueOf(input);
        return String.valueOf(new SimpleDateFormat("dd-MM-yyyy").format(ts)); //java.text.DateFormat.getDateTimeInstance().format(ts.getTime())
    }

    //get date with your format pattern
    public String getFormatFromStringDate(String pattern, Date date) {
        return new SimpleDateFormat(pattern).format(date); //java.text.DateFormat.getDateTimeInstance().format(ts.getTime())
    }

    //get current date with format
    public String getCurrentDate() {
        return getSimpleDateFormat("yyyy-MM-dd");
    }

    //normal date format
    private String getSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }

    //get current year
    public String getYear() {
        return getSimpleDateFormat("yyyy");
    }

    //total day between date calculation
    public String getTotalDay(String dateStart, String dateStop) {
        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);
            //in milliseconds
            long diff = d2.getTime() - d1.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);
            return String.valueOf(diffDays + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Integer> getYearList() {
        ArrayList<Integer> yearList = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2003; i <= year; i++) {
            yearList.add(i);
        }
        return yearList;
    }

    //date validation regex
    public boolean isValidDate(String str) {
        String regex = "\\d{4}-\\d{2}-\\d{2}"; // XXXX-XX-XX
        return str.matches(regex);
    }

    //====================================================| DatePicker
    public void getDatePickerDialog(Context context, final EditText birth) {
        DatePicker datePicker = new DatePicker(context);
        int day = datePicker.getDayOfMonth();
        int mon = datePicker.getMonth();
        int year = datePicker.getYear();
        new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String date = year +"-"+ (String.format("%02d", month+1)) +"-"+ String.format("%02d", dayOfMonth);
                birth.setText(date);
            }
        }, year, mon, day).show();
    }

    //normal date format with 0 infront of days and month
    public String dateFormat(int year, int month,int day){
        String monthZero = (month >=10)? Integer.toString(month):String.format("0%s",Integer.toString(month));
        String dayZero = (day >=10)? Integer.toString(day):String.format("0%s",Integer.toString(day));
        return year+"-"+monthZero+"-"+dayZero;
    }

    //====================================================| Checkbox
    public String getCheckboxValue(LinearLayout checkboxLayout) {
        StringBuilder value = new StringBuilder();
        for(int i=0; i<checkboxLayout.getChildCount(); i++) {
            CheckBox cb = (CheckBox) checkboxLayout.getChildAt(i);
            if (cb.isClickable()) {
                //last element do not append comma
                if (i == checkboxLayout.getChildCount()-1) {
                    value.append(cb.getText().toString());
                } else {
                    value.append(cb.getText().toString()).append(",");
                }
            }
        }
        return value.toString();
    }

    public ArrayAdapter<String> getSpinnerData(final AdapterPosition mPosition, Context context, Spinner spinner, List<String> list) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, list);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mPosition.onPosition(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        return adapter;
    }

    public interface AdapterPosition {
        void onPosition(int position);
    }

    //===============================================| Google Maps
    //https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
    public String getAddress(Context context, LatLng latLng) {
        String address = null;
        Geocoder geocoder = new Geocoder(context, Locale.ENGLISH); //Locale.ENGLISH | Locale.getDefault()
        try {
            List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                address = addressList.get(0).getAddressLine(0);
                String locality = addressList.get(0).getLocality(); //village
                String postalCode = addressList.get(0).getPostalCode();
                String countryName = addressList.get(0).getCountryName();
                String adminArea = addressList.get(0).getAdminArea(); // state
                String subAdminArea = addressList.get(0).getSubAdminArea(); //district
                String subLocality = addressList.get(0).getSubLocality();
                Log.d(TAG, "locality: " + locality +" postalCode: "+ postalCode +" countryName: "+ countryName +" adminArea: "+ adminArea +" subAdminArea: "+ subAdminArea +" subLocality: "+ subLocality);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    //-----------------------------------------------| Pass a JSON style object to your map
    public void mapsStyle(Context context, GoogleMap googleMap, int res) {
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, res));
            if (!success) {
                //Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            //Log.e(TAG, "Can't find style. Error: ", e);
        }
    }

    //-----------------------------------------------| Add Marker
    public Marker addMarkerPosition (GoogleMap mMap, Marker originMarker, double latitude, double longitude, String locality, Drawable drawable){
        if (originMarker != null) {
            originMarker.remove();
        }
        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(latitude,longitude));
        options.draggable(true);
        options.title(locality);
        //options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        //options.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_origin)); //PNG Icon
        options.icon(getMarkerIconFromDrawable(drawable)); //PNG Icon
        return mMap.addMarker(options);
    }

    //-----------------------------------------------| Move Map Camera
    public void goToLocation(GoogleMap mMap, double latitude, double longitude, int zoom) {
        LatLng latLng = new LatLng(latitude,longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom));
    }

    public Marker moveToCurrentLocation(Context context, GoogleMap mMap, Marker currentLocationMarker, LatLng latLng, Drawable drawable) {
        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }
        MarkerOptions options = new MarkerOptions().position(latLng).draggable(true).title(getAddress(context, latLng)).icon(getMarkerIconFromDrawable(drawable));
        currentLocationMarker = mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
        mMap.animateCamera(CameraUpdateFactory.zoomIn()); // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null); // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        return currentLocationMarker;
    }

    public void moveToLocation(GoogleMap mMap, LatLng latLng) {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
        mMap.animateCamera(CameraUpdateFactory.zoomIn()); // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null); // Zoom out to zoom level 10, animating with a duration of 2 seconds.
    }

    //-----------------------------------------------| Marker Icon from XML
    public BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //-----------------------------------------------| Change position of current location icon
    public void changeCurrentLocationIcon(SupportMapFragment mapFragment) {
        if (mapFragment != null) {
            ImageView btnMyLocation = (ImageView) ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            btnMyLocation.setImageResource(R.drawable.selector_my_location);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) btnMyLocation.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            //layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
            btnMyLocation.setLayoutParams(layoutParams);
        }
    }

    //-----------------------------------------------| Get Direction Data
    //https://stackoverflow.com/questions/40455341/how-to-parse-steps-from-the-google-directions-api-to-java-android
    /*public ArrayList<String> getDirectionRawBody(final String json) throws JSONException {
        ArrayList<String> list = new ArrayList<>();
        JSONObject obj = new JSONObject(json);
        JSONArray routes = obj.getJSONArray("routes");
        JSONObject routeObj = routes.getJSONObject(0);
        JSONArray legs = routeObj.getJSONArray("legs");
        JSONObject legObj = legs.getJSONObject(0);
        list.add(legObj.getString("end_address"));
        list.add(legObj.getString("start_address"));
        Log.d(TAG, ""+legObj.getString("end_address") +", "+legObj.getString("start_address"));
        return list;
    }*/

    //===============================================| Check Internet Connection
    public boolean haveNetwork(final Activity activity) {
        boolean haveWifi = false;
        boolean haveMobileData = false;
        ConnectivityManager manager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo[] infos = manager.getAllNetworkInfo();
            for (NetworkInfo info : infos) {
                if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                    if (info.isConnected()) {
                        haveWifi = true;
                    }
                }
                if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (info.isConnected()) {
                        haveMobileData = true;
                    }
                }
            }
        }
        return haveWifi | haveMobileData;
    }

    //-----------------------------------------------| Check Location Connection
    public boolean isEnabledLocation(final Activity activity) {
        boolean gpsEnabled = false;
        boolean networkEnabled = false;
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            try {
                gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                networkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*if(!gpsEnabled && !networkEnabled) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(myIntent);
            }*/
        }
        return gpsEnabled && networkEnabled;
    }

    //===============================================| Alert Dialog
    public void alertDialog(final Context context, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.alert_title)
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public AlertDialog.Builder deleteDialog(final Context context) {
        return new AlertDialog.Builder(context).setTitle("Are your sure?").setMessage("Do you want to delete it?").setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    //====================================================| Close/hide the Android Soft Keyboard
    public void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //===============================================| Encryption and Decryption
    //encode normal string to base64 format
    public String encode(String input) {
        try {
            byte[] data = input.getBytes("UTF-8");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(data);
            } else {
                return android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Decode base64 string to normal string
    public String decode(String base64) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                byte[] decodedBytes = Base64.getDecoder().decode(base64);
                return new String(decodedBytes, "UTF-8");
            } else {
                byte[] decodedBytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
                return new String(decodedBytes, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    //====================================================| For Image
    public String bitmapToBase64(ImageView imageView) {
        String encode = null;
        if (imageView.getVisibility()== View.VISIBLE) {
            Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            //String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap,"Title",null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                encode = Base64.getEncoder().encodeToString(stream.toByteArray());
            } else {
                encode = android.util.Base64.encodeToString(stream.toByteArray(), android.util.Base64.DEFAULT);
            }
        }
        return encode;
    }

    //https://github.com/elye/demo_android_base64_image/tree/master/app/src/main/java/com/elyeproj/base64imageload
    public Bitmap base64ToBitmap(String encode) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            byte[] decodedBytes = Base64.getDecoder().decode(encode);
            bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        } else {
            byte[] decodedString = android.util.Base64.decode(encode, android.util.Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        return bitmap;
    }

    public String saveToInternalStorage(Context context, Bitmap bitmapImage, String imageName){
        File directory = new File(context.getFilesDir() + "/AppPhoto/");
        directory.mkdir(); //Create imageDir
        File file = new File(directory, imageName);
        try {
            OutputStream output = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, output); // Compress into png format image from 0% - 100%
            output.flush();
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directory.getAbsolutePath();
    }

    public Bitmap loadImage(String imagePath, String imageName){
        Bitmap bitmap = null;
        try {
            File file = new File(imagePath, imageName);
            bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //===============================================| Handling large Bitmap
    public Bitmap getDownBitmap(Context ctx, Uri uri, int targetWidth, int targetHeight) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options outDimens = getBitmapDimensions(ctx, uri);
            int sampleSize = calculateSampleSize(outDimens.outWidth, outDimens.outHeight, targetWidth, targetHeight);
            bitmap = downBitmap(ctx, uri, sampleSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private BitmapFactory.Options getBitmapDimensions(Context ctx, Uri uri) throws IOException {
        BitmapFactory.Options outDimens = new BitmapFactory.Options();
        outDimens.inJustDecodeBounds = true; // the decoder will return null (no bitmap)

        InputStream is = ctx.getContentResolver().openInputStream(uri);
        // if Options requested only the size will be returned
        BitmapFactory.decodeStream(is, null, outDimens);
        is.close();
        return outDimens;
    }

    private int calculateSampleSize(int width, int height, int targetWidth, int targetHeight) {
        int inSampleSize = 1;
        if (height > targetHeight || width > targetWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) targetHeight);
            final int widthRatio = Math.round((float) width / (float) targetWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    private Bitmap downBitmap(Context ctx, Uri uri, int sampleSize) throws IOException {
        Bitmap resizedBitmap;
        BitmapFactory.Options outBitmap = new BitmapFactory.Options();
        outBitmap.inJustDecodeBounds = false; // the decoder will return a bitmap
        outBitmap.inSampleSize = sampleSize;

        InputStream is = ctx.getContentResolver().openInputStream(uri);
        resizedBitmap = BitmapFactory.decodeStream(is, null, outBitmap);
        is.close();

        return resizedBitmap;
    }

    //-----------------------------------------------| Bitmap to Byte
    public byte[] getBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, stream);
        return stream.toByteArray();
    }

    public Bitmap getBitmapFromByte(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    //===============================================| Customize view shape
    public void setBackgroundDrawable(View view, int solidColor, int stroke, int strokeColor, int topLeft, int topRight, int bottomRight, int bottomLeft) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        //shape.setCornerRadius(100f);
        shape.setCornerRadii(new float[] { topLeft, topLeft, topRight, topRight, bottomRight, bottomRight, bottomLeft, bottomLeft }); //top-left, top-right, bottom-right, bottom-left
        shape.setColor(solidColor);
        shape.setStroke(stroke, strokeColor);
        view.setBackground(shape);
    }

    public void setDrawableTin(Button view, int color) {
        Drawable[] drawables = view.getCompoundDrawables();
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            }
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setCompoundDrawableTintList(context.getResources().getColorStateList(color));
        }*/
    }

    //===============================================| Animation
    public void getAnimationCounter(TextView textView, int count) {
        ValueAnimator animator = new ValueAnimator();
        animator.setObjectValues(0, count);// here you set the range, from 0 to "count" value
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                textView.setText(String.valueOf(animation.getAnimatedValue()));
            }
        });
        animator.setDuration(1000); // here you set the duration of the anim
        animator.start();
    }

    //Oval animation from middle point
    public void layoutAnim(View mMain, View mContent) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int x = (int) (mMain.getX() + (mMain.getWidth() / 2)); //int x = mMain.getRight();
            int y = (int) (mMain.getY() + (mMain.getHeight() / 2)); //int y = mMain.getBottom();

            int startRadius = (mMain.getWidth() / 2); //0
            int endRadius = (int) Math.hypot(mMain.getWidth(), mMain.getHeight());
            Animator mAnim = ViewAnimationUtils.createCircularReveal(mContent, x, y, startRadius, endRadius);
            mContent.setVisibility(View.VISIBLE);
            mAnim.setDuration(500);
            mAnim.start();
        }
    }

    //===============================================| Add fragment | Replace fragment | Remove fragment
    //https://stackoverflow.com/questions/18634207/difference-between-add-replace-and-addtobackstack
    /*public void onAddFragment(FragmentManager mFragmentManager, Fragment mFragment){
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_fade_enter, 0)
                .add(R.id.home_container, mFragment) //.replace(R.id.home_container, mFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss(); //.commit();
        //getSupportFragmentManager().beginTransaction().add(android.R.id.content, new FragmentHome(), "myFragmentTag").addToBackStack("tag").commit(); // without FrameLayout
    }
    public void onReplaceFragment(FragmentManager mFragmentManager, Fragment mFragment){
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_fade_enter, 0)
                .replace(R.id.home_container, mFragment)
                .addToBackStack(null)
                .commit();
    }
    public void onRemoveFragment(FragmentManager mFragmentManager, Fragment mFragment){
        mFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fragment_fade_enter, 0)
                .remove(mFragment)
                .addToBackStack(null)
                .commit();
    }*/

    //===============================================| Create drawable icon with text
    /*public Drawable getCartIconWithText(Context context, String number) {
        LayerDrawable drawable = (LayerDrawable) context.getResources().getDrawable(R.drawable.sample_two_icons);
        //Drawable approvedItem = (Drawable ) drawable.findDrawableByLayerId(R.id.approved_item_shape);
        Drawable text1 = getDrawableText(context, number, null, android.R.color.white, 12);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            drawable.setDrawable(2, text1);
        } else {
            drawable.setDrawableByLayerId(R.id.cart_item_shape, text1);
        }
        Drawable[] layers = {drawable};
        return new LayerDrawable(layers);
    }

    private Drawable getDrawableText(Context context, String text, Typeface typeface, int color, int size) {
        Resources resources = context.getResources();
        Bitmap bitmap = Bitmap.createBitmap(48, 48, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(typeface != null ? typeface : Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(ContextCompat.getColor(context, color));
        float scale = resources.getDisplayMetrics().density;
        paint.setTextSize((int) (size * scale));
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;
        canvas.drawText(text, x, y, paint);
        return new BitmapDrawable(context.getResources(), bitmap);
    }*/

    //===============================================| Set margin by programmatically
    public void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            params.setMargins(left, top, right, bottom);
            //view.requestLayout();
            view.setLayoutParams(params);
        }
    }
}
