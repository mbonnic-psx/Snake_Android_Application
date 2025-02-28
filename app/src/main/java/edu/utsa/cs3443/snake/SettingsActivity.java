package edu.utsa.cs3443.snake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * SettingsActivity provides the settings UI where the user can adjust settings such as music playback
 * and apple color for the game. It handles user interactions with settings buttons and stores these preferences.
 */
public class SettingsActivity extends AppCompatActivity {

    private GameActivity gameActivity;

    private MediaPlayer mediaPlayer;
    public SharedPreferences sharedPreferences;

    private boolean isDifficultyEnabled;

    /**
     * onCreate method for SettingsActivity
     *
     * @param savedInstanceState the saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ImageButton musicButton = findViewById(R.id.musicButton);
        ImageButton homeButton = findViewById(R.id.homeButton);
        ImageButton selectButton = findViewById(R.id.characterSelectButton);

        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);

        String[] appleColors = {"red", "blue"};
        final int[] currentColorIndex = {0};

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentColorIndex[0] = (currentColorIndex[0] + 1) % appleColors.length;
                String selectedColor = appleColors[currentColorIndex[0]];

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Apple Color", selectedColor);
                editor.apply();
                Toast.makeText(SettingsActivity.this, "Apple Color is " + selectedColor, Toast.LENGTH_LONG).show();
            }
        });


        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.music);


        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0); //this will reset the music when game over
                } else {
                    restartMusic();
                }
                Toast.makeText(SettingsActivity.this, "Music is on", Toast.LENGTH_LONG).show();
            }

            /**
             *  restartMusic method to restart the music from begginning
             */
            private void restartMusic() {
                if (mediaPlayer != null) {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }
            }
        });


        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, MainMenuActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}
