package edu.utsa.cs3443.snake;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import edu.utsa.cs3443.snake.model.SnakePoints;

/**
 * GameActivity is the main activity for the Snake game. It manages the game's UI,
 * including drawing the snake, handling user input, updating the score, and managing game state.
 */
public class GameActivity extends AppCompatActivity implements SurfaceHolder.Callback {

    private final List<SnakePoints> snakePointsList = new ArrayList<>();

    private SurfaceView surfaceView;
    private TextView scoreTV;

    //surface holder to draw snake on surfaces canvas
    private SurfaceHolder surfaceHolder;

    //by default snake moves to the right
    private String movingPosition = "right";

    //score
    private int score = 0;

    //snake size
    private static final int pointSize = 56;

    //default snake tale
    private static final int defaultTalePoints = 3;

    //snake color
    private static final int snakeColor = Color.BLACK;

    //snake movement speed
    private static final int snakeMovingSpeed = 800;

    //random point position cordinates on the surfaceView
    private int positionX, positionY;

    //timer to move snake
    private Timer timer;

    //canvas to draw the snake
    private Canvas canvas = null;

    //point color / single point color of a snake
    private Paint pointColor = null;

    private TextView highestScoreTV;

    private Bitmap appleBitmap;
    private Bitmap snakeSpriteSheet;

    private Bitmap headSprite;
    private Bitmap bodySprite;
    private Bitmap tailSprite;

    private MediaPlayer mediaPlayer;
    private boolean isMusicEnabled;

    /**
     * Constructor for GameActivity
     *
     * @param savedInstanceState the saved instance state of the activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_game);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences sharedPreferences = getSharedPreferences("GameSettings", MODE_PRIVATE);
        isMusicEnabled = sharedPreferences.getBoolean("musicEnabled", true);

        mediaPlayer = MediaPlayer.create(this, R.raw.music);

        if (isMusicEnabled) {
            mediaPlayer.setLooping(true); //if game music is on this will loop it
            mediaPlayer.start();
        }

        int acceleration = getIntent().getIntExtra("acceleration", 3200);

        //getting surfaceview and score TextView from our view layout
        surfaceView = findViewById(R.id.surfaceView);
        scoreTV = findViewById(R.id.scoreTV);
        highestScoreTV = findViewById(R.id.highestScoreTV);

        //load apple image
        appleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.apple_red);
        snakeSpriteSheet = BitmapFactory.decodeResource(getResources(), R.drawable.snake_green_spritesheet);

        int newWidth = (int) (appleBitmap.getWidth() * 1.5); // Scale width by 1.5
        int newHeight = (int) (appleBitmap.getHeight() * 1.5); // Scale height by 1.5
        appleBitmap = Bitmap.createScaledBitmap(appleBitmap, newWidth, newHeight, true);

        headSprite = getSprite(snakeSpriteSheet, 0, 0, 40, 80, 4);
        headSprite = Bitmap.createScaledBitmap(headSprite, pointSize, pointSize, true);

        bodySprite = getSprite(snakeSpriteSheet, 0, 32, 40, 60, 4);
        bodySprite = Bitmap.createScaledBitmap(bodySprite, pointSize, pointSize, true);

        tailSprite = getSprite(snakeSpriteSheet, 0, 32, 40, 80, 4);
        tailSprite = Bitmap.createScaledBitmap(tailSprite, pointSize, pointSize, true);

        final AppCompatImageButton topBtn = findViewById(R.id.topBtn);
        final AppCompatImageButton leftBtn = findViewById(R.id.leftBtn);
        final AppCompatImageButton rightBtn = findViewById(R.id.rightBtn);
        final AppCompatImageButton bottomBtn = findViewById(R.id.bottomBtn);

        SharedPreferences sharedPreferences2 = getSharedPreferences("GameSettings", MODE_PRIVATE);
        String appleColor = sharedPreferences2.getString("Apple Color", "red");

        ImageView appleImageView = findViewById(R.id.appleImageView);
        switch (appleColor) {
            case "red":
                appleImageView.setImageResource(R.drawable.apple_red);
                break;
            case "blue":
                appleImageView.setImageResource(R.drawable.apple_blue);
                break;
            default:
                appleImageView.setImageResource(R.drawable.apple_red);
                break;
        }


        surfaceView.getHolder().addCallback(this);

        topBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!movingPosition.equals("bottom")) {
                    movingPosition = "top";
                    rotateBitmap(headSprite, 0);

                }
            }
        });

        leftBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!movingPosition.equals("right")) {
                    movingPosition = "left";
                    rotateBitmap(headSprite, 270);
                }
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!movingPosition.equals("left")) {
                    movingPosition = "right";
                    rotateBitmap(headSprite, 90);  // No rotation for right
                }
            }
        });

        bottomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!movingPosition.equals("top")) {
                    movingPosition = "bottom";
                    rotateBitmap(headSprite, 180);
                }
            }
        });

        displayHighestScore();
    }


    /**
     * surfaceCreated method Initilizes the game loop
     *
     * @param surfaceHolder surface holder
     */
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

        this.surfaceHolder = surfaceHolder;

        init();
    }

    /**
     * surfaceChanged method sets screen orientation
     *
     * @param surfaceHolder surface holder
     * @param i             format of the surface
     * @param i1            width of the surface
     * @param i2            height of the surface
     */
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    /**
     * surface destroyer method
     *
     * @param surfaceHolder surface holder to destroy or clean of assets
     */
    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

    }

    /**
     * onDestroy method to destroy the media player when the activity is destroyed
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * init method
     */
    private void init() {
        //clear snake points and length
        snakePointsList.clear();

        //set score to zero
        scoreTV.setText("0");

        //make score 0
        score = 0;

        //setting default moving position
        movingPosition = "right";

        //default snake starting position on the screen
        int startPositionX = (pointSize) * defaultTalePoints;

        //making snakes default length and points
        for (int i = 0; i < defaultTalePoints; i++) {
            //adding points to snakes tale
            SnakePoints snakePoints = new SnakePoints(startPositionX, pointSize);
            snakePointsList.add(snakePoints);

            //increasing value for the next point as snakes tale
            startPositionX = startPositionX - (pointSize * 2);
        }

        addPoint();

        moveSnake();
    }

    /**
     * addPoint method adds a new apple for the snake to get
     */
    private void addPoint() {
        int surfaceWidth = surfaceView.getWidth() - (pointSize * 2);
        int surfaceHeight = surfaceView.getHeight() - (pointSize * 2);

        int randomXPosition = new Random().nextInt(surfaceWidth / pointSize);
        int randomYPosition = new Random().nextInt(surfaceHeight / pointSize);

        if ((randomXPosition % 2) != 0) {
            randomXPosition = randomXPosition + 1;
        }

        if ((randomYPosition % 2) != 0) {
            randomYPosition = randomYPosition + 1;
        }

        positionX = (pointSize * randomXPosition) + pointSize;
        positionY = (pointSize * randomYPosition) + pointSize;
    }

    /**
     * saveData method
     *
     * @param activity   activity where the score can be saved
     * @param playerName the player name
     * @param score      the score to save
     */
    public void saveData(GameActivity activity, String playerName, int score) {
        try {
            String filename = "scoreboard.csv";

            // Open the file output stream in append mode so that new scores are added
            OutputStream out = activity.openFileOutput(filename, Context.MODE_APPEND);

            // Format the score as a CSV line
            String csvLine = playerName + "," + score + "\n";

            // Write the score data to the file
            out.write(csvLine.getBytes(StandardCharsets.UTF_8));

            // Close the output stream
            out.close();
        } catch (IOException e) {
            System.out.println("Failed to write data");
            e.printStackTrace();
        }
    }

    /**
     * displayHighestScore method
     */
    public void displayHighestScore() {
        int highestScore = 0;

        try {
            // Open the file in read mode
            InputStream inputStream = openFileInput("scoreboard.csv");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    int score = Integer.parseInt(parts[1].trim());
                    if (score > highestScore) {
                        highestScore = score;
                    }
                }
            }

            // Close the file stream
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Update the TextView with the highest score
        highestScoreTV.setText("Highest Score: " + highestScore);
    }

    /**
     * updateHighestScoreDisplay method
     */
    public void updateHighestScoreDisplay() {
        runOnUiThread(() -> displayHighestScore());
    }

    /**
     * moveSnake method
     */
    private void moveSnake() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                //getting head position
                int headPositionX = snakePointsList.get(0).getPositionX();
                int headPositionY = snakePointsList.get(0).getPositionY();

                //check if snake eaten a point
                if (headPositionX == positionX && positionY == headPositionY) {
                    //grow snake after eating a point
                    growSnake();

                    //add another random point on the screen
                    addPoint();
                }

                //check of which side snake is moving
                switch (movingPosition) {
                    case "right":

                        //move snake head to right
                        //other points follow snakes head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX + (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        //rotateBitmap(headSprite, 0);  // No rotation for right
                        break;

                    case "left":

                        //move snake head to left
                        //other points follow snakes head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX - (pointSize * 2));
                        snakePointsList.get(0).setPositionY(headPositionY);
                        //rotateBitmap(headSprite, 180);
                        break;

                    case "top":

                        //move snake head to top
                        //other points follow snakes head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY - (pointSize * 2));
                        //rotateBitmap(headSprite, 270);
                        break;

                    case "bottom":

                        //move snake head to bottom
                        //other points follow snakes head point to move the snake
                        snakePointsList.get(0).setPositionX(headPositionX);
                        snakePointsList.get(0).setPositionY(headPositionY + (pointSize * 2));
                        //rotateBitmap(headSprite, 90);
                        break;
                }


                //check if game over, weather snake touch edges or snake itself
                if (checkGameOver(headPositionX, headPositionY)) {
                    //stop timer, stop moving snake
//                    timer.purge();
//                    timer.cancel();

                    if (timer != null) {
                        timer.cancel();
                        timer.purge();
                        timer = null;
                    }

                    if (mediaPlayer != null && isMusicEnabled) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(0);
                        mediaPlayer.start();
                    }


                    saveData(GameActivity.this, "Player", score);
                    updateHighestScoreDisplay();

                    //show game over dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    builder.setMessage("Your Score is " + score);
                    builder.setTitle("Game Over");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Start Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //restart game, re-init data, save player info and update leaderboard

                            init();
                        }
                    });

                    builder.setNegativeButton("Main Menu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            //restart game, re-init data, save player info and update leaderboard
                            finish();
                        }
                    });

                    builder.setNegativeButton("Leaderboard", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(GameActivity.this, LeaderboardActivity.class);
                            intent.putExtra("SCORE", score);
                            startActivity(intent);
                            //restart game, re-init data, save player info and update leaderboard
                        }
                    });


                    //timer runs in background so we need to show dialog on main thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });
                } else {

                    canvas = surfaceHolder.lockCanvas();
                    canvas.drawColor(Color.WHITE, PorterDuff.Mode.CLEAR);

// Draw the head using headSprite
                    canvas.drawBitmap(headSprite,
                            snakePointsList.get(0).getPositionX() - headSprite.getWidth() / 2,
                            snakePointsList.get(0).getPositionY() - headSprite.getHeight() / 2,
                            null);

// Draw the apple
                    canvas.drawBitmap(appleBitmap,
                            positionX - (appleBitmap.getWidth() / 2),
                            positionY - (appleBitmap.getHeight() / 2),
                            null);

// Draw the body using bodySprite
                    for (int i = 1; i < snakePointsList.size(); i++) {
                        int getTempPositionX = snakePointsList.get(i).getPositionX();
                        int getTempPositionY = snakePointsList.get(i).getPositionY();

                        // Move points across the head
                        snakePointsList.get(i).setPositionX(headPositionX);
                        snakePointsList.get(i).setPositionY(headPositionY);

                        // Draw body parts using bodySprite
                        canvas.drawBitmap(bodySprite,
                                snakePointsList.get(i).getPositionX() - bodySprite.getWidth() / 2,
                                snakePointsList.get(i).getPositionY() - bodySprite.getHeight() / 2,
                                null);

                        // Update the head position for the next segment
                        headPositionX = getTempPositionX;
                        headPositionY = getTempPositionY;
                    }

// Unlock the canvas and post it
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }, 1000 - snakeMovingSpeed, 1000 - snakeMovingSpeed);
    }

    /**
     * growSnake method
     */
    private void growSnake() {
        //create new snake point
        SnakePoints snakePoints = new SnakePoints(0, 0);

        //add point to the snakes tale
        snakePointsList.add(snakePoints);

        //increase score
        score++;

        //setting score to textViews
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                scoreTV.setText(String.valueOf(score));
            }
        });
    }

    /**
     * checkGameOver method checks if snake touches edges or itself
     *
     * @param headPositionX checks snakes head position on x-coordinate
     * @param headPositionY checks snakes head position on y-coordinate
     * @return gameOver
     */
    private boolean checkGameOver(int headPositionX, int headPositionY) {
        boolean gameOver = false;

        //check if snakes head touches edges
        if (snakePointsList.get(0).getPositionX() < 0 || snakePointsList.get(0).getPositionY() < 0 || snakePointsList.get(0).getPositionX() >= surfaceView.getWidth() || snakePointsList.get(0).getPositionY() >= surfaceView.getHeight()) {
            gameOver = true;
        } else {
            for (int i = 0; i < snakePointsList.size(); i++) {
                if (headPositionX == snakePointsList.get(i).getPositionX() && headPositionY == snakePointsList.get(i).getPositionY()) {
                    gameOver = true;
                    break;
                }

            }
        }

        return gameOver;
    }

    /**
     * createPointColor method establish color of snake
     *
     * @return pointColor
     */
    private Paint createPointColor() {
        //chick if color is not defined
        if (pointColor == null) {
            pointColor = new Paint();
            pointColor.setColor(Color.RED);
            pointColor.setStyle(Paint.Style.FILL);
            pointColor.setAntiAlias(true); //smoothness of the point\

        }

        return pointColor;
    }

    /**
     * getSprite method gets sprite from sprite sheet
     *
     * @param spriteSheet the sprite sheet
     * @param x           x-coordinate of sprite sheet
     * @param y           y- coordinate of sprite sheet
     * @param width       width of sprite
     * @param height      height of sprite
     * @param scaleFactor how much the sprite will scale
     * @return Bitmap.createScaledBitmap(sprite, newWidth, newHeight, true);
     */
    public Bitmap getSprite(Bitmap spriteSheet, int x, int y, int width, int height, int scaleFactor) {
        // Extract the sprite from the sprite sheet
        Bitmap sprite = Bitmap.createBitmap(spriteSheet, x, y, width, height);

        // Scale the sprite to the desired size (based on the scaleFactor)
        int newWidth = width * scaleFactor;
        int newHeight = height * scaleFactor;
        return Bitmap.createScaledBitmap(sprite, newWidth, newHeight, true);
    }

    /**
     * rotateBitmap method rotates bitmap
     *
     * @param source bitmap to rotate
     * @param angle  angle to rotate
     * @return Bitmap.createScaledBitmap(sprite, newWidth, newHeight, true);
     */
    private Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);  // Rotate by the given angle
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}





