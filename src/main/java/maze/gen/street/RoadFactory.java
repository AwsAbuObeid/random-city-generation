package maze.gen.street;

import maze.gen.map.CityMap;

import java.awt.Point;

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
                    cityMap.setSquare(i, j, 1);
        else
            for (int i = Math.min(p1.y, p2.y) - buffer + evenFix; i <= Math.max(p1.y, p2.y) + buffer; i++)
                for (int j = p1.x - buffer + evenFix; j <= p1.x + buffer; j++)
                    cityMap.setSquare(j, i, 1);
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
    
    private boolean isConnected(Point curr, Point next){
        for (int x = Math.min(curr.x, next.x); x <= Math.max(curr.x, next.x); x++)
        for (int y = Math.min(curr.y, next.y); y <= Math.max(curr.y, next.y); y++)
        	if (cityMap.getSquare(x, y) != 1) 
        		return false;
        return true;
    }
    
    private Point getNextPoint(Point start,Direction direction){
        return new Point(start.x + direction.getVector()[0]*roadLength, start.y + direction.getVector()[1]*roadLength);
    }
    
    private boolean is2Connected(Point curr, Point next){
    	 int buffer = roadWidth/2 ;
         int evenFix=roadWidth%2==0?1:0;
         if (curr.y == next.y) {
             for (int i = Math.min(curr.x, next.x) - buffer + evenFix; i <= Math.max(curr.x, next.x) + buffer; i++)
                 for (int j = curr.y - buffer + evenFix; j <= curr.y + buffer; j++)
                	 if(cityMap.getSquare(i,j)!=1) return false;
         
         }else
             for (int i = Math.min(curr.y, next.y) - buffer + evenFix; i <= Math.max(curr.y, next.y) + buffer; i++)
                 for (int j = curr.x - buffer + evenFix; j <= curr.x + buffer; j++)
                	 if(cityMap.getSquare(i,j)!=1) return false;
         return true;
    }
    private boolean is3Connected(Point curr, Point next){
        return cityMap.getSquare((2*curr.x + next.x)/3,(2*curr.y + next.y)/3)==1;
    }
    private boolean is4Connected(Point curr, Point next){
        int evenFix1=roadWidth%2==0?1:0;
        int startX = (curr.x + next.x)/2 - roadWidth / 2 +  evenFix1;
        int startY = (curr.y + next.y)/2 - roadWidth / 2 +  evenFix1;
        int endX = (curr.x + next.x)/2 + roadWidth / 2  ;
        int endY = (curr.y + next.y)/2 + roadWidth / 2  ;

        for (int x = startX; x <= endX; x++)
            for (int y = startY; y <= endY; y++)
                if(cityMap.getSquare(x,y)!=1) return false;
        return true;
    }



}
