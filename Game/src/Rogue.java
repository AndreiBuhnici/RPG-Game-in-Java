import java.util.ArrayList;

public class Rogue extends Character{

    public Rogue(String name , int level , int exp, int x, int y){
        maxHealth = 100;
        currentHealth = maxHealth;
        maxMana = 100;
        currentMana = maxMana;
        this.x = x;
        this.y = y;
        this.name = name;
        inventory = new Inventory(6,20);
        iceProtection = false;
        fireProtection = false;
        earthProtection = true;
        this.level = level;
        this.exp = exp;
        strength = level;
        charisma = level;
        dexterity = 3 + level*2;
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
            charisma += level;
            dexterity += level*2;
        }
    }

    @Override
    void receiveDamage(int damage) {
        if (Math.random() < 0.5) {
            currentHealth -= damage;
            System.out.println("Hit! -"+ damage + " HP");
        }
        else {
            int receivedDamage = damage - charisma + strength;
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
            return dexterity*2;
        else {
            System.out.println("Critical Strike!");
            return dexterity*4;
        }
    }

    @Override
    public void accept(Visitor<Entity> visitor) {
        visitor.visit(this);
    }
}
