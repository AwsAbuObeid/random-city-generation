package maze.gen;


import maze.gen.map.Chunk;
import maze.gen.street.Generator;
import maze.gen.street.MapValue;

import javax.swing.*;
import java.awt.*;
import java.util.Deque;
import java.util.Set;

import static maze.gen.map.Chunk.getChunkSize;

public class Window extends JPanel {
    private int blockSize,scaleFactor;
    private final boolean showGridLines;
    private final Generator gen;
    private final Point center;
    private int seed;
    private int chunkSize;
    private Set<Chunk> test;
    public Window( ) {
        blockSize=1;
        this.showGridLines=false;
        this.gen =new Generator();
        center=new Point(0,0);
        gen.generateGrid(123456);
        chunkSize= getChunkSize();
        //test=gen.getMap().getSpawnChunks();
    }
    public void zoomIn(){
        blockSize++;
        chunkSize =  getChunkSize() * blockSize;
        repaint();
    }
    public void zoomOut() {
        if(blockSize==1) return;
        blockSize--;
        chunkSize =  getChunkSize() * blockSize;
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
    private void drawGridLines(Graphics2D g2){
        int modX=center.x*blockSize;
        int modY=center.y*blockSize;
        int startX=modX-modX%chunkSize;
        int startY=modY-modY%chunkSize;
        int endX=getWidth();
        int endY=getHeight();
        g2.setColor(Color.RED);

        for (int i = startX; i < endX+modX; i += chunkSize) {
            int x = i-modX;
            g2.drawLine(x, 0, x, getHeight());
        }

        for (int j = startY; j < endY+modY; j += chunkSize) {
            int y = j -modY ;
            g2.drawLine(0, y, getWidth(), y);
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
            int x =i+center.x ;
            int y =j+center.y ;
            g2.setColor(Color.gray);
            if(map(x,y)== MapValue.ROAD.getValue()) {
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
    public void incStraightProb() {
        gen.straightRoadProb+=0.01;
        gen.generateGrid(seed);
        repaint();
        System.out.println("straightRoadProb :" + gen.straightRoadProb);
    }

    public void decStraightProb() {
        gen.straightRoadProb-=0.005;
        gen.generateGrid(seed);
        repaint();
        System.out.println("straightRoadProb :" + gen.straightRoadProb);
    }
    private int map(int x,int y){
        //if(test.contains(gen.getMap().getPointChunk(new Point(x, y)))) return 4;
        return gen.getMap().getSquare(x,y);
    }

    public void movePlayer(int movementX, int movementY){
        gen.movePlayer(movementX,movementY);
        repaint();
    }

    public void decCurveProb() {
        gen.curvedRoadProb-=0.01;
        gen.generateGrid(seed);
        repaint();
        System.out.println("curvedRoadProb :" + gen.curvedRoadProb);
    }

    public void incCurveProb() {
        gen.curvedRoadProb+=0.005;
        gen.generateGrid(seed);
        repaint();
        System.out.println("curvedRoadProb :" + gen.curvedRoadProb);
    }
}
