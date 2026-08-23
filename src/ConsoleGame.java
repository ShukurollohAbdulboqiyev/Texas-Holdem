import java.util.Scanner;

public class ConsoleGame {

    private final Scanner scanner = new Scanner(System.in);
    private final GameSetup gameSetup = new GameSetup();

    public Table setupGame() {

        Table table = gameSetup.createTable();

        System.out.print("How many players are playing?: ");
        int playerCount = scanner.nextInt();
        scanner.nextLine();

        for (int i = 0; i < playerCount; i++) {

            System.out.print("What is your name?: ");
            String name = scanner.nextLine();

            System.out.print("How much money do you want to bring? $: ");
            double chips = scanner.nextDouble();
            scanner.nextLine();

            Player player = gameSetup.createPlayer(name, chips);
            gameSetup.addPlayer(table, player);
        }

        return table;
    }

    public void showGameState(Table table) {

        for (Player player : table.getPlayers()) {
            System.out.println(
                    player.getName() + " $" +
                            player.getChips() + " " +
                            player.getHoleCards()
            );
        }
    }

    public void processPlayerActions(Game game, Table table) {

        for (Player player : table.getPlayers()) {

            boolean valid;

            do {
                System.out.println("\n1. Bet");
                System.out.println("2. Raise");
                System.out.println("3. Call");
                System.out.println("4. Check");
                System.out.println("5. Fold");

                System.out.print(
                        player.getName() + ", what would you like to do?: "
                );

                int choice = scanner.nextInt();

                Action action = switch (choice) {
                    case 1 -> Action.BET;
                    case 2 -> Action.RAISE;
                    case 3 -> Action.CALL;
                    case 4 -> Action.CHECK;
                    case 5 -> Action.FOLD;
                    default -> throw new IllegalArgumentException("Invalid action");
                };

                double amount = 0;

                if (action == Action.BET) {
                    System.out.print("How much do you want to bet?: ");
                    amount = scanner.nextDouble();

                } else if (action == Action.RAISE) {
                    System.out.print(player.getName() + ", raise to: ");
                    amount = scanner.nextDouble();
                } else if (action == Action.CHECK) {
                    System.out.println(player.getName() + ", checked.");
                }
                else if(action == Action.FOLD){
                    System.out.println(player.getName() + " folded.");
                }

                valid = game.processAction(player, action, amount);

                if (!valid) {
                    System.out.println("Invalid action. Please try again.");
                }

            } while (!valid);
        }
    }
}