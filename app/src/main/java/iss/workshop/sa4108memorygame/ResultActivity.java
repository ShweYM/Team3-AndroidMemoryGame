package iss.workshop.sa4108memorygame;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ResultActivity extends AppCompatActivity {

    private Button start;
    private Button scoreboard;
    private Button submit;
    private String name;
    private int score;

    EditText nameInput;
    TextView playerScore;

    MediaPlayer player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();

        //plays scoreboard audio when displaying player score
        //new SoundPoolPlayer(this).playSoundWithRedId(R.raw.scoreboard);

        player = MediaPlayer.create(this, R.raw.scoreboard);
        player.start();
        player.setLooping(true);

        Intent intent = getIntent();
        final String time = intent.getStringExtra("timer");
        /*if(time != null) {
            Toast.makeText(getApplicationContext(), time, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Nope", Toast.LENGTH_LONG).show();
        }*/
        System.out.println("Time - " + time);

        nameInput = (EditText) findViewById(R.id.nameInput);
        playerScore = (TextView) findViewById(R.id.playerScore);
        playerScore.setText(time);
        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nameInput.length() > 0) {
                    submitScore(time);
                } else {
                    Toast.makeText(getApplicationContext(), "Name is required.", Toast.LENGTH_SHORT).show();
                }

            }
        });


        scoreboard = (Button) findViewById(R.id.scoreboard);
        scoreboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScoreboard();
            }
        });

        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }
        });


    }

//    @Override
//    public void onBackPressed() {
//        Intent intent = new Intent(ResultActivity.this, StartPage.class);
//        startActivity(intent);
//        finish();
//    }

    public void openScoreboard() {
        Intent scoreIntent = new Intent(ResultActivity.this, ScoreActivity.class);
        scoreIntent.putExtra("name", "Team 3");
        startActivity(scoreIntent);
    }

    public void openMainActivity() {
        Intent mainIntent = new Intent(ResultActivity.this, StartPage.class);
        startActivity(mainIntent);
    }

    public void submitScore(String time) {

        String filePath = "ScoreBoard";
        String fileName = "ScoreBoard.txt";
        File mTargetFile = new File(this.getFilesDir(), filePath + "/" + fileName);
        String fileContent = nameInput.getText().toString() + "," + time + "\n";

        try {

            File parent = mTargetFile.getParentFile();

            if (!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create directory: " + parent);
            }
            FileOutputStream fos = new FileOutputStream(mTargetFile, true);
            fos.write(fileContent.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        nameInput.setEnabled(false);
        submit.setClickable(false);
        Intent mainIntent = new Intent(ResultActivity.this, ScoreActivity.class);

        startActivity(mainIntent);


    }

    @Override
    protected void onPause() {
        super.onPause();
        player.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.seekTo(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.release();
    }
}