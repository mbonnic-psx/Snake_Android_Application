package edu.utsa.cs3443.snake.model;


/**
 * Represents a point in the Snake game.
 * The SnakePoints class stores the X and Y coordinates
 * of a point on the game board.
 */
public class SnakePoints {

    private int positionX, positionY;

    /**
     * Constructor for SnakePoints
     *
     * @param positionX the X-coordinate of the point
     * @param positionY the Y-coordinate of the point
     */
    public SnakePoints(int positionX, int positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    /**
     * gets positionX
     *
     * @return positionX gets X-coordinate of the point
     */
    public int getPositionX() {
        return positionX;
    }

    /**
     * sets positionX
     *
     * @param positionX sets X-coordinate of the point
     */
    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    /**
     * gets positionY
     *
     * @return positionY gets Y-coordinate of the point
     */
    public int getPositionY() {
        return positionY;
    }

    /**
     * sets positionY
     *
     * @param positionY sets Y-coordinate of the point
     */
    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }
}
