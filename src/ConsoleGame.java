import java.util.InputMismatchException;
import java.util.Scanner;

public class ConsoleGame {

    private final Scanner scanner = new Scanner(System.in);
    private final GameSetup gameSetup = new GameSetup();

    public Table setupGame() {
        Table table = gameSetup.createTable();
        final int MAX_PLAYERS = 10;
        final int MIN_PLAYERS = 2;
        final double MIN_CHIPS = 10.0;
        final double MAX_CHIPS = 10000.0;
        int playerCount = 0;
        double chips = 0.0;

        do {
           try {
               System.out.print("How many players are playing?: ");
               playerCount = scanner.nextInt();
               scanner.nextLine();
           }catch (InputMismatchException e){
               scanner.nextLine();
               System.out.println("Wrong Input. Try Again");
           }

        }while(playerCount < MIN_PLAYERS || playerCount > MAX_PLAYERS);

        for (int i = 0; i < playerCount; i++) {
            System.out.print("What is your name?: ");
            String name = scanner.nextLine();

            do {
                try{
                    System.out.print("How much money do you want to bring? $: ");
                    chips = scanner.nextDouble();
                    scanner.nextLine();

                    if (chips < MIN_CHIPS) {
                        System.out.println("The minimum buy-in is $" + MIN_CHIPS);
                    } else if (chips > MAX_CHIPS) {
                        System.out.println("The maximum buy-in is $" + MAX_CHIPS);
                    }
                }catch (InputMismatchException e){
                    scanner.nextLine();
                    System.out.println("The amount should be in numeric numbers");
                }
            }while(chips < MIN_CHIPS || chips > MAX_CHIPS);

            Player player = gameSetup.createPlayer(name, chips);
            gameSetup.addPlayer(table, player);
        }

        return table;
    }

    public void showPlayerState(Table table, Player player) {

        System.out.println(
                "\n--- " + player.getName() + "'S TURN ---"
        );

        System.out.println(
                "Your cards: " + player.getHoleCards()
        );

        System.out.println(
                "Your position: " + player.getPosition()
        );

        System.out.println(
                "Your chips: $" + player.getChips()
        );

        System.out.println(
                "Your current bet: $" + player.getCurrentBet()
        );

        System.out.println(
                "Pot: $" + table.getPot()
        );

        System.out.println(
                "Highest bet: $" + table.getHighestBet()
        );

        System.out.println(
                "Community cards: " + table.getCommunityCards()
        );
    }

    public void playGame(Game game, Table table) {
        game.startGame();
        game.assignPosition();

        game.playBettingRound(GameStage.PRE_FLOP);
        runBettingRound(game, table);

        game.playBettingRound(GameStage.FLOP);
        runBettingRound(game, table);

        game.playBettingRound(GameStage.TURN);
        runBettingRound(game, table);

        game.playBettingRound(GameStage.RIVER);
        runBettingRound(game, table);
    }

    public void processPlayerAction(Game game, Player player) {

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
                default -> throw new IllegalArgumentException(
                        "Invalid action"
                );
            };

            double amount = 0;

            if (action == Action.BET) {

                System.out.print("How much do you want to bet?: ");
                amount = scanner.nextDouble();

            } else if (action == Action.RAISE) {

                System.out.print(
                        player.getName() + ", raise to: "
                );
                amount = scanner.nextDouble();
            }

            valid = game.processAction(player, action, amount);

            if (!valid) {

                System.out.println(
                        "Invalid action. Please try again."
                );

            } else {

                if (action == Action.CHECK) {
                    System.out.println(
                            player.getName() + " checked."
                    );

                } else if (action == Action.FOLD) {
                    System.out.println(
                            player.getName() + " folded."
                    );
                }
            }

        } while (!valid);
    }

    public void runBettingRound(Game game, Table table) {

        while (!game.isBettingRoundFinished()) {
            Player currentPlayer = game.getCurrentPlayer();

            showPlayerState(table, currentPlayer);

            processPlayerAction(game, currentPlayer);

            game.moveToNextPlayer();
        }
    }
}