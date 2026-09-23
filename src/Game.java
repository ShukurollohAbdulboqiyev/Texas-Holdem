import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {

    private final Deck deck;
    private final Table table;

    private int dealerIndex = 0;
    private Player currentPlayer;

    private final double smallBlind = 10.0;
    private final double bigBlind = 20.0;

    private double lastRaiseSize = bigBlind;

    private final Set<Player> actedPlayers = new HashSet<>();
    private final Set<Player> raiseRestrictedPlayers = new HashSet<>();

    public Game(Deck deck, Table table) {
        this.deck = deck;
        this.table = table;
    }

    public void startGame() {
        table.resetForNewHand();
        deck.shuffle();

        for (int round = 0; round < 2; round++) {
            for (Player player : table.getPlayers()) {
                player.addCard(deck.draw());
            }
        }
    }

    public boolean processAction(Player player, Action action, double amount) {
        if (player.isFolded() || player.isAllIn()) {
            return false;
        }

        switch (action) {

            case BET -> {
                if (!canBet(player)) {
                    return false;
                }

                if (!validateAmount(amount) || amount > player.getChips()) {
                    return false;
                }

                player.placeBet(amount);
                table.addToPot(amount);
                table.updateHighestBet(player);

                lastRaiseSize = player.getCurrentBet();
                actedPlayers.add(player);

                return true;
            }

            case RAISE -> {
                if (!canRaise(player)) {
                    return false;
                }

                if (amount <= table.getHighestBet()) {
                    return false;
                }

                double raiseSize = amount - table.getHighestBet();

                if (raiseSize < lastRaiseSize) {
                    return false;
                }

                double additionalAmount = amount - player.getCurrentBet();

                if (!validateAmount(additionalAmount) || additionalAmount > player.getChips()) {
                    return false;
                }

                player.placeBet(additionalAmount);
                table.addToPot(additionalAmount);

                double previousHighestBet = table.getHighestBet();

                table.updateHighestBet(player);

                if (player.getCurrentBet() > previousHighestBet) {
                    lastRaiseSize = player.getCurrentBet() - previousHighestBet;

                    actedPlayers.clear();
                    raiseRestrictedPlayers.clear();
                    actedPlayers.add(player);
                }

                return true;
            }

            case CALL -> {
                if (!canCall(player)) {
                    return false;
                }

                double additionalAmount = table.getHighestBet() - player.getCurrentBet();

                player.placeBet(additionalAmount);
                table.addToPot(additionalAmount);

                actedPlayers.add(player);
                raiseRestrictedPlayers.remove(player);

                return true;
            }

            case CHECK -> {
                if (!canCheck(player)) {
                    return false;
                }

                actedPlayers.add(player);

                return true;
            }

            case FOLD -> {
                player.setFolded(true);
                actedPlayers.add(player);

                return true;
            }

            case ALL_IN -> {
                if (player.getChips() <= 0) {
                    return false;
                }

                double allInAmount = player.getChips();
                double previousHighestBet = table.getHighestBet();

                player.placeBet(allInAmount);
                table.addToPot(allInAmount);
                table.updateHighestBet(player);

                double newHighestBet = table.getHighestBet();
                double increase = newHighestBet - previousHighestBet;

                if (newHighestBet > previousHighestBet) {

                    if (previousHighestBet == 0) {
                        lastRaiseSize = newHighestBet;
                        actedPlayers.clear();
                        raiseRestrictedPlayers.clear();
                    } else if (increase >= lastRaiseSize) {
                        lastRaiseSize = increase;
                        actedPlayers.clear();
                        raiseRestrictedPlayers.clear();
                    } else {
                        for (Player other : table.getPlayers()) {
                            if (other == player || other.isFolded() || other.isAllIn()) {
                                continue;
                            }

                            if (other.getCurrentBet() < newHighestBet
                                    && actedPlayers.contains(other)) {
                                actedPlayers.remove(other);
                                raiseRestrictedPlayers.add(other);
                            }
                        }
                    }
                }

                actedPlayers.add(player);

                return true;
            }

            default -> throw new IllegalStateException("Unexpected value: " + action);
        }
    }

    public boolean canBet(Player player) {
        return table.getHighestBet() == 0 && player.getChips() > 0;
    }

    public boolean canRaise(Player player) {
        if (table.getHighestBet() == 0 || player.getChips() <= 0) {
            return false;
        }

        return !raiseRestrictedPlayers.contains(player);
    }

    public boolean canCall(Player player) {
        double amountToCall = table.getHighestBet() - player.getCurrentBet();

        return amountToCall > 0 && amountToCall <= player.getChips();
    }

    public boolean canCheck(Player player) {
        return player.getCurrentBet() == table.getHighestBet();
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
                actedPlayers.clear();
                raiseRestrictedPlayers.clear();
                lastRaiseSize = bigBlind;

                int bigBlindIndex = -1;

                for (int i = 0; i < table.getPlayers().size(); i++) {
                    Player player = table.getPlayers().get(i);

                    if (table.getPlayers().size() == 2 && i == dealerIndex) {
                        double amount = Math.min(smallBlind, player.getChips());

                        player.placeBet(amount);
                        table.addToPot(amount);
                        table.updateHighestBet(player);

                    } else if (player.getPosition() == Position.SMALL_BLIND) {
                        double amount = Math.min(smallBlind, player.getChips());

                        player.placeBet(amount);
                        table.addToPot(amount);
                        table.updateHighestBet(player);

                    } else if (player.getPosition() == Position.BIG_BLIND) {
                        double amount = Math.min(bigBlind, player.getChips());

                        player.placeBet(amount);
                        table.addToPot(amount);
                        table.updateHighestBet(player);

                        bigBlindIndex = i;
                    }
                }

                if (bigBlindIndex == -1) {
                    return;
                }

                if (table.getPlayers().size() == 2) {
                    currentPlayer = table.getPlayers().get(dealerIndex);
                } else {
                    currentPlayer = findNextActivePlayer(bigBlindIndex);
                }
            }

            case FLOP -> {
                prepareNewStreet();

                for (int i = 0; i < 3; i++) {
                    table.addCommunityCard(deck.draw());
                }

                currentPlayer = findFirstPostFlopPlayer();
            }

            case TURN, RIVER -> {
                prepareNewStreet();

                table.addCommunityCard(deck.draw());

                currentPlayer = findFirstPostFlopPlayer();
            }

            case SHOWDOWN -> {
            }
        }
    }

    private void prepareNewStreet() {
        table.resetBettingRound();
        actedPlayers.clear();
        raiseRestrictedPlayers.clear();
        lastRaiseSize = 0.0;
    }

    private Player findFirstPostFlopPlayer() {
        if (table.getPlayers().size() == 2) {
            for (Player player : table.getPlayers()) {
                if (player.getPosition() == Position.BIG_BLIND
                        && !player.isFolded()
                        && !player.isAllIn()) {
                    return player;
                }
            }

            return findNextActivePlayer(dealerIndex);
        }

        return findNextActivePlayer(dealerIndex);
    }

    private Player findNextActivePlayer(int index) {
        int size = table.getPlayers().size();

        for (int i = 1; i <= size; i++) {
            int nextIndex = (index + i) % size;
            Player player = table.getPlayers().get(nextIndex);

            if (!player.isFolded() && !player.isAllIn()) {
                return player;
            }
        }

        return null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void moveToNextPlayer() {
        if (currentPlayer == null) {
            return;
        }

        int currentIndex = table.getPlayers().indexOf(currentPlayer);
        currentPlayer = findNextActivePlayer(currentIndex);
    }

    public boolean isBettingRoundFinished() {
        int activePlayers = 0;
        int playersWhoCanAct = 0;

        for (Player player : table.getPlayers()) {
            if (!player.isFolded()) {
                activePlayers++;

                if (!player.isAllIn()) {
                    playersWhoCanAct++;
                }
            }
        }

        if (activePlayers <= 1) {
            return true;
        }

        if (playersWhoCanAct == 0) {
            return true;
        }

        for (Player player : table.getPlayers()) {
            if (player.isFolded() || player.isAllIn()) {
                continue;
            }

            if (!actedPlayers.contains(player)) {
                return false;
            }

            if (player.getCurrentBet() != table.getHighestBet()) {
                return false;
            }
        }

        return true;
    }

    public void showdown() {
        HandEvaluation handEvaluation = new HandEvaluation();
        HandComparator comparator = new HandComparator();

        List<Player> eligiblePlayers = new ArrayList<>();

        for (Player player : table.getPlayers()) {
            if (!player.isFolded()) {
                eligiblePlayers.add(player);
            }
        }

        System.out.println("\n===== SHOWDOWN =====");

        if (eligiblePlayers.isEmpty()) {
            return;
        }

        if (eligiblePlayers.size() == 1) {
            Player winner = eligiblePlayers.get(0);
            double pot = table.getPot();

            winner.addChips(pot);

            System.out.println(winner.getName() + " wins $" + pot + " because everyone else folded.");

            table.clearPot();
            return;
        }

        List<HandResult> results = new ArrayList<>();

        for (Player player : eligiblePlayers) {
            HandResult result = handEvaluation.evaluateHand(player, table);
            results.add(result);

            System.out.println(player.getName() + ": " + result);
        }

        distributePots(eligiblePlayers, results, comparator);

        System.out.println("\n===== FINAL STACKS =====");

        for (Player player : table.getPlayers()) {
            System.out.println(player.getName() + ": $" + player.getChips());
        }
    }

    private void distributePots(List<Player> eligiblePlayers, List<HandResult> results, HandComparator comparator) {
        List<Double> contributionLevels = new ArrayList<>();

        for (Player player : table.getPlayers()) {
            if (player.getTotalContribution() > 0
                    && !contributionLevels.contains(player.getTotalContribution())) {
                contributionLevels.add(player.getTotalContribution());
            }
        }

        contributionLevels.sort(Double::compareTo);

        double previousLevel = 0.0;

        for (double level : contributionLevels) {
            double layerSize = level - previousLevel;

            if (layerSize <= 0) {
                continue;
            }

            double pot = layerSize * countPlayersAtOrAboveContribution(level);

            List<Player> potEligiblePlayers = new ArrayList<>();

            for (Player player : eligiblePlayers) {
                if (player.getTotalContribution() >= level) {
                    potEligiblePlayers.add(player);
                }
            }

            if (!potEligiblePlayers.isEmpty()) {
                List<Player> winners = findWinners(potEligiblePlayers, results, eligiblePlayers, comparator);
                splitPot(winners, pot);
            }

            previousLevel = level;
        }

        table.clearPot();
    }

    private int countPlayersAtOrAboveContribution(double level) {
        int count = 0;

        for (Player player : table.getPlayers()) {
            if (player.getTotalContribution() >= level) {
                count++;
            }
        }

        return count;
    }

    private List<Player> findWinners(List<Player> players, List<HandResult> results,
                                     List<Player> eligiblePlayers, HandComparator comparator) {
        List<Player> winners = new ArrayList<>();
        HandResult bestResult = null;

        for (Player player : players) {
            HandResult result = results.get(eligiblePlayers.indexOf(player));

            if (bestResult == null) {
                bestResult = result;
                winners.add(player);
                continue;
            }

            int comparison = comparator.compare(result, bestResult);

            if (comparison > 0) {
                winners.clear();
                winners.add(player);
                bestResult = result;
            } else if (comparison == 0) {
                winners.add(player);
            }
        }

        return winners;
    }

    private void splitPot(List<Player> winners, double pot) {
        if (winners.isEmpty()) {
            return;
        }

        double share = pot / winners.size();

        for (Player winner : winners) {
            winner.addChips(share);
        }

        if (winners.size() == 1) {
            System.out.println(winners.get(0).getName() + " wins $" + pot);
        } else {
            System.out.println("Pot of $" + pot + " split between:");

            for (Player winner : winners) {
                System.out.println(winner.getName() + " receives $" + share);
            }
        }
    }

    public void advanceDealer() {
        dealerIndex = (dealerIndex + 1) % table.getPlayers().size();
    }
}