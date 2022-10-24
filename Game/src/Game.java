import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Game {
    List<Account> accounts;
    Map<Cell.type,List<String>> stories;
    int defeatedEnemies;
    int earnedExperience;
    int earnedGold;

    // Singleton pattern
    private static Game instance = null;

    private Game(){}

    public static Game getInstance(){
        if(instance == null)
            instance = new Game();
        return instance;
    }

    // Starts the game
    // The while(true) statements are used for exception treatment,
    // so we don't exit the program and wait for valid input from the player
    void run(int selectedInterface) throws InterruptedException, InformationIncompleteException {
        String resourceName1 = "/accounts.json";
        accounts = new ArrayList<>();
        InputStream is1 = Test.class.getResourceAsStream(resourceName1);
        if (is1 == null) {
            throw new NullPointerException("Cannot find resource file " + resourceName1);
        }
        JSONTokener tokenizer1 = new JSONTokener(is1);
        JSONObject JSON_accounts = new JSONObject(tokenizer1);
        JSONArray array = JSON_accounts.getJSONArray("accounts");

        // Save JSON File information in the accounts list of Game
        for (Object o : array) {
            JSONObject account = (JSONObject) o;
            JSONObject JSON_credentials = (JSONObject) account.get("credentials");
            Credentials credentials = new Credentials(JSON_credentials.getString("email"),
                    JSON_credentials.getString("password"));

            JSONArray favoriteGames = account.getJSONArray("favorite_games");
            List<String> fav_games = new ArrayList<>();
            for (int i = 0; i < favoriteGames.length(); i++)
                fav_games.add((String) favoriteGames.get(i));

            JSONArray chars = account.getJSONArray("characters");
            List<Character> characters = new ArrayList<>();
            CharacterFactory factory = new CharacterFactory();
            for (Object aux : chars) {
                JSONObject character = (JSONObject) aux;
                // Factory Pattern for Character
                characters.add(factory.createCharacter(character.getString("profession"),
                        character.getString("name"), character.getInt("level"),
                        character.getInt("experience"),0,0));
            }
            accounts.add(new Account(credentials,fav_games,account.getString("name"),
                    account.getString("country"),characters,account.getInt("maps_completed")));
        }

        // We also save the stories for each cell in the stories HashMap from the stories.json file
        String resourceName2 = "/stories.json";
        stories = new HashMap<>();
        InputStream is2 = Test.class.getResourceAsStream(resourceName2);
        if (is2 == null) {
            throw new NullPointerException("Cannot find resource file " + resourceName2);
        }
        JSONTokener tokenizer2 = new JSONTokener(is2);
        JSONObject JSON_stories = new JSONObject(tokenizer2);
        JSONArray stories_array = JSON_stories.getJSONArray("stories");

        List<String> empty_string =  new ArrayList<>();
        List<String> enemy_string =  new ArrayList<>();
        List<String> shop_string =  new ArrayList<>();
        List<String> final_string =  new ArrayList<>();
        String last_type = "";
        for(Object o : stories_array){
            JSONObject story = (JSONObject) o;

            if(story.getString("type").equals("EMPTY"))
                empty_string.add(story.getString("value"));
            else if(story.getString("type").equals("ENEMY")){
                enemy_string.add(story.getString("value"));
                if(last_type.equals("EMPTY"))
                    stories.put(Cell.type.N, empty_string);
            }
            else if(story.getString("type").equals("SHOP")){
                shop_string.add(story.getString("value"));
                if(last_type.equals("ENEMY"))
                    stories.put(Cell.type.E, enemy_string);
            }
            else if(story.getString("type").equals("FINISH")){
                final_string.add(story.getString("value"));
                if(last_type.equals("SHOP"))
                    stories.put(Cell.type.S, shop_string);
            }
            last_type = story.getString("type");
        }
        stories.put(Cell.type.F, final_string);
        // Reading from JSON files stops here

        // Command Line
        if(selectedInterface == 1) {
            // The email for each account is printed so the player can choose an account
            Account selected_account;
            Scanner scanner = new Scanner(System.in);
            int k;
            System.out.println("\nAvailable accounts:");
            while (true) {
                int index;
                k = 1;
                for (Account account : accounts) {
                    System.out.println(k + ". " + "Email: " + account.info.getCredentials().getEmail());
                    k++;
                }
                System.out.println("\nChoose an account by typing the number and pressing ENTER:");
                try {
                    index = scanner.nextInt();
                    if (index > accounts.size() || index < 1) {
                        try {
                            throw new InvalidCommandException("Invalid Command!");
                        } catch (InvalidCommandException e) {
                            System.out.println("\n" + e.getMessage());
                            System.out.println("Type a valid command");
                        }
                    } else {
                        selected_account = accounts.get(index - 1);
                        break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("\nNot a number!");
                    System.out.println("Type a valid command");
                }
                scanner.nextLine();
            }
            System.out.println("\nYour selected account is " + selected_account.info.getCredentials().getEmail());
            scanner.nextLine();

            // Password is required to log into an account
            // This stage can be skipped for testing purposes by entering Testing
            while (true) {
                System.out.println("Please type the password and press ENTER (or type Testing to skip this step):");
                String password = scanner.nextLine();
                if (password.equals(selected_account.info.getCredentials().getPassword()) || password.equals("Testing"))
                    break;
                else
                    System.out.println("Wrong password, try again.");
            }
            // Player is now logged in and has to choose one of the available characters
            System.out.println("\nWelcome " + selected_account.info.getName() + "!\n" +
                    "You have " + selected_account.maps_completed + " maps completed.");
            TimeUnit.SECONDS.sleep(2);
            int choice;
            while (true) {
                System.out.println("\nSelect a Character:");
                k = 1;
                for (Character available_characters : selected_account.chars) {
                    System.out.println(k + ". " + available_characters.getClass().getSimpleName());
                    System.out.println("    - Name: " + available_characters.name);
                    System.out.println("    - Level: " + available_characters.level);
                    System.out.println("    - Experience: " + available_characters.exp);
                    k++;
                }
                System.out.println("Choose a character by typing the number and pressing ENTER:");
                try {
                    choice = scanner.nextInt();
                    if (choice > selected_account.chars.size() || choice < 1) {
                        try {
                            throw new InvalidCommandException("Invalid Command!");
                        } catch (InvalidCommandException e) {
                            System.out.println("\n" + e.getMessage());
                            System.out.println("Type a valid command");
                        }
                    } else {
                        System.out.println("\nSelected Character: "
                                + selected_account.chars.get(choice - 1).getClass().getSimpleName() + "\nLoading game...");
                        break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("\nNot a number!");
                    System.out.println("Type a valid command");
                }
                scanner.nextLine();
            }
            // Now that we have the current character selected we can start the game
            Character current_character = selected_account.chars.get(choice - 1);

            // We create the map of the game with the requested dimensions and the reference to the current character
            Grid map = Grid.grid_gen(5, 5, current_character);
            //Grid map = Grid.rand_grid_gen(current_character);
            Cell aux;
            CellElement previous;
            // For every move we print the current player HP, MP, Gold and (Inventory) Capacity

            // Also, for every move we show the available options of the current cell, and for every loop we print
            // the current cell's story

            // We use a string to print the map and a previous CellElement to determine if the last move was on the
            // Finish Cell, so we can exit the loop
            do {
                previous = map.current_cell.cellType;
                System.out.println("--------------------------------------------------------------------------");
                System.out.println("\nHP: " + map.player.currentHealth + "   MP: " + map.player.currentMana +
                        "   Gold: " + map.player.inventory.gold + "  Capacity: " + map.player.inventory.returnQuantity());
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < map.width; i++) {
                    for (int j = 0; j < map.height; j++) {
                        aux = map.get(i).get(j);
                        if (aux.visited) {
                            if (aux == map.current_cell && !(aux.cellType instanceof Empty))
                                s.append("P").append(aux.cellType.toCharacter()).append(" ");
                            else if (aux == map.current_cell)
                                s.append("P" + " ");
                            else
                                s.append(aux.cellType.toCharacter()).append(" ");
                        } else
                            s.append('?' + " ");
                    }
                    s.append('\n');
                }
                map.player.x = map.current_cell.x;
                map.player.y = map.current_cell.y;
                System.out.println(s);
                if(map.current_cell.cellType.toCharacter() == 'E') {
                    Enemy enemy = (Enemy) map.current_cell.cellType;
                    if(enemy.currentHealth > 0) {
                        String story = showStory(map.current_cell);
                        if (story != null)
                            System.out.println(story);
                    }
                }
                showOptions(map);
                if (map.player.currentHealth <= 0) {
                    System.out.println("Better luck next time!");
                    break;
                }
            } while (!previous.getClass().equals(Final.class));
            selected_account.maps_completed++;
        }
        // Graphic Interface
        else {
            new GameFrame(this);
        }
    }

    void showOptions(Grid map) throws InterruptedException {
        // If the current cell is an empty cell we display move possibilities and check for use input to be correct
        if(map.current_cell.cellType.toCharacter() == 'N'){
            Scanner command = new Scanner(System.in);
            while (true) {
                System.out.println("Proceed to the next step by typing P and pressing ENTER");
                String proceed = command.nextLine();
                if (proceed.equalsIgnoreCase("P")) {
                    while (true) {
                        int direction;
                        System.out.println("""
                                Choose where to go:
                                1. Go North
                                2. Go South
                                3. Go East
                                4. Go West""");
                        System.out.println("\nType the number and press ENTER below:");
                        try {
                            direction = command.nextInt();
                            if (direction == 1) {
                                map.goNorth();
                                break;
                            } else if (direction == 2) {
                                map.goSouth();
                                break;
                            } else if (direction == 3) {
                                map.goEast();
                                break;
                            } else if (direction == 4) {
                                map.goWest();
                                break;
                            } else {
                                try {
                                    throw new InvalidCommandException("Invalid Command!");
                                } catch (InvalidCommandException e) {
                                    System.out.println("\n" + e.getMessage());
                                    System.out.println("Type a valid command");
                                }
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("\nNot a number!");
                            System.out.println("Type a valid command");
                        }
                        command.nextLine();
                    }
                    break;
                }
                else {
                    try {
                        throw new InvalidCommandException("Invalid Command!");
                    } catch (InvalidCommandException e) {
                        System.out.println("\n" + e.getMessage());
                        System.out.println("Type a valid command");
                    }
                }
            }
        }
        // If the current cell is a Shop cell
        else if(map.current_cell.cellType.toCharacter() == 'S'){
            Scanner command = new Scanner(System.in);
            while(true) {
                // We check if the player wants to visit the shop, and if not, he will have to choose where to move
                int choice;
                System.out.println("""
                        Look at the items in the shop ?
                        1. Yes
                        2. No""");
                try {
                    choice = command.nextInt();
                    if (choice == 1) {
                        Shop shop = (Shop) map.current_cell.cellType;
                        do {
                            // If the shop is empty the player leaves
                            if (shop.potionList.size() == 0) {
                                System.out.println("The shop is empty");
                                break;
                            }
                            int k = 1;
                            // Display current gold and all the potions from the shop with the price regen value and the
                            // weight so the player can choose if he wants to buy a potion and can afford one
                            // (weight + gold)
                            System.out.println("\nCurrent gold: " + map.player.inventory.gold);
                            for (Potion pot : shop.potionList) {
                                System.out.println(k + ". " + pot.getClass().getSimpleName() + " Price: "
                                        + pot.getPrice() + " RegenValue: " + pot.getRegen() + " Weight: "
                                        + pot.getWeight());
                                k++;
                            }
                            System.out.println("""
                                Buy a potion ?
                                1. Yes
                                2. No""");
                            try {
                                choice = command.nextInt();
                                if (choice == 1) {
                                    Potion selected_potion;
                                    int var = map.player.inventory.gold;
                                    while (true) {
                                        System.out.println("Select the number of your desired potion and press ENTER:");
                                        int number;
                                        try {
                                            number = command.nextInt();
                                            if (number > shop.potionList.size() || number < 1) {
                                                try {
                                                    throw new InvalidCommandException("Invalid Command!");
                                                } catch (InvalidCommandException e) {
                                                    System.out.println("\n" + e.getMessage());
                                                    System.out.println("Type a valid command");
                                                }
                                            } else {
                                                selected_potion = shop.selectPotion(number - 1);
                                                break;
                                            }
                                        } catch (InputMismatchException e) {
                                            System.out.println("\nNot a number!");
                                            System.out.println("Type a valid command");
                                        }
                                        command.nextLine();
                                    }
                                    map.player.buyPotion(selected_potion);
                                    if (var == map.player.inventory.gold)
                                        shop.potionList.add(selected_potion);
                                    // Option to buy another potion
                                    System.out.println("""
                                    Continue shopping ?
                                    1. Yes
                                    2. No""");
                                    while (true) {
                                        try {
                                            choice = command.nextInt();
                                            if(choice != 1 && choice != 2 ) {
                                                try {
                                                    throw new InvalidCommandException("Invalid Command!");
                                                } catch (InvalidCommandException e) {
                                                    System.out.println("\n" + e.getMessage());
                                                    System.out.println("Type a valid command");
                                                }
                                            }
                                            else
                                                break;
                                        } catch (InputMismatchException e) {
                                            System.out.println("\nNot a number!");
                                            System.out.println("Type a valid command");
                                        }
                                        command.nextLine();
                                    }
                                }
                                if(choice == 2)
                                    break;
                                else {
                                    try {
                                        throw new InvalidCommandException("Invalid Command!");
                                    } catch (InvalidCommandException e) {
                                        System.out.println("\n" + e.getMessage());
                                        System.out.println("Type a valid command");
                                    }
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("\nNot a number!");
                                System.out.println("Type a valid command");
                            }
                            command.nextLine();
                        } while (true);
                    }
                    if (choice != 2) {
                        try {
                            throw new InvalidCommandException("Invalid Command!");
                        } catch (InvalidCommandException e) {
                            System.out.println("\n" + e.getMessage());
                            System.out.println("Type a valid command");
                        }
                    } else {
                        command.nextLine();
                        break;
                    }
                } catch (InputMismatchException e) {
                    System.out.println("\nNot a number!");
                    System.out.println("Type a valid command");
                }
                command.nextLine();
            }
            // Choose direction after leaving the shop
            StringBuilder s = new StringBuilder();
            Cell aux;
            for (int i=0 ; i< map.width; i++) {
                for (int j = 0; j < map.height; j++) {
                    aux = map.get(i).get(j);
                    if (aux.visited) {
                        if(aux == map.current_cell && !(aux.cellType instanceof Empty))
                            s.append("P").append(aux.cellType.toCharacter()).append(" ");
                        else if(aux == map.current_cell)
                            s.append("P" + " ");
                        else
                            s.append(aux.cellType.toCharacter()).append(" ");
                    }
                    else
                        s.append('?' + " ");
                }
                s.append('\n');
            }
            System.out.println(s);
            while (true) {
                System.out.println("Proceed to the next step by typing P and pressing ENTER");
                String proceed = command.nextLine();
                if (proceed.equalsIgnoreCase("P")) {
                    while (true) {
                        int direction;
                        System.out.println("""
                                Choose where to go:
                                1. Go North
                                2. Go South
                                3. Go East
                                4. Go West""");
                        System.out.println("\nType the number and press ENTER below:");
                        try {
                            direction = command.nextInt();
                            if (direction == 1) {
                                map.goNorth();
                                break;
                            } else if (direction == 2) {
                                map.goSouth();
                                break;
                            } else if (direction == 3) {
                                map.goEast();
                                break;
                            } else if (direction == 4) {
                                map.goWest();
                                break;
                            } else {
                                try {
                                    throw new InvalidCommandException("Invalid Command!");
                                } catch (InvalidCommandException e) {
                                    System.out.println("\n" + e.getMessage());
                                    System.out.println("Type a valid command");
                                }
                            }
                        } catch (InputMismatchException e) {
                            System.out.println("\nNot a number!");
                            System.out.println("Type a valid command");
                        }
                        command.nextLine();
                    }
                    break;
                }
                else {
                    try {
                        throw new InvalidCommandException("Invalid Command!");
                    } catch (InvalidCommandException e) {
                        System.out.println("\n" + e.getMessage());
                        System.out.println("Type a valid command");
                    }
                }
            }
        }
        // If the current cell is an Enemy cell
        else if(map.current_cell.cellType.toCharacter() == 'E'){
            Enemy enemy = (Enemy) map.current_cell.cellType;
            if(enemy.currentHealth < 0) {
                System.out.println("The enemy is dead!");
                Scanner command = new Scanner(System.in);
                while (true) {
                    System.out.println("Proceed to the next step by typing P and pressing ENTER");
                    String proceed = command.nextLine();
                    if (proceed.equalsIgnoreCase("P")) {
                        while (true) {
                            int direction;
                            System.out.println("""
                                Choose where to go:
                                1. Go North
                                2. Go South
                                3. Go East
                                4. Go West""");
                            System.out.println("\nType the number and press ENTER below:");
                            try {
                                direction = command.nextInt();
                                if (direction == 1) {
                                    map.goNorth();
                                    break;
                                } else if (direction == 2) {
                                    map.goSouth();
                                    break;
                                } else if (direction == 3) {
                                    map.goEast();
                                    break;
                                } else if (direction == 4) {
                                    map.goWest();
                                    break;
                                } else {
                                    try {
                                        throw new InvalidCommandException("Invalid Command!");
                                    } catch (InvalidCommandException e) {
                                        System.out.println("\n" + e.getMessage());
                                        System.out.println("Type a valid command");
                                    }
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("\nNot a number!");
                                System.out.println("Type a valid command");
                            }
                            command.nextLine();
                        }
                        break;
                    }
                    else {
                        try {
                            throw new InvalidCommandException("Invalid Command!");
                        } catch (InvalidCommandException e) {
                            System.out.println("\n" + e.getMessage());
                            System.out.println("Type a valid command");
                        }
                    }
                }
            }
            else {
                System.out.println("Engaging combat...");
                boolean my_turn = true;
                Scanner command = new Scanner(System.in);
                // Turn based combat with the player going first
                // We display both player's and enemy's mana and health every turn so the player can choose what to do next
                while (true) {
                    System.out.println("\n---------------------------------------------------\nNew combat turn\nYour HP: "
                            + map.player.currentHealth + "        Enemy's HP: " + enemy.currentHealth + "\nYour MP: "
                            + map.player.currentMana + "        Enemy's MP: " + enemy.currentMana);
                    if (my_turn) {
                        while (true) {
                            // The player has 3 options from which he can choose
                            System.out.println("""
                                    Your turn!
                                    What to do...:
                                    1. Use basic attack
                                    2. Use ability
                                    3. Use potion
                                    Choose an action by typing the number and pressing ENTER:""");
                            try {
                                int choice = command.nextInt();
                                // A basic attack
                                if (choice == 1) {
                                    enemy.receiveDamage(map.player.getDamage());
                                    break;
                                }
                                // A spell attack
                                // Checks if there are any spells left and if there are it displays them for the player
                                // It uses the selected ability on the enemy
                                else if (choice == 2) {
                                    if (map.player.abilities.size() == 0)
                                        System.out.println("No spells left!");
                                    else {
                                        while (true) {
                                            int k = 1;
                                            System.out.println("\nSpells:");
                                            for (Spell spell : map.player.abilities) {
                                                System.out.println(k + ". " + spell.getClass().getSimpleName()
                                                        + "   Damage: " + spell.damage + "    Mana Cost: "
                                                        + spell.manaCost);
                                                k++;
                                            }
                                            System.out.println("Choose a spell by typing the number and pressing ENTER:");
                                            try {
                                                choice = command.nextInt();
                                                if (choice > map.player.abilities.size() || choice < 1) {
                                                    try {
                                                        throw new InvalidCommandException("Invalid Command!");
                                                    } catch (InvalidCommandException e) {
                                                        System.out.println("\n" + e.getMessage());
                                                        System.out.println("Type a valid command");
                                                    }
                                                } else {
                                                    map.player.useAbility(map.player.abilities.get(choice - 1), enemy);
                                                    break;
                                                }
                                            } catch (InputMismatchException e) {
                                                System.out.println("\nNot a number!");
                                                System.out.println("Type a valid command");
                                            }
                                            command.nextLine();
                                        }
                                        break;
                                    }
                                }
                                // Potion usage
                                // Checks if there are any potions left, and if there are it displays them for the player
                                // It uses the selected potion on the player
                                else if (choice == 3) {
                                    if (map.player.inventory.potions.size() == 0)
                                        System.out.println("Potion inventory is empty!");
                                    else {
                                        while (true) {
                                            int k = 1;
                                            System.out.println("\nPotions:");
                                            for (Potion potion : map.player.inventory.potions) {
                                                System.out.println(k + ". " + potion.getClass().getSimpleName() +
                                                        "   RegenValue:" + potion.getRegen());
                                                k++;
                                            }
                                            System.out.println("Choose a potion by typing the number and pressing ENTER:");
                                            try {
                                                choice = command.nextInt();
                                                if (choice > map.player.inventory.potions.size() || choice < 1) {
                                                    try {
                                                        throw new InvalidCommandException("Invalid Command!");
                                                    } catch (InvalidCommandException e) {
                                                        System.out.println("\n" + e.getMessage());
                                                        System.out.println("Type a valid command");
                                                    }
                                                } else {
                                                    Potion current_potion = map.player.inventory.removePotion(choice - 1);
                                                    current_potion.usePotion(map.player);
                                                    break;
                                                }
                                            } catch (InputMismatchException e) {
                                                System.out.println("\nNot a number!");
                                                System.out.println("Type a valid command");
                                            }
                                            command.nextLine();
                                        }
                                        break;
                                    }
                                } else {
                                    try {
                                        throw new InvalidCommandException("Invalid Command!");
                                    } catch (InvalidCommandException e) {
                                        System.out.println("\n" + e.getMessage());
                                        System.out.println("Type a valid command");
                                    }
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("\nNot a number!");
                                System.out.println("Type a valid command");
                            }
                            command.nextLine();
                        }
                    } else {
                        // Enemy turn
                        // 50/50 chance to use a basic attack or use a spell
                        // If there are no spells left or no mana the enemy will use only basic attacks
                        System.out.println("Enemies turn!");
                        TimeUnit.SECONDS.sleep(1);
                        if (Math.random() < 0.5 && enemy.abilities.size() != 0) {
                            Random randomAbility = new Random();
                            Spell enemy_spell = enemy.abilities.get(randomAbility.nextInt(enemy.abilities.size()));
                            if (enemy.currentMana >= enemy_spell.manaCost)
                                enemy.useAbility(enemy_spell, map.player);
                            else
                                map.player.receiveDamage(enemy.getDamage());
                        } else
                            map.player.receiveDamage(enemy.getDamage());
                        // A timer for gameplay flavor so the player can analyze easily what the enemy did
                        if (map.player.currentHealth > 0)
                            TimeUnit.SECONDS.sleep(5);
                    }
                    // If the player or enemy dies we exit the combat
                    if (map.player.currentHealth <= 0)
                        break;

                    // If the enemy is defeated the player gains exp and gold
                    if (enemy.currentHealth <= 0) {
                        map.player.gainExperience(enemy.maxHealth / 3);
                        map.player.inventory.gold += enemy.maxHealth / 4;
                        defeatedEnemies++;
                        earnedGold += enemy.maxHealth / 4;
                        earnedExperience += enemy.maxHealth / 3;
                        break;
                    }
                    my_turn = !my_turn;
                }
            }
        }
        // If the current cell is a Finish cell
        // No options available, but it displays the text in case of a win
        else if(map.current_cell.cellType.toCharacter() == 'F'){
            System.out.println("Map completed...\n"+ "Enemies defeated: " + defeatedEnemies
                    + "\nEarned Experience: " + earnedExperience
                    + "\nEarned Gold: " + earnedGold
                    + "\nCurrent Level: " + map.player.level);
            System.out.println("You won!");
        }
    }

    // Method that displays a random story for the current cell depending on the cell type and removes it from the list
    String showStory(Cell current){
        if(!current.wasVisited ){
            List<String> aux;
            if(current.cellType.toCharacter() == 'E')
                aux = stories.get(Cell.type.E);
            else if(current.cellType.toCharacter() == 'S')
                aux = stories.get(Cell.type.S);
            else if(current.cellType.toCharacter() == 'F')
                aux = stories.get(Cell.type.F);
            else
                aux = stories.get(Cell.type.N);
            System.out.println(aux.size());
            String story = aux.get((int)(Math.random()*(aux.size())));
            aux.remove(story);
            return story;
        }
        return null;
    }
}
