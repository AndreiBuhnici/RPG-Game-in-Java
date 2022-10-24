import java.util.ArrayList;
import java.util.List;

public class Inventory {
    List<Potion> potions;
    int maxQuantity;
    int gold;

    public Inventory(int maxQuantity ,int gold){
        potions = new ArrayList<>();
        this.maxQuantity = maxQuantity;
        this.gold = gold;
    }

    void addPotion(Potion pot){
        potions.add(pot);
    }

    Potion removePotion(int index){
        Potion aux = potions.get(index);
        potions.remove(aux);
        return aux;
    }

    int returnQuantity(){
        int quantity = 0;
        for(Potion aux : potions){
            quantity += aux.getWeight();
        }
        return maxQuantity - quantity;
    }
}
