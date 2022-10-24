import java.util.ArrayList;

public class Warrior extends Character{

    public Warrior(String name, int level, int exp, int x, int y){
        maxHealth = 155;
        currentHealth = maxHealth;
        maxMana = 75;
        currentMana = maxMana;
        this.x = x;
        this.y = y;
        this.name = name;
        inventory = new Inventory(10,20);
        iceProtection = false;
        fireProtection = true;
        earthProtection = false;
        this.level = level;
        this.exp = exp;
        strength = 3 + level*2;
        charisma = level;
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
            strength += level*2;
            charisma += level;
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
            int receivedDamage = damage - charisma + dexterity;
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
            return strength*2;
        else {
            System.out.println("Critical Strike!");
            return strength*4;
        }
    }

    @Override
    public void accept(Visitor<Entity> visitor) {
        visitor.visit(this);
    }
}
