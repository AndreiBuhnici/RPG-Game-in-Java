public class ManaPotion implements Potion{
    private final int price;
    private final int regenValue;
    private final int weight;

    public ManaPotion(int price, int regenValue, int weight){
        this.price = price;
        this.regenValue = regenValue;
        this.weight = weight;
    }

    @Override
    public void usePotion(Character player) {
        player.regenerateMP(getRegen());
        player.inventory.potions.remove(this);
        System.out.println("Used a Mana Potion");
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public int getRegen() {
        return regenValue;
    }

    @Override
    public int getWeight() {
        return weight;
    }
}
