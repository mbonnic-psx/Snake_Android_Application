package edu.utsa.cs3443.snake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import edu.utsa.cs3443.snake.model.Leaderboard;

/**
 * MainMenuActivity is the activity that handles the main menu screen of the Snake game.
 * It provides options to start the game, view the settings, and check the leaderboard.
 */
public class MainMenuActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private boolean isDifficultyEnabled;
    private int snakeAcceleration;

    /**
     * onCreate method for MainMenuActivity
     *
     * @param savedInstanceState the saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        isDifficultyEnabled = sharedPreferences.getBoolean("difficultyEnabled", false);
        snakeAcceleration = isDifficultyEnabled ? 2 : 800;

        // Initialize buttons
        ImageButton start = findViewById(R.id.snake);
        ImageButton settings = findViewById(R.id.settings);
        ImageButton leaderboard = findViewById(R.id.leaderboard);

        // Set button listeners
        start.setOnClickListener(view -> launchGame());
        settings.setOnClickListener(view -> launchSettings());
        leaderboard.setOnClickListener(view -> launchLeaderboard());
    }

    /**
     * launchGame method to launch GameActivity
     */
    private void launchGame() {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("acceleration", snakeAcceleration);
        startActivity(intent);
    }

    /**
     * launchSettings method to launch SettingsActivity
     */
    private void launchSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * launchLeaderboard method to launch LeaderboardActivity
     */
    private void launchLeaderboard() {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }
}
