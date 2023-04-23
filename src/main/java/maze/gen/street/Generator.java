package maze.gen.street;

import maze.gen.map.Chunk;
import maze.gen.map.CityMap;

import java.awt.Point;
import java.util.*;

import static maze.gen.street.Direction.*;

public class Generator {

    private Deque<Chunk> generationQueue;
    private Map<Integer, RoadFactory> factories;
    private CityMap cityMap;
    private int seed;
    public double straightRoadProb = 0.5;
    public double curvedRoadProb =straightRoadProb/2;
    int smallestGridSize = 6;
    int smallestRoadWidth =1;
    int gridSizeMultiplier =5;
    int roadWidthMultiplier=2;
    int levels=3;
    double smallerStreetScaleFactor =0.1;
    int[] roadWidths;

    public void generateGrid(int seed) {
        this.seed=seed;
        factories=new HashMap<>();
        cityMap=new CityMap();
        generationQueue=new ArrayDeque<>();
        roadWidths=new int[levels];
        long startTime = System.currentTimeMillis(); // Record the start time
        for(int i=0;i<levels;i++){
            int width= (int) (smallestRoadWidth*(Math.pow(roadWidthMultiplier,i)));
            int length=(int) (smallestGridSize*(Math.pow(gridSizeMultiplier,i)));
            factories.put(width,new RoadFactory(length,width,cityMap));
            roadWidths[i]=width;
            if(i==levels-1){
                Chunk.setSize(length);
                //Chunk.setSize(16);
                Road newRoad=factories.get(width).genRoad(new Point(0,0),RIGHT);
                cityMap.getPointChunk(newRoad.getEnd()).addRoad(newRoad);
                newRoad=factories.get(width).genRoad(new Point(0,0),LEFT);
                cityMap.getPointChunk(newRoad.getEnd()).addRoad(newRoad);
                newRoad=factories.get(width).genRoad(new Point(0,0),UP);
                cityMap.getPointChunk(newRoad.getEnd()).addRoad(newRoad);
                newRoad=factories.get(width).genRoad(new Point(0,0),DOWN);
                cityMap.getPointChunk(newRoad.getEnd()).addRoad(newRoad);

            }
        }
        generationQueue.addAll(cityMap.getSpawnChunks());
        fractalRoads();
        System.out.println("generation took " + (System.currentTimeMillis() - startTime) / 1000.0 + " seconds to run.");

    }
    public CityMap getMap(){
        return cityMap;
    }

    private void fractalRoads() {
        while (!generationQueue.isEmpty()) {
            System.out.println(generationQueue.size());
            Chunk chunk = generationQueue.pop();
            for (int roadWidth : roadWidths)
                while(!chunk.getConPoints(roadWidth).isEmpty()) {
                Road r = chunk.getConPoints(roadWidth).pop();
                String newSeed = seed + " " + r.getEnd().x + " " + r.getEnd().y;
                Random random = new Random(new Random(newSeed.hashCode()).nextInt());
                Direction[] openDirs = factories.get(roadWidth).getOpenDirections(r);
                Collections.shuffle(Arrays.asList(openDirs), random);
                boolean connected = openDirs.length != 3;
                for (int i = 0; i < openDirs.length; i++) {
                    double prob = straightRoadProb;
                    if (r.getDir() != openDirs[i]) prob = curvedRoadProb;
                    if (random.nextDouble() < prob || (i == openDirs.length - 1 && !connected)) {
                        Direction dir = openDirs[i];
                        if ((i == openDirs.length - 1 && !connected) /*&& Arrays.asList(openDirs).contains(r.getDir())*/)
                            dir = r.getDir();
                        if (!connected) connected = true;
                        //if (cityMap.getPointChunk(factories.get(roadWidth).getNextPoint(r.getEnd(),dir)).equals(chunk))
                        Road newRoad = factories.get(roadWidth).genRoad(r.getEnd(), dir);
                        Direction[] directions = factories.get(roadWidth).getOpenDirections(newRoad);
                        createSmallerRoads(newRoad.getEnd(), directions, roadWidth / roadWidthMultiplier, random);

                        cityMap.getPointChunk(newRoad.getEnd()).addRoad(newRoad);

                    }
                }
            }
            // chunk.setGenerated(true);

        }
    }
    private void createSmallerRoads(Point p,Direction[] directions,int roadWidth,Random random){
        if (!factories.containsKey(roadWidth)||! cityMap.isPointLoaded(p)) return;
        for(Direction dir: directions){
            double prob = 1.0 - smallerStreetScaleFactor * roadWidth;
            if(random.nextDouble()<prob) {
                Road road = factories.get(roadWidth).genRoad(p, dir);
                cityMap.getPointChunk(road.getEnd()).addRoad(road);
            }
        }

    }


    public void movePlayer(int movementX, int movementY) {
        Point loc=cityMap.getPlayerLoc();
        cityMap.setPlayerLoc(new Point(loc.x+movementX, loc.y+movementY));
        System.out.println(cityMap.getPlayerLoc());
        //TODO
        //if movement is less than half a chunk then record it and dont load chunks
        
        Set<Point> radiusChunkPoints=cityMap.playerRadiusChunkLocs();
        for(Point chunkLoc :radiusChunkPoints){
        	if(!cityMap.isChunkLoaded(chunkLoc)) continue;
        	Chunk chunk=cityMap.getEdgeChunks().get(chunkLoc);
        	if(chunk==null) continue;
            for(int width:roadWidths) continue;
            	//getConRoads(width).addAll(chunk.getConPoints(width));
        }
        fractalRoads();
    }
    
}
