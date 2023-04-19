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
    private int chunkSize;
    public Window(int sizeX, int sizeY, int seed) {
        blockSize=1;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.showGridLines=true;
        this.seed=seed;
        this.gen =new Generator();
        center=new Point(0,0);
        gen.generateGrid(seed);
        chunkSize=gen.getMap().getChunkSize();
    }
    public void zoomIn(){
        blockSize++;
        sizeX=  getWidth()/blockSize;
        sizeY= getHeight()/blockSize;
        chunkSize = gen.getMap().getChunkSize() * blockSize;
        repaint();
    }
    public void zoomOut() {
        if(blockSize==1) return;
        blockSize--;
        sizeX=  getWidth()/blockSize;
        sizeY= getHeight()/blockSize;
        chunkSize = gen.getMap().getChunkSize() * blockSize;
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
        drawMap(g2);
        if(showGridLines) drawGridLines(g2);

    }
    private void drawGridLines3(Graphics2D g2){
        int chunkSizeInPixels = chunkSize * blockSize;
        int startX = center.x % chunkSizeInPixels;
        int startY = center.y % chunkSizeInPixels;
        
        g2.setColor(Color.RED);
        
        // Vertical lines
        int x = startX;
        while (x < getWidth()) {
            g2.drawLine(x, 0, x, getHeight());
            x += chunkSizeInPixels;
        }
        
        // Horizontal lines
        int y = startY;
        while (y < getHeight()) {
            g2.drawLine(0, y, getWidth(), y);
            y += chunkSizeInPixels;
        }
    }
    
    private void drawGridLines(Graphics2D g2){
        int startX = center.x - (getWidth() / blockSize) / 2;
        int startY = center.y - (getHeight() / blockSize) / 2;
        startX = (startX / chunkSize) * chunkSize;
        startY = (startY / chunkSize) * chunkSize;
        for (int i = startX; i < getWidth() + startX; i += chunkSize) {
            for (int j = startY; j < getHeight() + startY; j += chunkSize) {
                int x = i - center.x;
                int y = j - center.y;
                g2.setColor(Color.RED);
                g2.drawLine(x, 0, x, getHeight());
                g2.drawLine(0, y, getWidth(), y);
            }
        }
    }

    private void drawGridLines2(Graphics2D g2){
        for (int i = 0; i< getWidth(); i+=chunkSize)
        for (int j = 0; j< getHeight(); j+=chunkSize){
        	int x = i - center.x % chunkSize;
            int y = j - center.y % chunkSize;
            g2.setColor(Color.RED);
            g2.drawLine(x,0,x, getHeight());
            g2.drawLine(0,y, getWidth(),y);
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
