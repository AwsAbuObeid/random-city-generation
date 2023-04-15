package maze.gen.street;

public enum Direction {
    UP(-1,0),DOWN(1,0),LEFT(0,-1),RIGHT(0,1);

    private final int[] vector;
    Direction(int i, int j){
        this.vector =new int[]{i,j};
    }
    public int[] getVector(){
        return vector;
    }
    public boolean isOpposite(Direction direction){
        return (direction.vector[0] + vector[0] == 0) && (direction.vector[1] + vector[1] == 0);
    }

}
