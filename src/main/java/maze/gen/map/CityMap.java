package maze.gen.map;


import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static maze.gen.map.Chunk.getChunkSize;

public class CityMap {
    private final HashMap<Point,Chunk> chunks;
    private final Map<Point,Chunk> edgeChunks;
    private Point playerLoc;
    private final int renderDistance;
    public CityMap() {
        chunks = new HashMap<>();
        edgeChunks=new HashMap<>();
        playerLoc=new Point(0,0);
        renderDistance=10;
    }

    public void setSquare(int x, int y, int value){
        int locX= (int) Math.floor(x/(getChunkSize() +0.0));
        int locY= (int) Math.floor(y/(getChunkSize() +0.0));
        getChunk(locX,locY);
        chunks.get(new Point(locX,locY)).set(Math.floorMod(x, getChunkSize()),Math.floorMod(y, getChunkSize()),value);
    }
    public int getSquare(int x, int y){
        int locX= chunkLoc(x);
        int locY= chunkLoc(y);
        if (Point.distance(x,y, playerLoc.x, playerLoc.y)<10) return 4;
        return getChunk(locX,locY).get(Math.floorMod(x, getChunkSize()),Math.floorMod(y, getChunkSize()));
    }

    private Chunk getChunk(int locX, int locY) {
        Point p=new Point(locX,locY);
        if(!chunks.containsKey(p))
            chunks.put(p,new Chunk(locX,locY));
        return chunks.get(p);
    }

    /**
     * returns the chunk object that the point p is in.
     */
    public Chunk getPointChunk(Point p){
        return getChunk((int) Math.floor(p.x/(getChunkSize() +0.0)),(int) Math.floor(p.y/(getChunkSize() +0.0)));
    }


    public boolean isPointLoaded(Point p){
        double dx = chunkLoc(p.x)-chunkLoc(playerLoc.x);
        double dy = chunkLoc(p.y)-chunkLoc(playerLoc.y);
        return Math.sqrt(dx*dx + dy*dy)<=renderDistance;
    }
    public boolean isChunkLoaded(Point p){
        double dx = p.x-chunkLoc(playerLoc.x);
        double dy = p.y-chunkLoc(playerLoc.y);
        return Math.sqrt(dx*dx + dy*dy)<=renderDistance;
    }
    public int chunkLoc(int v){
        return (int) Math.floor(v/(getChunkSize() +0.0));
    }
    
    public Point chunkLoc(Point p){
        return new Point(chunkLoc(p.x),chunkLoc(p.y));
    }
    
    public Map<Point,Chunk> getEdgeChunks(){
        return edgeChunks;
    }

    public Point getPlayerLoc() {
        return playerLoc;
    }

    public void setPlayerLoc(Point playerLoc) {
        this.playerLoc = playerLoc;
    }
    public Set<Point> playerRadiusChunkLocs(){
    	Set<Point> points=new HashSet<>();
    	double edgeLength=2*Math.PI*renderDistance;
    	double segmentSize=360/edgeLength;
        for (double angle = 0; angle < 360; angle+=segmentSize) {
	         double radian = Math.toRadians(angle);
	         int x = chunkLoc(playerLoc.x)+ (int) (renderDistance  * Math.cos(radian));
	         int y = chunkLoc(playerLoc.y) + (int) (renderDistance * Math.sin(radian));
	         points.add(new Point(x,y));
        }
    	return points;
    }
    public Set<Chunk> getSpawnChunks(){
        double renderDistance=this.renderDistance;

        Set<Chunk> points=new HashSet<>();
        while(renderDistance>0) {
            double edgeLength = 2 * Math.PI * renderDistance;
            double segmentSize = Math.round(360 / edgeLength);
            for (double angle = 0; angle < 360; angle += segmentSize) {
                double radian = Math.toRadians(angle);
                int x = chunkLoc(playerLoc.x) + (int) (renderDistance * Math.cos(radian));
                int y = chunkLoc(playerLoc.y) + (int) (renderDistance * Math.sin(radian));

                points.add(getChunk(x, y));
            }
            //renderDistance-=segmentSize;
            //renderDistance--;
            renderDistance-=0.25;
        }

        return points;

    }
}