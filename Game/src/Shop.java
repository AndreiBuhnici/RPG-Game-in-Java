import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Shop implements CellElement{
    List<Potion> potionList;

    public Shop(){
        potionList = new ArrayList<>();
        int max = 4;
        int min = 2;
        int n = (int) Math.floor(Math.random()*(max-min+1)+min);
        List<Potion> potList = new ArrayList<>();
        potList.add(new HealthPotion(5,25,1));
        potList.add(new HealthPotion(10,50,2));
        potList.add(new ManaPotion(5,25,1));
        potList.add(new ManaPotion(10,50,2));
        Random rand = new Random();
        while(n != 0){
            Potion aux = potList.get(rand.nextInt(potList.size()));
            potionList.add(aux);
            n--;
        }
    }

    public Potion selectPotion(int index){
        Potion aux = potionList.get(index);
        potionList.remove(index);
        return aux;
    }

    @Override
    public char toCharacter() {
        return 'S';
    }
}
