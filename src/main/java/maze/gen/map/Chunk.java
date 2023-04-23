package maze.gen.map;

import maze.gen.street.Road;

import java.awt.Point;
import java.util.*;

public class Chunk {
    private static int size;
    private final int[][] map;
    private final Map<Integer,Deque<Road>> conPointsMap;
    private final Point loc;
    private boolean generated = false;
    public static void setSize(int size){
        Chunk.size =size;
    }
    public boolean isGenerated(){
        return generated;
    }
    public void setGenerated(boolean generated){
        this.generated=generated;
    }
    public Chunk(int locX,int locY){
        map=new int[size][size];
        loc=new Point(locX,locY);
        conPointsMap=new HashMap<>();
    }
    public static int getChunkSize() {
        return size;
    }
    public void set(int x,int y,int value){
        if(generated) {
            System.out.println(loc + " is already generated my guy !");
            return;
        }
        map[x][y]=value;
    }
    public int get(int x,int y){
        return map[x][y];
    }
    public Deque<Road> getConPoints(int roadWidth){
        if(!conPointsMap.containsKey(roadWidth))
            conPointsMap.put(roadWidth,new ArrayDeque<>());
        return conPointsMap.get(roadWidth);
    }
    public void addRoad(Road road){
        getConPoints(road.getWidth()).push(road);
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Chunk))
            return false;
        Chunk other = (Chunk) obj;
        return this.loc.equals(other.loc) ;
    }
    @Override
    public int hashCode() {
        return loc.hashCode();
    }

    public Point getLoc() {
        return loc;
    }
}
