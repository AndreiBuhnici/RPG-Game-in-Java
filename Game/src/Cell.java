public class Cell {
    enum type{
        N, E, S, F
    }
    int x;
    int y;
    CellElement cellType;
    boolean visited;
    boolean wasVisited;

    public Cell(int x, int y, CellElement cellType, boolean visited){
        this.x = x;
        this.y = y;
        this.cellType = cellType;
        this.visited = visited;
        wasVisited = false;

    }
}
