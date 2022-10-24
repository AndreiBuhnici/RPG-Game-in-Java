public class CharacterFactory {

    public Character createCharacter(String characterType, String name, int lvl, int exp, int x, int y) {
        switch (characterType) {
            case "Warrior":
                return new Warrior(name,lvl,exp,x,y);
            case "Rogue":
                return new Rogue(name,lvl,exp,x,y);
            case "Mage":
                return new Mage(name,lvl,exp,x,y);
        }
        return null;
    }
}