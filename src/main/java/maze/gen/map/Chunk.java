package maze.gen.map;

import maze.gen.street.Road;

import java.awt.Point;
import java.util.*;

public class Chunk {
    private static final int size=16;
    private final int[][] map;
    private Map<Integer,Deque<Road>> conPointsMap;
    private final Point loc;
    public Chunk(int locX,int locY){
        map=new int[size][size];
        loc=new Point(locX,locY);
        conPointsMap=new HashMap<>();
    }
    public static int getSize() {
        return size;
    }
    public void set(int x,int y,int value){
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
