package com.subra.taskman.services;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.subra.taskman.utils.ConstantKey;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordForegroundService extends Service {

    private static final String TAG = "ForegroundService";
    private boolean isStarted;
    private MediaRecorder mRecorder;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            if (intent.getExtras().containsKey(ConstantKey.RECORDING)) {
                final String value = intent.getStringExtra(ConstantKey.RECORDING);
                startRecording();
                Toast.makeText(this, "Start Recording", Toast.LENGTH_SHORT).show();
            }
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRecording();
    }

    private void startRecording() {
        try {
            String path = getApplicationContext().getFilesDir().getPath();
            File file = new File(path);
            String mRecordName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            mRecorder = new MediaRecorder();
                    /*mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    mRecorder.setAudioChannels(1);
                    mRecorder.setAudioSamplingRate(8000);
                    mRecorder.setAudioEncodingBitRate(44100);
                    mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);*/

            mRecorder.reset();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            if (!file.exists()){
                file.mkdirs();
            }
            String mRecordFilePath = file+"/" + "REC_" + mRecordName + ".3gp";
            mRecorder.setOutputFile(mRecordFilePath);

            mRecorder.prepare();
            mRecorder.start();
            isStarted = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (isStarted && mRecorder != null) {
            mRecorder.stop();
            mRecorder.reset(); // You can reuse the object by going back to setAudioSource() step
            mRecorder.release();
            mRecorder = null;
            isStarted = false;
        }
    }


}