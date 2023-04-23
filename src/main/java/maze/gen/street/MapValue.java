package maze.gen.street;

public enum MapValue {
    ROAD(1),EMPTY(0),VOID(-1);
    private final int value;
    MapValue(int value) {
        this.value=value;
    }
    public int getValue() {
        return value;
    }
}
