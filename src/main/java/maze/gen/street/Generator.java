package maze.gen.street;

import maze.gen.map.Chunk;
import maze.gen.map.CityMap;

import java.awt.Point;
import java.util.*;

import static maze.gen.street.Direction.*;

public class Generator {
    private Map<Integer,Deque<Road>> conRoadsMap;
    public double straightRoadProb = 0.9;
    private CityMap cityMap;
    private int seed;
    private Map<Integer, RoadFactory> factories;

    int smallestGridSize = 5;
    int smallestRoadWidth =1;
    int gridSizeMultiplier =6;
    int roadWidthMultiplier=2;
    int levels=3;
    double smallerStreetScaleFactor =0.1;
    int[] roadWidths;

    public void generateGrid(int seed) {
        this.seed=seed;
        factories=new HashMap<>();
        cityMap=new CityMap();
        conRoadsMap =new HashMap<>();
        roadWidths=new int[levels];

        for(int i=0;i<levels;i++){
            int width= (int) (smallestRoadWidth*(Math.pow(roadWidthMultiplier,i)));
            int length=(int) (smallestGridSize*(Math.pow(gridSizeMultiplier,i)));
            factories.put(width,new RoadFactory(length,width,cityMap));
            roadWidths[i]=width;
            if(i==levels-1){
                getConRoads(width).push(factories.get(width).genRoad(new Point(0,0),RIGHT));
                getConRoads(width).push(factories.get(width).genRoad(new Point(0,0),LEFT));
                getConRoads(width).push(factories.get(width).genRoad(new Point(0,0),UP));
                getConRoads(width).push(factories.get(width).genRoad(new Point(0,0),DOWN));
            }
        }
        for(int i=levels-1;i>=0;i--)
            fractalRoads((int)(smallestRoadWidth*(Math.pow(roadWidthMultiplier,i))),straightRoadProb);
        //fractalRoads(4,straightRoadProb);

    }
    public CityMap getMap(){
        return cityMap;
    }

    private boolean roadExists(Point b){
        if(cityMap.getSquare(b.x-1,b.y)==1 &&cityMap.getSquare(b.x+1,b.y)==1)
            return true;
        if(cityMap.getSquare(b.x,b.y-1)==1 &&cityMap.getSquare(b.x,b.y+1)==1)
            return true;
        return false;
    }
    private void fractalRoads(int roadWidth,double straightRoadProb) {
        while (!getConRoads(roadWidth).isEmpty()) {
            Road r = getConRoads(roadWidth).pop();
            String newSeed = seed + " " + r.getEnd().x + " " + r.getEnd().y;
            Random random = new Random(new Random(newSeed.hashCode()).nextInt());
            Direction[] openDirs = factories.get(roadWidth).getOpenDirections(r);
            Collections.shuffle(Arrays.asList(openDirs), random);
            boolean connected = openDirs.length != 3;
            for (Direction dir : openDirs) {
                double prob = straightRoadProb;
                if (r.getDir() != dir) prob = (1 - straightRoadProb);
                if (random.nextDouble() < prob) {
                    if (!connected) connected = true;
                    Road newRoad = factories.get(roadWidth).genRoad(r.getEnd(), dir);
                    createSmallerRoads(newRoad.getEnd(), factories.get(roadWidth).getOpenDirections(newRoad), roadWidth /roadWidthMultiplier, random);
                    if (factories.get(roadWidth).getOpenDirections(newRoad).length != 1)
                        storeRoad(newRoad);
                }
            }
            if (!connected) {
                Direction dir = random.nextDouble() < straightRoadProb ? r.getDir() : openDirs[random.nextInt(openDirs.length)];
                Road newRoad = factories.get(roadWidth).genRoad(r.getEnd(), dir);
                createSmallerRoads(newRoad.getEnd(), factories.get(roadWidth).getOpenDirections(newRoad), roadWidth/roadWidthMultiplier, random);
                if (factories.get(roadWidth).getOpenDirections(newRoad).length != 1)
                    storeRoad(newRoad);
            }
        }
    }
    private void createSmallerRoads(Point p,Direction[] directions,int roadWidth,Random random){
        if (!factories.containsKey(roadWidth)||! cityMap.isPointLoaded(p)) return;
        for(Direction dir: directions){
            double prob = 1.0 - smallerStreetScaleFactor * roadWidth;
            if(random.nextDouble()<prob) {
                Road road = factories.get(roadWidth).genRoad(p, dir);
                storeRoad(road);
            }
        }

    }
    private void storeRoad(Road newRoad){
        if (!cityMap.isPointLoaded(newRoad.getEnd())) {
            cityMap.getEdgeChunks().add(cityMap.getPointChunk(newRoad.getEnd()));
            cityMap.getPointChunk(newRoad.getEnd()).addRoad(newRoad);
        } else
            getConRoads(newRoad.getWidth()).push(newRoad);
    }

    private Deque<Road> getConRoads(int roadWidth){
        if(!conRoadsMap.containsKey(roadWidth))
            conRoadsMap.put(roadWidth,new ArrayDeque<>());
        return conRoadsMap.get(roadWidth);
    }

    public void movePlayer(int movementX, int movementY) {
        Point loc=cityMap.getPlayerLoc();
        cityMap.setPlayerLoc(new Point(loc.x+movementX, loc.y+movementY));
        System.out.println(cityMap.getPlayerLoc());
        Set<Chunk> edgeChunks=cityMap.getEdgeChunks();
        for(Chunk chunk:edgeChunks){
            if(cityMap.isChunkLoaded(chunk.getLoc()))
                for(int width:roadWidths)
                    getConRoads(width).addAll(chunk.getConPoints(width));
        }
        for(int width:roadWidths)
            fractalRoads(width,straightRoadProb);
    }
}
