package maze.gen.street;

import java.awt.Point;
import java.util.Objects;

import static maze.gen.street.Direction.*;

public class Road {
    private final Point start;
    private final Point end;
    private final int width;
    private final Direction dir;

    public Road(Point start, Point end,int width,Direction dir) {
        this.start = start;
        this.end = end;
        this.width=width;
        this.dir=dir;
    }
    public Road(Point start, Point end,int width) {
        this.start = start;
        this.end = end;
        this.width = width;
        int dx = end.x - start.x;
        int dy = end.y - start.y;

        if (dx < 0)         dir = LEFT;
         else if (dx > 0)   dir = RIGHT;
         else if (dy < 0)   dir = UP;
         else if (dy > 0)   dir = DOWN;
         else throw new IllegalStateException("what the dog doin ?");
    }
    public Point getEnd(){
        return end;
    }

    public int getWidth() {
        return width;
    }
    public Direction getDir() {
        return dir;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Road road = (Road) o;
        return width == road.width &&
                ((start.equals(road.start) && end.equals(road.end)) || (start.equals(road.end) && end.equals(road.start)));
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end, width);
    }
}
