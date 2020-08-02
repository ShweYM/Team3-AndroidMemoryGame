package iss.workshop.sa4108memorygame;

import android.media.*;
import android.content.*;
import android.os.*;
import android.app.*;
import android.widget.*;

public class SoundPoolPlayer
{

    private SoundPool mainSoundPool;
    private AudioManager mainAudioManager;
    private float volume;
    // Maximumn sound stream.
    private static final int MAX_STREAMS = 10;
    private static final int streamType = AudioManager.STREAM_MUSIC;
    private int soundId;
    private int resId;
    private Context mainContext;
    public SoundPoolPlayer(Context context){
        this.mainContext=context;
    }

    public void playSoundWithRedId(int resId){
        this.resId=resId;
        init();
    }

    //init settings
    private void init(){

        mainAudioManager = (AudioManager)this.mainContext. getSystemService(Context.AUDIO_SERVICE);
        float currentVolumeIndex = (float) mainAudioManager.getStreamVolume(streamType);
        float maxVolumeIndex  = (float) mainAudioManager.getStreamMaxVolume(streamType);
        this.volume = currentVolumeIndex / maxVolumeIndex;


        ((Activity)this.mainContext).setVolumeControlStream(streamType);

        // For Android SDK >= 21
        if (Build.VERSION.SDK_INT >= 21 ) {

            AudioAttributes audioAttrib = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            SoundPool.Builder builder= new SoundPool.Builder();
            builder.setAudioAttributes(audioAttrib).setMaxStreams(MAX_STREAMS);

            this.mainSoundPool = builder.build();
        }
        // for Android SDK < 21
        else {
            // SoundPool(int maxStreams, int streamType, int srcQuality)
            this.mainSoundPool = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        }

//        public SoundPoolPlayer load(mainContext , resId) {
//            this.soundId=this.mainSoundPool.load(this.mainContext,this.resId,1);
//            // When load complete.
//            this.mainSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
//                @Override
//                public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
//                    playSound();
//                }
//            });
//        }

        //load res
        this.soundId=this.mainSoundPool.load(this.mainContext,this.resId,1);
        // When load complete.
        this.mainSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                playSound();
            }
        });

    }

    //play the sound res
    private void playSound(){
        float leftVolumn = volume;
        float rightVolumn = volume;
        // Play sound of gunfire. Returns the ID of the new stream.
        int streamId = this.mainSoundPool.play(this.soundId,leftVolumn, rightVolumn, 1, 0, 1f);
    }

    public static void release() {

            SoundPoolPlayer.release();}


}

