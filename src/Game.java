public class Game {

    private final Deck deck;
    private final Table table;

    private int dealerIndex;
    private Player currentPlayer;

    private double smallBlind = 10.0;
    private double bigBlind = 2 * smallBlind;

    private int playersActed;

    public Game(Deck deck, Table table) {
        this.deck = deck;
        this.table = table;
    }

    public void startGame() {
        deck.shuffle();

        // Getting hole cards to the players
        for (int round = 0; round < 2; round++) {
            for (Player player : table.getPlayers()) {
                player.addCard(deck.draw());
            }
        }
    }

    public boolean processAction(Player player, Action action, double amount) {
        if (player.isFolded()) {
            return false;
        }

        switch (action) {

            case BET -> {
                if (table.getHighestBet() > 0) {
                    return false;
                }

                if (validateAmount(amount) || amount > player.getChips()) {
                    return false;
                }

                player.placeBet(amount);
                table.addToPot(amount);
                table.updateHighestBet(player);

                playersActed++;

                return true;
            }

            case RAISE -> {
                if (amount <= table.getHighestBet()) {
                    return false;
                }

                double additionalAmount = amount - player.getCurrentBet();

                if (validateAmount(additionalAmount) || additionalAmount > player.getChips()) {
                    return false;
                }

                player.placeBet(additionalAmount);
                table.addToPot(additionalAmount);
                table.updateHighestBet(player);

                playersActed = 1;

                return true;
            }

            case CALL -> {
                double additionalAmount = table.getHighestBet() - player.getCurrentBet();

                if (additionalAmount < 0) {
                    return false;
                }

                if (additionalAmount > player.getChips()) {
                    return false;
                }

                player.placeBet(additionalAmount);
                table.addToPot(additionalAmount);

                playersActed++;

                return true;
            }

            case CHECK -> {
                if (player.getCurrentBet() != table.getHighestBet()) {
                    return false;
                }

                playersActed++;

                return true;
            }

            case FOLD -> {
                player.setFolded(true);

                playersActed++;

                return true;
            }

            default -> throw new IllegalStateException("Unexpected value: " + action);
        }
    }

    public boolean validateAmount(double amount) {
        return amount > 0;
    }

    public boolean assignPosition() {
        int playerCount = table.getPlayers().size();

        if (playerCount < 2) {
            return false;
        }

        int bigBlindIndex;
        int smallBlindIndex;

        if (playerCount == 2) {
            smallBlindIndex = dealerIndex;
            bigBlindIndex = (dealerIndex + 1) % playerCount;
        } else {
            smallBlindIndex = (dealerIndex + 1) % playerCount;
            bigBlindIndex = (dealerIndex + 2) % playerCount;
        }

        for (int i = 0; i < playerCount; i++) {
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

    public void playBettingRound(GameStage gameStage) {
        switch (gameStage) {

            case PRE_FLOP -> {
                // No BB selected yet
                int bigBlindIndex = -1;

                playersActed = 0;

                for (int i = 0; i < table.getPlayers().size(); i++) {
                    Player player = table.getPlayers().get(i);

                    if (player.getPosition() == Position.SMALL_BLIND) {
                        player.placeBet(smallBlind);
                        table.addToPot(smallBlind);
                        table.updateHighestBet(player);
                    } else if (player.getPosition() == Position.BIG_BLIND) {
                        player.placeBet(bigBlind);
                        table.addToPot(bigBlind);
                        table.updateHighestBet(player);

                        bigBlindIndex = i;
                    }
                }

                if (bigBlindIndex == -1) {
                    return;
                }

                int nextIndex = (bigBlindIndex + 1) % table.getPlayers().size();
                currentPlayer = table.getPlayers().get(nextIndex);
            }
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void moveToNextPlayer() {
        int currentIndex = table.getPlayers().indexOf(currentPlayer);
        int nextIndex;

        do {
            nextIndex = (currentIndex + 1) % table.getPlayers().size();
            currentIndex = nextIndex;
        } while (table.getPlayers().get(nextIndex).isFolded());

        currentPlayer = table.getPlayers().get(nextIndex);
    }

    public boolean isBettingRoundFinished() {
        int activePlayers = 0;

        for (Player player : table.getPlayers()) {
            if (!player.isFolded()) {
                activePlayers++;
            }
        }

        if (activePlayers <= 1) {
            return true;
        }

        if (playersActed < activePlayers) {
            return false;
        }

        for (Player player : table.getPlayers()) {
            if (!player.isFolded() && player.getCurrentBet() != table.getHighestBet()) {
                return false;
            }
        }

        return true;
    }
}