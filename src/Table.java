import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Table {

    private double pot;
    private double highestBet;
    private final List<Player> players = new ArrayList<>();
    private final List<Card> communityCards = new ArrayList<>();

    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public List<Card> getCommunityCards() {
        return Collections.unmodifiableList(communityCards);
    }

    public double getPot() {
        return pot;
    }

    public double getHighestBet() {
        return highestBet;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void addCommunityCard(Card card) {
        communityCards.add(card);
    }

    public void addToPot(double amount) {
        pot += amount;
    }

    public void updateHighestBet(Player player) {
        if (player.getCurrentBet() > highestBet) {
            highestBet = player.getCurrentBet();
        }
    }
}