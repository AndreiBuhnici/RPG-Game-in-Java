public abstract class Character extends Entity {
    String name;
    int x;
    int y;
    Inventory inventory;
    int exp;
    int level;
    int charisma;
    int strength;
    int dexterity;

    void buyPotion(Potion pot){
        if(inventory.returnQuantity() >= pot.getWeight()) {
            if (inventory.gold >= pot.getPrice()) {
                inventory.addPotion(pot);
                inventory.gold -= pot.getPrice();
                System.out.println("Potion added to inventory!");
            }
            else
                System.out.println("Not enough money.");
        }
        else
            System.out.println("Not enough space.");
    }

    abstract void gainExperience(int value);
}
