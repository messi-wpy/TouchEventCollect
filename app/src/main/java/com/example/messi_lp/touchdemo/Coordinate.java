package com.example.messi_lp.touchdemo;

/**
 * status:
 *      -1--->down
 *       0--->move
 *       1--->up
 */
public class Coordinate {
    private int x;
    private int y;
    public Coordinate(int x,int y){
        this.x=x;
        this.y=y;

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getFormatP(){
        return String.format("(%d,%d)",x,y);

    }
}
