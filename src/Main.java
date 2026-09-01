public class Main {

    public static void main(String[] args) {

        ConsoleGame consoleGame = new ConsoleGame();

        // asking and creating/adding players and preparing the table.
        Table table = consoleGame.setupGame();

        Deck deck = new Deck();
        Game game = new Game(deck, table);

        consoleGame.playGame(game, table);
    }
}