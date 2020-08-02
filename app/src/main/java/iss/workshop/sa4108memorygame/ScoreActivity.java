package iss.workshop.sa4108memorygame;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ScoreActivity extends AppCompatActivity {

    TextView rank, name, time;
    private int count=1;
    ArrayList<User> users = new ArrayList<User>();
    MediaPlayer player3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        //continues to play scoreboard audio
        //new SoundPoolPlayer(this).playSoundWithRedId(R.raw.scoreboard);
        Intent intent = getIntent();
        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();

        player3 = MediaPlayer.create(this, R.raw.scoreboard);
        player3.start();
        player3.setLooping(true);

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mainIntent = new Intent(ScoreActivity.this, StartPage.class);
                startActivity(mainIntent);
            }
        });

        /*TextView playerName = (TextView) findViewById(R.id.playerName);
        TextView playerScore = (TextView) findViewById(R.id.playerScore);
        float score = intent.getFloatExtra("score", 10.0f);*/

        String filePath = "ScoreBoard";
        String fileName = "ScoreBoard.txt";

        File mTargetFile = new File(this.getFilesDir(), filePath + "/" +fileName);

     /*   try{
            File parent = mTargetFile.getParentFile();
            if(!parent.exists() && !parent.mkdirs()) {
                throw new IllegalStateException("Couldn't create directory: " + parent);
            }
            FileOutputStream fos = new FileOutputStream(mTargetFile);
            String fileContent = "Arjun,00:50\n";
            fos.write(fileContent.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

       TableLayout table = (TableLayout) findViewById(R.id.table);
       try {
            FileInputStream fis = new FileInputStream(mTargetFile);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] tokens = strLine.split(",");
                String[] time = tokens[1].split(":");
                users.add(new User(tokens[0], Integer.valueOf(time[0])*60 + Integer.valueOf(time[1])));
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e){
           e.printStackTrace();
        }

       // Sorting the users in the list based on the time in seconds
        Collections.sort(users, new Comparator<User>() {
            @Override
            public int compare(User user, User t1) {
                return user.getTime()-t1.getTime() != 0 ? user.getTime()-t1.getTime():user.getName().compareTo(t1.getName());
            }
        });
        // Displaying the list of users in the activity_score
        for(User user : users) {
            System.out.println(user.toString());
            TableRow tableRow = new TableRow(this);
            tableRow.setShowDividers(LinearLayout.SHOW_DIVIDER_END);
            rank = new TextView(this);
            rank.setText(String.valueOf(count));
            rank.setTextSize(20);
            TableRow.LayoutParams parameters1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 0.1f);
            rank.setLayoutParams(parameters1);
            rank.setGravity(Gravity.CENTER);
            name = new TextView(this);
            name.setText(user.getName());
            name.setTextSize(20);
            TableRow.LayoutParams parameters2 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f);
            name.setLayoutParams(parameters2);
            name.setGravity(Gravity.CENTER);
            time = new TextView(this);
            time.setText(String.valueOf(user.getTime()/60)+"' "+String.valueOf(user.getTime()%60));
            time.setTextSize(20);
            time.setLayoutParams(parameters2);
            time.setGravity(Gravity.CENTER);
            tableRow.addView(rank);
            tableRow.addView(name);
            tableRow.addView(time);
            table.addView(tableRow);
            count++;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        player3.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player3.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        player3.seekTo(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player3.release();
    }
}