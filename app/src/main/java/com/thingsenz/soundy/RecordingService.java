package com.thingsenz.soundy;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.github.squti.androidwaverecorder.WaveConfig;
import com.github.squti.androidwaverecorder.WaveConfigKt;
import com.github.squti.androidwaverecorder.WaveRecorder;
import com.github.windsekirun.naraeaudiorecorder.NaraeAudioRecorder;
import com.github.windsekirun.naraeaudiorecorder.config.AudioRecordConfig;
import com.github.windsekirun.naraeaudiorecorder.config.AudioRecorderConfig;
import com.github.windsekirun.naraeaudiorecorder.constants.AudioConstants;
import com.github.windsekirun.naraeaudiorecorder.source.NoiseAudioSource;
import com.thingsenz.soundy.activities.MainActivity;
import com.thingsenz.soundy.fragments.RecordFragment;
import com.thingsenz.soundy.ui.RecorderVisualizerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RecordingService extends Service {

    public static final String PAUSE_REC="com.thingsenz.soundy.PAUSE_REC";
    public static final String RESUME_REC="com.thingsenz.sound.RESUME_REC";

    private static final String LOG_TAG = "RecordingService";
    private static final String AUDIO_REC__WAV_FILE_EXT=".wav";
    private static final String NOTIFICATION_CHANNEL="soundy_channel";
    private static final byte RECORDER_BPP=16;
    private static final String AUDIO_REC_TEMP_FILE="record_temp.raw";
    private static final int RECORDER_SAMPLERATE=44100;
    private static final int RECORDER_CHANNELS= AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENC=AudioFormat.ENCODING_PCM_16BIT;

    private String mFileName = null;
    private String mFilePath = null;
    private boolean isRec=false;

    private NotificationChannel notificationChannel;

    public static final int REPEAT_INTERVAL = 40;

    private Handler handler = new Handler(); // Handler for updating the

    private RecorderVisualizerView recorderVisualizerView;

    private AudioRecordConfig recordConfig;
    private NotificationManager notificationManager;
    //private NoiseAudioSource noiseAudioSource=new NoiseAudioSource(recordConfig);
    private NaraeAudioRecorder audioRecorder;

    String id="Soundy";

    private MediaRecorder mRecorder = null;

    private BroadcastReceiver pauseReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mRecorder!=null&&intent.getBooleanExtra("MP3",true)) {
                mRecorder.pause();
            } else if (waveRecorder!=null) {
                waveRecorder.pauseRecording();
            }
            totalMillis+=System.currentTimeMillis()-mStartingTimeMillis;
        }
    };

    private BroadcastReceiver resumeReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mRecorder!=null&&intent.getBooleanExtra("MP3",true))
                mRecorder.resume();
            else if (waveRecorder!=null)
                waveRecorder.resumeRecording();
            mStartingTimeMillis=System.currentTimeMillis();

        }
    };


    private DBHelper mDatabase;

    private long mStartingTimeMillis = 0,totalMillis=0;
    private WaveRecorder waveRecorder;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private boolean wavFormat=false;
    private OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public interface OnTimerChangedListener {
        void onTimerChanged(int seconds);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mDatabase = new DBHelper(getApplicationContext());
        IntentFilter i1=new IntentFilter(),i2=new IntentFilter();
        i1.addAction(PAUSE_REC);
        getApplicationContext().registerReceiver(pauseReceiver,i1);
        i2.addAction(RESUME_REC);
        getApplicationContext().registerReceiver(resumeReceiver,i2);
        wavFormat=MySharedPreferences.getPrefWavFormat(getApplicationContext());
        audioRecorder=new NaraeAudioRecorder();
        recordConfig=new AudioRecordConfig(MediaRecorder.AudioSource.MIC,AudioFormat.ENCODING_PCM_16BIT,AudioFormat.CHANNEL_IN_MONO, AudioConstants.FREQUENCY_44100);


    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!wavFormat) startRecording();
        else {
            setFileNameAndPath();
            mStartingTimeMillis=System.currentTimeMillis();
            audioRecorder.create(config -> {
                config.setDestFile(new File(mFilePath));
                config.setDebugMode(true);
                config.setRecordConfig(recordConfig);
                config.setTimerCountListener((currentTime, maxTime) -> {
                });


                return null;
            });



            audioRecorder.startRecording(getApplicationContext());
            //waveRecorder=new WaveRecorder(mFilePath);
            //waveRecorder.setNoiseSuppressorActive(true);
            /*WaveConfig waveConfig=new WaveConfig();
            waveConfig.setAudioEncoding(AudioFormat.ENCODING_PCM_16BIT);
            waveConfig.setChannels(AudioFormat.CHANNEL_IN_MONO);
            waveConfig.setSampleRate(RECORDER_SAMPLERATE);
            waveRecorder.setWaveConfig(waveConfig);*/
            //waveRecorder.startRecording();
            /*wavRecorderCustom=new WAVRecorder(mFilePath,getApplicationContext());
            wavRecorderCustom.startRecording();*/
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (mRecorder != null&&!wavFormat) {
            stopRecording();
        }
        else if (wavFormat&&audioRecorder!=null) {
            //wavRecorderCustom.stopRecording(getApplicationContext(), (System.currentTimeMillis() - mStartingTimeMillis));//stopWAVRecording();
            //waveRecorder.startRecording();
            audioRecorder.stopRecording();
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
            Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();

            //remove notification
            if (mIncrementTimerTask != null) {
                mIncrementTimerTask.cancel();
                mIncrementTimerTask = null;
            }






            try {
                mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

            } catch (Exception e){
                Log.e(LOG_TAG, "exception", e);
            }
        }
        super.onDestroy();
    }


    public void startRecording() {

        setFileNameAndPath();

        mRecorder=new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFile(mFilePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mRecorder.setAudioChannels(1);
        if (MySharedPreferences.getPrefHighQuality(this)) {
            mRecorder.setAudioSamplingRate(44100);
            mRecorder.setAudioEncodingBitRate(192000);
        }

        try {
            mRecorder.prepare();
            mRecorder.start();
            createNotification();
            isRec=true;

            mStartingTimeMillis=System.currentTimeMillis();

            handler.post(updateVisualizer);
        } catch (IOException e) {
            Log.e(LOG_TAG,"prepare failed");
        }

    }




    /*private void startWAVRecording() {

        setFileNameAndPath();

        wavRecorder=new AudioRecord(MediaRecorder.AudioSource.MIC,RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENC,bufferSize);
        int i=wavRecorder.getState();
        if (i==1)
            wavRecorder.startRecording();
        isRec=true;
        mStartingTimeMillis=System.currentTimeMillis();
        recordingThread=new Thread(new Runnable() {
            @Override
            public void run() {
                writeWAVDataToFile();
            }
        },"WAVRecorder Thread");
        recordingThread.start();

    }


    private void writeWAVDataToFile() {
        byte data[]=new byte[bufferSize];
        String filename=getTempFileName();
        FileOutputStream os=null;

        try {
            os=new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            Log.d(LOG_TAG,"Fileoutputstrea, temp failed");
            e.printStackTrace();
        }
        int read=0;
        if (os!=null) {

            while (isRec) {
                Log.d(LOG_TAG,"Inside Loop");

            read = wavRecorder.read(data, 0, bufferSize);
            if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                try {
                    os.write(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void stopWAVRecording() {


        if(wavRecorder!=null) {
            isRec=false;
            int i=wavRecorder.getState();
            if (i==1)
                wavRecorder.stop();
            wavRecorder.release();
            wavRecorder=null;
            recordingThread=null;


        }

        copyWAVFile(getTempFileName(),mFilePath);
        deleteTempFile();

        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();

        //remove notification
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }





        try {
            mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

        } catch (Exception e){
            Log.e(LOG_TAG, "exception", e);
        }

    }

    private void deleteTempFile() {
        File file=new File(getTempFileName());
        file.delete();
    }

    private void copyWAVFile(String inFileName,String outFileName) {
        FileInputStream in=null;
        FileOutputStream out=null;

        long totalAudioLen=0;
        long totalDataLen=totalAudioLen+36;
        long longSampleRate=RECORDER_SAMPLERATE;
        int channels=1;
        long byteRate=RECORDER_BPP*RECORDER_SAMPLERATE*channels/8;

        byte[] data=new byte[bufferSize];

        try {
            in=new FileInputStream(inFileName);
            out=new FileOutputStream(outFileName);
            totalAudioLen=in.getChannel().size();
            totalDataLen=totalAudioLen+36;

            Log.d(LOG_TAG,"WAVSize: "+totalDataLen);
            writeWAVFileHeader(out,totalAudioLen,totalDataLen,longSampleRate,channels,byteRate);
            while (in.read(data)!=-1)
                out.write(data);
            in.close();
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }

    }


    private void writeWAVFileHeader(FileOutputStream out, long totalAudioLen,
                                    long totalDataLen, long longSampleRate, int channels,
                                    long byteRate) throws IOException {
        byte[] header=new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = RECORDER_BPP; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header,0,44);
    }
*/


    public void setFileNameAndPath() {

       // int count=0;  (mDatabase.getCount()+count)
        File f;

        //do {
            //count++;
            String fileTimestamp=new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss",Locale.getDefault()).format(new Date());
            if (!wavFormat)
                mFileName=getString(R.string.default_file_name)+"_"+fileTimestamp+".mp3";
            else
                mFileName="MyRecording"+"_"+fileTimestamp+AUDIO_REC__WAV_FILE_EXT;
            mFilePath= Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath+="/Soundy/"+mFileName;
            f=new File(mFilePath);
       // } while (f.exists()&&!f.isDirectory());


    }


    public void stopRecording() {
        mRecorder.stop();
        removeNotification();
        if (totalMillis==0) {
            mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        }
        else
            mElapsedMillis=(System.currentTimeMillis()-mStartingTimeMillis)+totalMillis;
        mRecorder.release();
        Toast.makeText(this, getString(R.string.toast_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();

        handler.removeCallbacks(updateVisualizer);
        //remove notification

        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;
        isRec=false;



        try {
            mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

        } catch (Exception e){
            Log.e(LOG_TAG, "exception", e);
        }
    }

    Runnable updateVisualizer=new Runnable() {
        @Override
        public void run() {
            if (isRec) {
                Intent intent=new Intent(RecordFragment.VIZ_ACTION);
                intent.putExtra("AMP",mRecorder.getMaxAmplitude());
                sendBroadcast(intent);

                handler.postDelayed(this,REPEAT_INTERVAL);

            }
        }
    };


    private void createNotification()
    {
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Recording...")
                .setUsesChronometer(true)
                .setContentIntent(PendingIntent.getActivity(this,10,new Intent(this, MainActivity.class),0))
                //.setChronometerCountDown(false)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_HIGH);
//        builder.setChronometerCountDown(false);
        createNotificationChannel();

        notificationManager=getSystemService(NotificationManager.class);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
            notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(0,builder.build());


    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            CharSequence name;
            notificationChannel=new NotificationChannel(NOTIFICATION_CHANNEL,"soundy", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Soundy");
        }
    }

    private void removeNotification() {
        notificationManager.cancel(0);
    }







}
