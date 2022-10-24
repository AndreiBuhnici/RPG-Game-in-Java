import java.util.ArrayList;

public class Mage extends Character{

    public Mage(String name , int level , int exp, int x, int y){
        maxHealth = 85;
        currentHealth = maxHealth;
        maxMana = 155;
        currentMana = maxMana;
        this.x = x;
        this.y = y;
        this.name = name;
        inventory = new Inventory(4,20);
        iceProtection = true;
        fireProtection = false;
        earthProtection = false;
        this.level = level;
        this.exp = exp;
        strength = level;
        charisma = 3 + level*2;
        dexterity = level;
        abilities = new ArrayList<>();
        abilities.add(new Ice());
        abilities.add(new Fire());
        abilities.add(new Earth());
    }

    void gainExperience(int value){
        exp += value;
        if(exp >= 100) {
            level++;
            exp = exp - 100;
            strength += level;
            charisma += level*2;
            dexterity += level;
        }
    }

    @Override
    void receiveDamage(int damage) {
        if (Math.random() < 0.5) {
            currentHealth -= damage;
            System.out.println("Hit! -"+ damage + " HP");
        }
        else {
            int receivedDamage = damage - (dexterity + strength);
            if(receivedDamage < 0)
                receivedDamage = 0;
            currentHealth -= receivedDamage;
            System.out.println("Hit! -"+ (receivedDamage) + " HP");
        }

        if(currentHealth <= 0)
            System.out.println("You died...");
    }

    @Override
    int getDamage() {
        if (Math.random() < 0.5)
            return charisma*2;
        else {
            System.out.println("Critical Strike!");
            return charisma*4;
        }
    }

    @Override
    public void accept(Visitor<Entity> visitor) {
        visitor.visit(this);
    }
}
