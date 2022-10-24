public class HealthPotion implements Potion{
    private final int price;
    private final int regenValue;
    private final int weight;

    public HealthPotion(int price, int regenValue, int weight){
        this.price = price;
        this.regenValue = regenValue;
        this.weight = weight;
    }

    @Override
    public void usePotion(Character player) {
        player.regenerateHP(getRegen());
        player.inventory.potions.remove(this);
        System.out.println("Used a Health Potion");
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
