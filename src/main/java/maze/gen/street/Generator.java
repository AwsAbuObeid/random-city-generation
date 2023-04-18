package maze.gen.street;

import maze.gen.map.Chunk;
import maze.gen.map.CityMap;

import java.awt.Point;
import java.util.*;

import static maze.gen.street.Direction.*;

public class Generator {
    private Map<Integer,Deque<Road>> conRoadsMap;
    private Map<Integer, RoadFactory> factories;
    private CityMap cityMap;
    private int seed;
    public double straightRoadProb = 0.9;
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

    }
    public CityMap getMap(){
        return cityMap;
    }

    private void fractalRoads(int roadWidth,double straightRoadProb) {
        while (!getConRoads(roadWidth).isEmpty()) {
            Road r = getConRoads(roadWidth).pop();
            String newSeed = seed + " " + r.getEnd().x + " " + r.getEnd().y;
            Random random = new Random(new Random(newSeed.hashCode()).nextInt());
            Direction[] openDirs = factories.get(roadWidth).getOpenDirections(r);
            Collections.shuffle(Arrays.asList(openDirs), random);
            boolean connected = openDirs.length != 3;
            for ( int i=0;i<openDirs.length;i++) {
                double prob = straightRoadProb;
                if (r.getDir() != openDirs[i]) prob = (1 - straightRoadProb);
                if (random.nextDouble() < prob || (i==openDirs.length-1 && !connected)) {
                    if (!connected) connected = true;
                    Road newRoad = factories.get(roadWidth).genRoad(r.getEnd(), openDirs[i]);
                    createSmallerRoads(newRoad.getEnd(), factories.get(roadWidth).getOpenDirections(newRoad), roadWidth /roadWidthMultiplier, random);
                    if (factories.get(roadWidth).getOpenDirections(newRoad).length != 1)
                        storeRoad(newRoad);
                }
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
    	Point roadEnd =newRoad.getEnd();
        if (!cityMap.isPointLoaded(roadEnd)) {
            cityMap.getEdgeChunks().put(cityMap.chunkLoc(roadEnd),cityMap.getPointChunk(roadEnd));
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
        //TODO
        //if movement is less than half a chunk then record it and dont load chunks
        
        Set<Point> radiusChunkPoints=cityMap.playerRadiusChunkLocs();
        System.out.println("size "+radiusChunkPoints.size());
        for(Point chunkLoc :radiusChunkPoints){
        	if(!cityMap.isChunkLoaded(chunkLoc)) continue;
        	Chunk chunk=cityMap.getEdgeChunks().get(chunkLoc);
        	if(chunk==null) continue;
            for(int width:roadWidths)
            	getConRoads(width).addAll(chunk.getConPoints(width));
        }
        for(int width:roadWidths)
            fractalRoads(width,straightRoadProb);
    }
    
}
