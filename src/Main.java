public class Main {

    public static void main(String[] args) {

        ConsoleGame consoleGame = new ConsoleGame();

        Table table = consoleGame.setupGame();

        Deck deck = new Deck();
        Game game = new Game(deck, table);

        consoleGame.playGame(game, table);
    }
}