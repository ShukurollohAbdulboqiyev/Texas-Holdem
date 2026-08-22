import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String name;
    private double chips;
    private final List<Card> holeCards = new ArrayList<>();
    private double currentBet = 0.0;

    public Player(String name, double chips) {
        this.name = name;
        this.chips = chips;
    }

    public String getName() {
        return name;
    }

    public double getChips() {
        return chips;
    }

    public List<Card> getHoleCards() {
        return holeCards;
    }

    public double getCurrentBet() {
        return currentBet;
    }

    public void addCard(Card card){
        holeCards.add(card);
    }

    // placing a bet
    public void placeBet(double amount){
        chips -= amount;
        currentBet += amount;
    }
}