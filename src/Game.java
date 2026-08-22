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

    public boolean processAction(Player player, Action action, double amount) {

        switch (action) {

            case BET -> {
                if (!validateAmount(amount) || amount > player.getChips()) {
                    return false;
                }

                player.placeBet(amount);
                table.addToPot(amount);
                table.updateHighestBet(player);

                return true;
            }

            case RAISE -> {
                double additionalAmount = amount - player.getCurrentBet();

                if (!validateAmount(additionalAmount)
                        || additionalAmount > player.getChips()) {
                    return false;
                }

                player.placeBet(additionalAmount);
                table.addToPot(additionalAmount);
                table.updateHighestBet(player);

                return true;
            }

            case CALL -> {
                double additionalAmount =
                        table.getHighestBet() - player.getCurrentBet();

                if (additionalAmount > player.getChips()) {
                    return false;
                }

                player.placeBet(additionalAmount);
                table.addToPot(additionalAmount);

                return true;
            }

            case CHECK, FOLD -> {
                return true;
            }

            default -> throw new IllegalStateException(
                    "Unexpected value: " + action
            );
        }
    }

    public boolean validateAmount(double amount) {
        return amount > 0;
    }
}