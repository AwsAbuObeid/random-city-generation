package maze.gen.street;

import maze.gen.map.CityMap;

import java.awt.Point;

import static maze.gen.street.MapValue.ROAD;

public class RoadFactory {
    private final int roadLength;
    private final int roadWidth;
    private final CityMap cityMap;


    public RoadFactory(int roadLength, int roadWidth , CityMap cityMap) {
        this.roadLength = roadLength;
        this.roadWidth = roadWidth;
        this.cityMap = cityMap;
    }
    public Road genRoad(Point start, Direction direction){
        Point end=getNextPoint(start,direction);
        drawRoad(start,end);
        return new Road(start,end,roadWidth,direction);
    }

    public void drawRoad(Point p1, Point p2) {
        int buffer = roadWidth/2 ;
        int evenFix=roadWidth%2==0?1:0;
        if (p1.y == p2.y)
            for (int i = Math.min(p1.x, p2.x) - buffer + evenFix; i <= Math.max(p1.x, p2.x) + buffer; i++)
                for (int j = p1.y - buffer + evenFix; j <= p1.y + buffer; j++)
                    cityMap.setSquare(i, j, ROAD.getValue());
        else
            for (int i = Math.min(p1.y, p2.y) - buffer + evenFix; i <= Math.max(p1.y, p2.y) + buffer; i++)
                for (int j = p1.x - buffer + evenFix; j <= p1.x + buffer; j++)
                    cityMap.setSquare(j, i, ROAD.getValue());
    }

    public Direction[] getOpenDirections(Road r){
        Direction[] directions =new Direction[3];
        int size=0;
        for(Direction direction : Direction.values()){
            if(direction.isOpposite(r.getDir())) continue;
            Point end=getNextPoint(r.getEnd(),direction);
            if(!isConnected(r.getEnd(),end))
                directions[size++]=direction;
        }
        Direction[] result=new Direction[size];
        System.arraycopy(directions,0,result,0,size);
        return result;
    }
    
    private boolean isConnected2(Point curr, Point next){
        for (int x = Math.min(curr.x, next.x); x <= Math.max(curr.x, next.x); x++)
        for (int y = Math.min(curr.y, next.y); y <= Math.max(curr.y, next.y); y++)
        	if (cityMap.getSquare(x, y) != ROAD.getValue())
        		return false;
        return true;
    }

    private boolean isConnected(Point p1, Point p2){
        int buffer = roadWidth/2 ;
        int evenFix=roadWidth%2==0?1:0;
        if (p1.y == p2.y)
            for (int i = Math.min(p1.x, p2.x) - buffer + evenFix; i <= Math.max(p1.x, p2.x) + buffer; i++)
                for (int j = p1.y - buffer + evenFix; j <= p1.y + buffer; j++) {
                    if (cityMap.getSquare(i, j) != 1) return false;
                }
        else
            for (int i = Math.min(p1.y, p2.y) - buffer + evenFix; i <= Math.max(p1.y, p2.y) + buffer; i++)
                for (int j = p1.x - buffer + evenFix; j <= p1.x + buffer; j++) {
                    if (cityMap.getSquare(j, i) != 1) return false;
                }
        return true;
    }
    
    public Point getNextPoint(Point start,Direction direction){
        return new Point(start.x + direction.getVector()[0]*roadLength, start.y + direction.getVector()[1]*roadLength);
    }
}
