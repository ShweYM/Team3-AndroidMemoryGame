package iss.workshop.sa4108memorygame;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DetailActivity extends AppCompatActivity
        implements AdapterView.OnItemClickListener {

    class DetailTask extends TimerTask {

        @Override
        public void run() {
            DetailActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    long millis = System.currentTimeMillis() - start_time;
                    int seconds = (int) (millis / 1000);
                    int minutes = seconds / 60;
                    //int hours = minutes / 60;
                    seconds = seconds % 60;

                    //timer_txt.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
                    if (minutes != 2) {
                        timer_txt.setText(String.format("%02d:%02d", minutes, seconds));
                    } else {
                        timer.cancel();
                        timer.purge();
                        Toast.makeText(getApplicationContext(), "Time's up", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), StartPage.class);
                        startActivity(intent);
                    }


                }
            });

        }
    }


    Timer timer;
    ImageView currentView = null;
    TextView timer_txt;
    private int countPair = 0;
    boolean timer_start = true;
    long start_time = 0;

    String firstClick = null;
    ImageView firstview = null;
    ImageView secondview = null;

    boolean isBusy = false;
    boolean isFlipped1 = false;
    boolean isFlipped2 = false;
    boolean isMatched = false;
    String countMsg;

    //final HashMap<Integer,Integer> dict = new HashMap<Integer, Integer>();
    List<String> imageList = new ArrayList<>();
    ArrayList<String> pictureList;
    MediaPlayer player,player1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toast.makeText(getApplicationContext(),"Time Limit : 2 minutes",Toast.LENGTH_SHORT).show();

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();

        //getting user selected images from main activity
        Intent intent = getIntent();
        pictureList = intent.getStringArrayListExtra("pictureList");
        System.out.println("pictureList from main activity::: " + pictureList);
        //duplicate the selected images
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < pictureList.size(); j++) {
                imageList.add(pictureList.get(j));
            }
        }

        //Timer start when arrives this activity
        timer_txt = (TextView) findViewById(R.id.timer);
        if (timer_start) {
            start_time = System.currentTimeMillis();
            timer = new Timer();
            timer.schedule(new DetailTask(), 0, 500);
        }

        //for background music
        player1 = MediaPlayer.create(this, R.raw.over_the_rainbow);
        player1.start();
        player1.setLooping(true);

        //shuffle all the images
        Collections.shuffle(imageList);

        GridView gridView = (GridView) findViewById(R.id.gridView2);
        ImageAdapter2 adapter = new ImageAdapter2(this);

        if (gridView != null) {
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(this);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        if (((ImageView) view).isEnabled() == true) {
            if (isBusy) {
                return;
            }
            if (isMatched) {
                return;
            }
            if (firstview == null) {
                firstClick = imageList.get(i);
                firstview = (ImageView) view;
                flip(firstview, i, "first");

                return;
            }

            if (firstClick != imageList.get(i)) {
                secondview = (ImageView) view;
                flip(secondview, i, "second");
                isBusy = true;

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flip(secondview, 0, "second");
                        flip(firstview, 0, "first");
                        firstview = null;
                        secondview = null;
                        isBusy = false;
                    }
                }, 1000);
            } else {
                secondview = (ImageView) view;
                flip(secondview, i, "second");

                //sound when two images match
                player = MediaPlayer.create(this,R.raw.pop_clip_in);
                player.start();

                firstview.setEnabled(false);
                secondview.setEnabled(false);
                //increasing count of matches
                //isMatched = true;
                countPair++;
                countMsg = countPair + "/" + pictureList.size() + " Matches";
                TextView count = findViewById(R.id.NoOfMatches);
                if (count != null) {
                    count.setText(countMsg);
                }
                if (countPair == pictureList.size()) {
                    isMatched = true;

                    timer.cancel();
                    timer.purge();
                    player1.stop();

                    Toast.makeText(getApplicationContext(), "Congratulations!!! You win.", Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), timer_txt.getText().toString(), Toast.LENGTH_LONG).show();
                    //b.setText("start");
                    Intent intent = new Intent(DetailActivity.this, ResultActivity.class);
                    intent.putExtra("timer", timer_txt.getText().toString());
                    startActivity(intent);
                    deleteFilesinGamePhoto(DetailActivity.this);
                }

                firstview = null;
                secondview = null;
                isFlipped1 = false;
                isFlipped2 = false;
                isBusy = false;

            }
        }
    }

    @Override
    public void onBackPressed() {
        timer.cancel();
        Intent intent = new Intent(DetailActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player1.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player1.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player1.seekTo(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player1.release();
    }

    public void flip(ImageView view, int id, String level) {
        if (isMatched) {
            return;
        }
        if (level == "first") {
            if (isFlipped1) {
                view.setImageResource(R.drawable.question);
                isFlipped1 = false;
            } else {
                view.setImageURI(Uri.parse(imageList.get(id)));
                isFlipped1 = true;
            }
        }
        if (level == "second") {
            if (isFlipped2) {
                view.setImageResource(R.drawable.question);
                isFlipped2 = false;
            } else {
                view.setImageURI(Uri.parse(imageList.get(id)));
                isFlipped2 = true;
            }
        }
    }

    protected void deleteFilesinGamePhoto(Context context) {
//        String filePath = "/data/user/0/iss.workshop.sa4108memorygame/files/GamePhoto";
//        String filePath = context.getFilesDir().getPath();
        String filePath = "GamePhoto";
        File file = new File(context.getFilesDir(),filePath);
        if (file.isDirectory()) {
            String[] filearray = file.list();
            System.out.println(Arrays.toString(filearray));
            System.out.println(file);
            for (int i = 0; i < filearray.length; i++) {
                new File(file, filearray[i]).delete();
            }
        }
    }

}
