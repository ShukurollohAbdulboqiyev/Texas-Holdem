public class Game {

    private final Deck deck;
    private final Table table;

    public Game(Deck deck, Table table) {
        this.deck = deck;
        this.table = table;
    }

    public void startGame() {
        deck.shuffle();

        // Deal two hole cards to each player
        for (int round = 0; round < 2; round++) {
            for (Player player : table.getPlayers()) {
                player.addCard(deck.draw());
            }
        }
    }

    public void processAction(Player player, Action action, double amount) {

        switch (action) {

            case BET -> {
                player.placeBet(amount);
                table.addToPot(amount);
                table.updateHighestBet(player);
            }

            case RAISE -> {
                double additionalAmount = amount - player.getCurrentBet();

                player.placeBet(additionalAmount);
                table.addToPot(additionalAmount);
                table.updateHighestBet(player);
            }

            case CALL -> {
                // Noting here
            }

            case CHECK -> {
                // Nothing changes
            }

            case FOLD -> {
                // Folding logic will be added to Player
            }
        }
    }
}