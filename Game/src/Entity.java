import java.util.ArrayList;

public abstract class Entity implements Element<Entity> {
    ArrayList<Spell> abilities;
    int currentHealth;
    int maxHealth;
    int currentMana;
    int maxMana;
    boolean fireProtection;
    boolean iceProtection;
    boolean earthProtection;

    void regenerateHP(int health){
        currentHealth += health;
        if(currentHealth > maxHealth)
            currentHealth = maxHealth;
    }

    void regenerateMP(int mana){
        currentMana += mana;
        if(currentMana > maxMana)
            currentMana = maxMana;
    }

    void useAbility(Spell ability, Entity target){
        if(currentMana >= ability.manaCost){
            currentMana -= ability.manaCost;
            target.accept(ability);
            abilities.remove(ability);
        } else
            System.out.println("Not enough mana.");
    }

    abstract void receiveDamage(int damage);
    abstract int getDamage();
}
