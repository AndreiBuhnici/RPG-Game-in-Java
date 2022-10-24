import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Grid extends ArrayList<ArrayList<Cell>> {
    int height;
    int width;
    Character player;
    Cell current_cell;
    private Grid(int height, int width, Character player){
        this.height = height;
        this.width = width;
        this.player = player;
    }

    static Grid grid_gen(int length, int width, Character player){
        Grid grid = new Grid(length, width, player);
        for (int i=0; i<width; i++)
            grid.add(new ArrayList<>(length));

        for (int i=0; i<width; i++){
            for(int j=0; j<length; j++)
                if(i == 0 && j == 0)
                    grid.get(i).add(new Cell(i, j, new Empty(), true));
                else if((i == 0 || i == 1) && j == length - 2) {
                    grid.get(i).add(new Cell(i, j, new Shop(), false));
                }
                else if(i == 2 && j == 0) {
                    grid.get(i).add(new Cell(i, j, new Shop(), false));
                }
                else if(i == length - 2 && j == length - 1) {
                    grid.get(i).add(new Cell(i, j, new Enemy(), false));
                }
                else if(i == length - 1 && j == length - 1) {
                    grid.get(i).add(new Cell(i, j, new Final(), false));
                }
                else {
                    grid.get(i).add(new Cell(i, j, new Empty(), false));
                }
        }
        grid.current_cell = grid.get(0).get(0);
        return grid;
    }

    static Grid rand_grid_gen(Character player){
        List<Integer> sizes = Arrays.asList(4, 5, 6);
        Random randSize = new Random();
        int length = sizes.get(randSize.nextInt(sizes.size()));
        int width = sizes.get(randSize.nextInt(sizes.size()));
        Grid grid = new Grid(length, width, player);
        for (int i=0 ; i<width; i++)
            grid.add(new ArrayList<>(length));

        List<Shop> shopList = new ArrayList<>();
        shopList.add(new Shop());
        shopList.add(new Shop());
        int shopIterator = 0;

        List<Enemy> enemyList = new ArrayList<>();
        enemyList.add(new Enemy());
        enemyList.add(new Enemy());
        enemyList.add(new Enemy());
        enemyList.add(new Enemy());
        int enemyIterator = 0;

        boolean finalPresent = false;

        for (int i=0 ; i<width; i++){
            for(int j=0; j<length; j++)
                if(i == 0 && j == 0)
                    grid.get(i).add(new Cell(i, j, new Empty(), true));
                else {
                    Random rand = new Random();
                    int shopRand = rand.nextInt(100);
                    int enemyRand = rand.nextInt(100);
                    if((shopRand <= 25 && shopIterator < shopList.size()) || (i==width-1 && (j+shopList.size()-shopIterator==length) &&  shopIterator < shopList.size())){
                        grid.get(i).add(new Cell(i, j, shopList.get(shopIterator), true));
                        shopIterator++;
                    } else if((enemyRand <= 25 && enemyIterator < enemyList.size()) || (i==width-1 && (j+enemyList.size()-enemyIterator==length) && enemyIterator < enemyList.size())){
                        grid.get(i).add(new Cell(i, j, enemyList.get(enemyIterator), true));
                        enemyIterator++;
                    }
                    else {
                        if (!finalPresent) {
                            int finalRand = rand.nextInt(100);
                            if (finalRand <= 15) {
                                grid.get(i).add(new Cell(i, j, new Final(), true));
                                finalPresent = true;
                            }
                        } else
                            grid.get(i).add(new Cell(i, j, new Empty(), true));
                    }
                }
        }
        if (!finalPresent) {
            for (int i=0 ; i< width; i++){
                Cell current = grid.get(length - 1).get(i);
                if (current.cellType.toCharacter()== 'E')
                    current.cellType = new Final();
            }
        }
        grid.current_cell = grid.get(0).get(0);
        return grid;
    }

    void goNorth(){
        if((current_cell.x - 1) <0) {
            System.out.println("Can't go North, out of bounds");
            current_cell.wasVisited = true;
        }
        else{
            current_cell = get(current_cell.x - 1).get(current_cell.y);
            player.y -=1;
            if(!current_cell.visited)
                current_cell.visited = true;
            else
                current_cell.wasVisited = true;
        }
    }

    void goSouth(){
        if((current_cell.x + 1) >= width) {
            System.out.println("Can't go South, out of bounds");
            current_cell.wasVisited = true;
        }
        else{
            current_cell = get(current_cell.x + 1).get(current_cell.y);
            player.y +=1;
            if(!current_cell.visited)
                current_cell.visited = true;
            else
                current_cell.wasVisited = true;
        }
    }
    void goWest(){
        if((current_cell.y - 1) < 0) {
            System.out.println("Can't go West, out of bounds");
            current_cell.wasVisited = true;
        }
        else{
            current_cell = get(current_cell.x).get(current_cell.y - 1);
            player.x -=1;
            if(!current_cell.visited)
                current_cell.visited = true;
            else
                current_cell.wasVisited = true;
        }
    }

    void goEast(){
        if((current_cell.y + 1) >= height) {
            System.out.println("Can't go East, out of bounds");
            current_cell.wasVisited = true;
        }
        else{
            current_cell = get(current_cell.x).get(current_cell.y + 1);
            player.x +=1;
            if(!current_cell.visited)
                current_cell.visited = true;
            else
                current_cell.wasVisited = true;
        }
    }
}
