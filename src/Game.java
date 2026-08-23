public class Game {

    private final Deck deck;
    private final Table table;
    private int dealerIndex;

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
                if (validateAmount(amount) || amount > player.getChips()) {
                    return false;
                }

                player.placeBet(amount);
                table.addToPot(amount);
                table.updateHighestBet(player);

                return true;
            }

            case RAISE -> {
                double additionalAmount = amount - player.getCurrentBet();

                if (validateAmount(additionalAmount)
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

            case CHECK ->{
                return table.getHighestBet() == player.getCurrentBet();
            }

            case FOLD -> {
                player.setFolded(true);
                return true;
            }

            default -> throw new IllegalStateException(
                    "Unexpected value: " + action
            );
        }
    }

    public boolean validateAmount(double amount) {
        return !(amount > 0);
    }

    public boolean assignPosition(Position position){

        int playerCount = table.getPlayers().size();

        int bigBlindIndex;
        int smallBlindIndex;

        if(playerCount == 2){
            smallBlindIndex = dealerIndex;
            bigBlindIndex = (dealerIndex + 1) % playerCount;
        }
        else {
            smallBlindIndex = (dealerIndex + 1) % playerCount;
            bigBlindIndex = (dealerIndex + 2) % playerCount;
        }

        for(int i = 0; i< playerCount; i++){
            Player player = table.getPlayers().get(i);

            if (i == dealerIndex) {
                player.setPosition(Position.DEALER);

            } else if (i == smallBlindIndex) {
                player.setPosition(Position.SMALL_BLIND);

            } else if (i == bigBlindIndex) {
                player.setPosition(Position.BIG_BLIND);
            }
        }

        return true;
    }

    public void playBettingRound(GameStage gameStage){

        switch (gameStage){
            case PRE_FLOP -> {

            }
        }
    }
}