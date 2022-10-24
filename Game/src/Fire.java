public class Fire extends Spell{
    public Fire(){
        damage = 40;
        manaCost = 35;
    }

    @Override
    public void visit(Entity entity) {
        if(entity.fireProtection)
            System.out.println(entity.getClass().getSimpleName() +" has protection to fire.");
        else {
            System.out.println(getClass().getSimpleName() + " spell cast on "
                    + entity.getClass().getSimpleName() +"!");
            entity.receiveDamage(damage);
        }
    }
}
