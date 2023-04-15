package maze.gen.map;

import java.awt.Point;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CityMap {
    int chunkSize;
    HashMap<String,Chunk> chunks;
    Set<Chunk> edgeChunks;
    Point playerLoc;
    int renderDistance;
    public CityMap() {
        chunkSize =Chunk.getSize();
        chunks = new HashMap<>();
        edgeChunks=new HashSet<>();
        playerLoc=new Point(0,0);
        renderDistance=60;
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
    private int chunkLoc(int v){
        return (int) Math.floor(v/(chunkSize +0.0));
    }
    public Set<Chunk> getEdgeChunks(){
        return edgeChunks;
    }

    public Point getPlayerLoc() {
        return playerLoc;
    }

    public void setPlayerLoc(Point playerLoc) {
        this.playerLoc = playerLoc;
    }
}