import java.util.InputMismatchException;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) throws InterruptedException, InformationIncompleteException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to World of Marcel!");
        while(true) {
            int choice;
            System.out.println("""
            Select your desired interface by typing the number and pressing ENTER:
            1. Terminal interface
            2. Graphic interface""");
            System.out.println("\nType here: ");
            try {
                choice = scanner.nextInt();
                if (choice == 1 || choice == 2) {
                    Game game = Game.getInstance();
                    game.run(choice);
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
            scanner.nextLine();
        }
        scanner.close();
    }
}
