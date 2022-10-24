public class Earth extends Spell{
    public Earth(){
        damage = 80;
        manaCost = 75;
    }

    @Override
    public void visit(Entity entity) {
        if(entity.earthProtection)
            System.out.println(entity.getClass().getSimpleName() +" has protection to earth.");
        else {
            System.out.println(getClass().getSimpleName() + " spell cast on "
                    + entity.getClass().getSimpleName() +"!");
            entity.receiveDamage(damage);
        }
    }
}
