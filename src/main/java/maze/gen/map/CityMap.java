package maze.gen.map;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CityMap {
    private int chunkSize;
    private HashMap<String,Chunk> chunks;
    private Map<Point,Chunk> edgeChunks;
    private Point playerLoc;
    private int renderDistance;
    public CityMap() {
        chunkSize =Chunk.getSize();
        chunks = new HashMap<>();
        edgeChunks=new HashMap<>();
        playerLoc=new Point(0,0);
        renderDistance=90;
    }

    public void setSquare(int x, int y, int value){
        int locX= (int) Math.floor(x/(chunkSize +0.0));
        int locY= (int) Math.floor(y/(chunkSize +0.0));
        getChunk(locX,locY);
        chunks.get(locX+","+locY).set(Math.floorMod(x, chunkSize),Math.floorMod(y, chunkSize),value);
    }
    public int getSquare(int x, int y){
        int locX= chunkLoc(x);
        int locY= chunkLoc(y);
        getChunk(locX,locY);
        return chunks.get(locX+","+locY).get(Math.floorMod(x, chunkSize),Math.floorMod(y, chunkSize));
    }

    private Chunk getChunk(int locX, int locY) {
        if(!chunks.containsKey(locX+","+locY))
            chunks.put(locX+","+locY,new Chunk(locX,locY));
        return chunks.get(locX+","+locY);
    }

    /**
     * returns the chunk object that the point p is in.
     */
    public Chunk getPointChunk(Point p){
        return getChunk((int) Math.floor(p.x/(chunkSize +0.0)),(int) Math.floor(p.y/(chunkSize +0.0)));
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
        return (int) Math.floor(v/(chunkSize +0.0));
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
    	double segmentSize=360/(edgeLength/(chunkSize/4));
    	System.out.println(segmentSize);
        for (int angle = 0; angle < 360; angle+=segmentSize) {
	         double radian = Math.toRadians(angle);
	         int x = chunkLoc(playerLoc.x)+ (int) (renderDistance  * Math.cos(radian));
	         int y = chunkLoc(playerLoc.y) + (int) (renderDistance * Math.sin(radian));
	         points.add(new Point(x,y));
        }
    	return points;
    }
    public int getChunkSize() {
    	return chunkSize;
    }

    
}