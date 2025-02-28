package edu.utsa.cs3443.snake;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;

import edu.utsa.cs3443.snake.model.Leaderboard;
import edu.utsa.cs3443.snake.model.Player;

/**
 * Manages the display and functionality of the leaderboard screen for the Snake game.
 * This activity allows users to add new players, update player scores, and view a
 * dynamically sorted leaderboard.
 */
public class LeaderboardActivity extends AppCompatActivity {

    private Leaderboard leaderboard;

    private static final String TAG = "LeaderboardActivity";

    private EditText editTextPlayerName;

    /**
     * onCreate method for LeaderboardActivity
     *
     * @param savedInstanceState the saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityleaderboard);

        leaderboard = new Leaderboard(this);
        leaderboard.initializeLeaderboard();

        editTextPlayerName = findViewById(R.id.et_player_name);

        ImageButton buttonAddPlayer = findViewById(R.id.bt_add_player);
        Button buttonMainMenu = findViewById(R.id.main_menu);

        dynamicSetup();

        buttonAddPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlayer();
                Toast.makeText(LeaderboardActivity.this, "Added Player", Toast.LENGTH_LONG).show();
                dynamicSetup();

            }
        });

        buttonMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LeaderboardActivity.this, MainMenuActivity.class);
                startActivity(intent);
            }
        });


    }

    /**
     * addPlayer method to leaderboard
     */
    private void addPlayer() {
        String name = editTextPlayerName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
            return;
        }
        int score = getIntent().getIntExtra("SCORE", 0);

        // Check if the player already exists
        for (Player player : leaderboard.getPlayers()) {
            if (player.getName().equalsIgnoreCase(name)) {
                Toast.makeText(this, "Player name already exists. Choose a different name.", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Add the new player if the name doesn't exist
        leaderboard.addPlayer(new Player(name, score));
        leaderboard.saveLeaderboard();
        Toast.makeText(this, "Player added successfully!", Toast.LENGTH_SHORT).show();
    }

    /**
     * dynamicSetup method to display leaderboard
     */
    private void dynamicSetup() {

        ArrayList<Player> players = leaderboard.getPlayers();

        if (players == null)
            return;

        ArrayList<Player> sortedPlayers = new ArrayList<>();


        for (Player player : players) {
            int i = 0;
            while (i < sortedPlayers.size() && sortedPlayers.get(i).getScore() > player.getScore()) {
                i++;
            }
            sortedPlayers.add(i, player);
        }


        LinearLayout verticalLayout = findViewById(R.id.ll_holder);
        verticalLayout.removeAllViews();
        int count = 1;
        for (Player player : sortedPlayers) {

            LinearLayout horizontalLayout = new LinearLayout(this);

            horizontalLayout.setOrientation(LinearLayout.HORIZONTAL);
            horizontalLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            horizontalLayout.setPadding(10, 10, 10, 10);
            horizontalLayout.setGravity(Gravity.CENTER);


            // Creating and adding a TextView to the horizontal LinearLayout
            TextView textView = new TextView(this);
            textView.setText(count + ".           " + player.getName() + "          " + player.getScore());
            count += 1;
            textView.setTextSize(18);
            textView.setTextColor(Color.WHITE);
            Typeface typeface = ResourcesCompat.getFont(this, R.font.gameboy);
            textView.setTypeface(typeface);
            textView.setPadding(10, 10, 10, 10);
            textView.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1 // Weight for equal distribution
            ));
            horizontalLayout.addView(textView);

            // Adding the horizontal LinearLayout to the vertical LinearLayout
            verticalLayout.addView(horizontalLayout);
        }
    }
}