import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Enemy extends Entity implements CellElement{

    public Enemy(){
        int min = 80;
        int max = 100;
        maxHealth = (int) Math.floor(Math.random()*(max-min+1)+min);
        currentHealth = maxHealth;
        maxMana = (int) Math.floor(Math.random()*(max-min+1)+min);
        currentMana = maxMana;
        Random rand = new Random();
        fireProtection = rand.nextBoolean();
        iceProtection = rand.nextBoolean();
        earthProtection = rand.nextBoolean();
        int minSpell = 2;
        int maxSpell = 4;
        int n = (int) Math.floor(Math.random()*(maxSpell-minSpell+1)+minSpell);
        abilities = new ArrayList<>();
        List<Spell> aux = new ArrayList<>();
        aux.add(new Ice());
        aux.add(new Fire());
        aux.add(new Earth());
        Random randAbilities = new Random();
        while(n != 0){
            abilities.add(aux.get(randAbilities.nextInt(aux.size())));
            n--;
        }
    }

    @Override
    public char toCharacter() {
        return 'E';
    }

    @Override
    void receiveDamage(int damage) {
        if(Math.random() < 0.5) {
            currentHealth -= damage;
            System.out.println("Hit! -"+ damage + " HP");
        }
        else
            System.out.println("Dodged!");

        if(currentHealth <= 0 )
            System.out.println("Target is dead.");
    }

    @Override
    int getDamage() {
        if (Math.random() < 0.5)
            return 10;
        else {
            System.out.println("Critical Strike!");
            return 20;
        }
    }

    @Override
    public void accept(Visitor<Entity> visitor) {
        visitor.visit(this);
    }
}
