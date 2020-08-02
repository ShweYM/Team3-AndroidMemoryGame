package iss.workshop.sa4108memorygame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class StartPage extends AppCompatActivity {
    private ImageButton btn_start;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startpage);
        new SoundPoolPlayer(this).playSoundWithRedId(R.raw.gamebackground);
        btn_start = (ImageButton) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMainActivity();
            }

        });

        imageView = findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.hide();
    }

    public void openMainActivity() {

        Intent mainIntent = new Intent(StartPage.this, MainActivity.class);
        startActivity(mainIntent);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundPoolPlayer.release();
    }

}
