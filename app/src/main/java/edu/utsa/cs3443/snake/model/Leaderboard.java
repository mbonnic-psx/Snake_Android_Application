package edu.utsa.cs3443.snake.model;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Represents a leaderboard for the Snake game.
 * The Leaderboard class manages player data, including reading from and writing to
 * a file in the emulator's memory. It supports adding players, loading data from a file,
 * and saving the leaderboard data back to a file.
 */
public class Leaderboard {

    /*
    since the assests folder is read-only, we will read
    and write data to the emulator's memory.

    Before you run the application, check the file location in the
    emulator's memory by navigating to
    View Menu > Tool Windows > Device Explorer then
    data > user > 0 > edu.utsa.cs3443.writingtoafile > files

    openFileInput() and openFileOutput() methods
    read and write data under user/data/0/files directory
     */

    private static final String TAG = "Leaderboard";
    private ArrayList<Player> players;
    private final String filename;
    private final Activity activity;

    /**
     * constructor for Leaderboard
     *
     * @param activity the Activity associated with the Leaderboard
     */
    public Leaderboard(Activity activity) {
        this.activity = activity;
        players = new ArrayList<Player>();
        filename = "leaderboard.csv";
    }

    /**
     * initilize leaderboard data from file
     */
    public void initializeLeaderboard() {

        try {
            // if the file does not exist, openFileInput() will throw
            // a FileNotFoundException. The catch block can create the
            // file using openFileOutput()
            Log.i(TAG, "Attempting to read from file ...");
            // manager = activity.getAssets();
            // in
            InputStream in = activity.openFileInput(filename);
            Log.i(TAG, "SUCCESS");
            // if the file exists, read the leaderboard info.
            loadLeaderboard(in);
        } catch (FileNotFoundException e1) {
            Log.e(TAG, "Unable to read from file. File does not exist. " + filename);
            // if the file does not exist, create it
            // openFileOutput() opens a private file associated with this
            // Context's application package for writing.
            // The method creates the file if it doesn't already exist.
            try {
                Log.i(TAG, "Attempting to create a file ...");
                OutputStream out = activity.openFileOutput(filename, Context.MODE_PRIVATE);
                Log.i(TAG, "SUCCESS");
            } catch (FileNotFoundException e2) {
                Log.e(TAG, "Unable to create file. " + filename);
            }
        }
    }

    /**
     * load leaderboard data from file
     *
     * @param in the InputStream to read from
     */
    private void loadLeaderboard(InputStream in) {
        /* Reading from a file in the AVD memory */

        if (in != null) {
            Scanner scan = new Scanner(in);
            String playerInfo = "";
            String[] tokens;
            while (scan.hasNextLine()) {
                playerInfo = scan.nextLine();
                tokens = playerInfo.split(",");
                Log.i(TAG, playerInfo);
                addPlayer(new Player(tokens[0].trim(), Integer.parseInt(tokens[1].trim())));
            }
        }
    }

    /**
     * add player to leaderboard
     *
     * @param player the Player to add to the leaderboard
     */
    public void addPlayer(Player player) {

        if (players == null) {
            players = new ArrayList<Player>();
        }

        if (player != null) {
            Log.i(TAG, "Adding " + player.getName() + " " + player.getScore() + " to the players");
            players.add(player);
        }
    }

    /**
     * save leaderboard data to file
     */
    public void saveLeaderboard() {
        try {
            Log.i(TAG, "Attempting to save to file ... ");
            OutputStream out = activity.openFileOutput(filename, Context.MODE_PRIVATE);
            Log.i(TAG, "SUCCESS");
            if (players != null) {
                for (Player player : players) {
                    out.write(player.getName().getBytes(StandardCharsets.UTF_8));
                    out.write(", ".getBytes(StandardCharsets.UTF_8));
                    out.write(String.valueOf(player.getScore()).getBytes(StandardCharsets.UTF_8));
                    out.write("\n".getBytes(StandardCharsets.UTF_8));
                }
            }
            out.close();
        } catch (IOException e) {
            Log.i(TAG, "Failed to write to file. " + filename);
        }
    }

    /**
     * get players
     *
     * @return players arraylist
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * toString layout
     *
     * @return leaderboard formatted String of all players
     */
    public String toString() {
        String leaderboard = "Leaderboard: \n";
        for (Player player : getPlayers()) {
            leaderboard = leaderboard + "\t player: " + player.getName() + " score: " + player.getScore() + "\n";
        }
        return leaderboard;
    }
}
