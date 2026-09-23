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
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Wrong Input. Try Again");
            }
        } while (playerCount < MIN_PLAYERS || playerCount > MAX_PLAYERS);

        for (int i = 0; i < playerCount; i++) {
            System.out.print("What is your name?: ");
            String name = scanner.nextLine();

            chips = 0.0;

            do {
                try {
                    System.out.print("How much money do you want to bring? $: ");
                    chips = scanner.nextDouble();
                    scanner.nextLine();

                    if (chips < MIN_CHIPS) {
                        System.out.println("The minimum buy-in is $" + MIN_CHIPS);
                    } else if (chips > MAX_CHIPS) {
                        System.out.println("The maximum buy-in is $" + MAX_CHIPS);
                    }
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    chips = 0.0;
                    System.out.println("The amount should be in numeric numbers");
                }
            } while (chips < MIN_CHIPS || chips > MAX_CHIPS);

            Player player = gameSetup.createPlayer(name, chips);
            gameSetup.addPlayer(table, player);
        }

        return table;
    }

    public void showPlayerState(Table table, Player player) {
        System.out.println("\n--- " + player.getName() + "'S TURN ---");
        System.out.println("Your cards: " + player.getHoleCards());
        System.out.println("Your position: " + player.getPosition());
        System.out.println("Your chips: $" + player.getChips());
        System.out.println("Your current bet: $" + player.getCurrentBet());
        System.out.println("Pot: $" + table.getPot());
        System.out.println("Highest bet: $" + table.getHighestBet());
        System.out.println("Community cards: " + table.getCommunityCards());
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

        game.playBettingRound(GameStage.SHOWDOWN);
        game.showdown();
    }

    public void processPlayerAction(Game game, Player player) {
        boolean valid = false;
        int choice;

        do {
            boolean canBet = game.canBet(player);
            boolean canRaise = game.canRaise(player);
            boolean canCall = game.canCall(player);
            boolean canCheck = game.canCheck(player);
            boolean canAllIn = player.getChips() > 0;

            int option = 1;

            System.out.println();

            if (canBet) {
                System.out.println(option + ". Bet");
                option++;
            }

            if (canRaise) {
                System.out.println(option + ". Raise");
                option++;
            }

            if (canCall) {
                System.out.println(option + ". Call");
                option++;
            }

            if (canCheck) {
                System.out.println(option + ". Check");
                option++;
            }

            System.out.println(option + ". Fold");
            option++;

            if (canAllIn) {
                System.out.println(option + ". All-In");
                option++;
            }

            int maxOption = option - 1;

            System.out.print(player.getName() + ", what would you like to do?: ");

            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.println("Wrong Input. Try Again.");
                continue;
            }

            if (choice < 1 || choice > maxOption) {
                System.out.println("Invalid choice. Choose one of the available actions.");
                continue;
            }

            option = 1;
            Action action = null;

            if (canBet && choice == option) {
                action = Action.BET;
            }

            if (canBet) {
                option++;
            }

            if (canRaise && action == null && choice == option) {
                action = Action.RAISE;
            }

            if (canRaise) {
                option++;
            }

            if (canCall && action == null && choice == option) {
                action = Action.CALL;
            }

            if (canCall) {
                option++;
            }

            if (canCheck && action == null && choice == option) {
                action = Action.CHECK;
            }

            if (canCheck) {
                option++;
            }

            if (action == null && choice == option) {
                action = Action.FOLD;
            }

            option++;

            if (canAllIn && action == null && choice == option) {
                action = Action.ALL_IN;
            }

            double amount = 0;

            if (action == Action.BET) {
                try {
                    System.out.print("How much do you want to bet?: ");
                    amount = scanner.nextDouble();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    System.out.println("The amount should be in numeric numbers.");
                    continue;
                }
            } else if (action == Action.RAISE) {
                try {
                    System.out.print(player.getName() + ", raise to: ");
                    amount = scanner.nextDouble();
                } catch (InputMismatchException e) {
                    scanner.nextLine();
                    System.out.println("The amount should be in numeric numbers.");
                    continue;
                }
            }

            valid = game.processAction(player, action, amount);

            if (!valid) {
                System.out.println("Invalid action. Please try again.");
            } else if (action == Action.CHECK) {
                System.out.println(player.getName() + " checked.");
            } else if (action == Action.CALL) {
                System.out.println(player.getName() + " called.");
            } else if (action == Action.FOLD) {
                System.out.println(player.getName() + " folded.");
            } else if (action == Action.ALL_IN) {
                System.out.println(player.getName() + " is ALL-IN!");
            } else if (action == Action.BET) {
                System.out.println(player.getName() + " bet $" + amount + ".");
            } else if (action == Action.RAISE) {
                System.out.println(player.getName() + " raised to $" + amount + ".");
            }

        } while (!valid);
    }

    public void runBettingRound(Game game, Table table) {
        while (!game.isBettingRoundFinished()) {
            Player currentPlayer = game.getCurrentPlayer();

            if (currentPlayer == null) {
                break;
            }

            showPlayerState(table, currentPlayer);
            processPlayerAction(game, currentPlayer);
            game.moveToNextPlayer();
        }
    }
}