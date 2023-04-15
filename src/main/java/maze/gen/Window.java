package maze.gen;


import maze.gen.street.Generator;

import javax.swing.*;
import java.awt.*;

public class Window extends JPanel {
    private int sizeX, sizeY, blockSize;
    private final boolean showGridLines;
    private final Generator gen;
    private final Point center;
    private int seed;
    public Window(int sizeX, int sizeY, int seed) {
        blockSize=1;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.showGridLines=false;
        this.seed=seed;
        this.gen =new Generator();
        center=new Point(0,0);
        gen.generateGrid(seed);
    }
    public void zoomIn(){
        blockSize++;
        sizeX=  getWidth()/blockSize;
        sizeY= getHeight()/blockSize;
        repaint();
    }
    public void zoomOut() {
        if(blockSize==1) return;
        blockSize--;
        sizeX=  getWidth()/blockSize;
        sizeY= getHeight()/blockSize;
        repaint();
    }
    public void changeSeed(int seed) {
        this.seed=seed;
        gen.generateGrid(seed);
        repaint();
    }
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        if(showGridLines) drawGridLines(g2);
        drawMap(g2);
    }
    private void drawGridLines(Graphics2D g2){
        for (int i = 0; i< getWidth(); i+=blockSize)
        for (int j = 0; j< getHeight(); j+=blockSize){
            g2.setColor(Color.BLACK);
            g2.drawLine(i,0,i, sizeY);
            g2.drawLine(0,j, sizeX,j);
        }
    }
    public void moveWindow(int movementX, int movementY){
        movementX/=blockSize;
        movementY/=blockSize;
        center.x-=movementX;
        center.y-=movementY;
        repaint();
    }

    private void drawMap(Graphics2D g2){
        for(int i = 0; i< getWidth() ; i++)
        for (int j = 0; j< getHeight(); j++){
            int x =i+center.x -sizeX/2;
            int y =j+center.y -sizeY/2;
            g2.setColor(Color.gray);
            if(map(x,y)==1) {
                g2.setColor(Color.darkGray);
                g2.fillRect(blockSize * i, blockSize * j, blockSize, blockSize);
                int smallSquareSize = blockSize / 4;
                int smallSquareX = blockSize * i + blockSize / 2 - smallSquareSize / 2;
                int smallSquareY = blockSize * j + blockSize / 2 - smallSquareSize / 2;
                g2.setColor(Color.white);
                g2.fillRect(smallSquareX, smallSquareY, smallSquareSize, smallSquareSize);
            } else if (map(x,y)==2) {
                g2.setColor(Color.green);
                g2.fillRect(blockSize * i, blockSize * j, blockSize, blockSize);
            }
            else if (map(x,y)>2) {
                g2.setColor(Color.blue);
                g2.fillRect(blockSize * i, blockSize * j, blockSize, blockSize);
            }

        }
    }
    public void incProb() {
        gen.straightRoadProb+=0.01;
        gen.generateGrid(seed);
        repaint();
        System.out.println("repainted with :" + gen.straightRoadProb);
    }

    public void decProb() {
        gen.straightRoadProb-=0.005;
        gen.generateGrid(seed);
        repaint();
        System.out.println("repainted with :" + gen.straightRoadProb);
    }
    private int map(int x,int y){
        return gen.getMap().getSquare(x,y);
    }

    public void movePlayer(int movementX, int movementY){
        gen.movePlayer(movementX,movementY);
        repaint();
    }
}
