public class Ice extends Spell{
    public Ice(){
        damage = 20;
        manaCost = 15;
    }

    @Override
    public void visit(Entity entity) {
        if(entity.iceProtection)
            System.out.println(entity.getClass().getSimpleName() +" has protection to ice.");
        else {
            System.out.println(getClass().getSimpleName() + " spell cast on "
                    + entity.getClass().getSimpleName() +"!");
            entity.receiveDamage(damage);
        }
    }
}
