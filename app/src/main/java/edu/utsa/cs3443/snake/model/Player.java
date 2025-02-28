package edu.utsa.cs3443.snake.model;


/**
 * Represents a player in the Snake game.
 * The Player class stores the player's name, score, and the filename
 * for their associated picture.
 */
public class Player {

    private String name;
    private int score;
    private String pictureFilename;

    /**
     * constructor for Player
     *
     * @param name  the name of the player
     * @param score initial score
     */
    public Player(String name, int score) {
        this.name = name;
        this.score = score;
        this.pictureFilename = name.toLowerCase();
    }

    /**
     * get name
     *
     * @return name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * set name
     *
     * @param name new name of the player
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * get score
     *
     * @return the current score of the player
     */
    public int getScore() {
        return score;
    }

    /**
     * set score
     *
     * @param score new score of the player
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * get pictureFilename
     *
     * @return pictureFilename
     */
    public String getPictureFilename() {
        return pictureFilename;
    }

    /**
     * set pictureFilename
     *
     * @param pictureFilename new pictureFilename
     */
    public void setPictureFilename(String pictureFilename) {
        this.pictureFilename = pictureFilename;
    }
}


