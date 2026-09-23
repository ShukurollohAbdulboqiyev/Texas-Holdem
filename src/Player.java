import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String name;
    private double chips;
    private final List<Card> holeCards = new ArrayList<>();
    private double currentBet = 0.0;
    private double totalContribution = 0.0;
    private boolean isFolded = false;
    private boolean isAllIn = false;
    private Position position;

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

    public double getTotalContribution() {
        return totalContribution;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isFolded() {
        return isFolded;
    }

    public boolean isAllIn() {
        return isAllIn;
    }

    public void setFolded(boolean folded) {
        isFolded = folded;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void addCard(Card card) {
        holeCards.add(card);
    }

    public void placeBet(double amount) {
        if (amount < 0 || amount > chips) {
            throw new IllegalArgumentException("Invalid bet amount");
        }

        chips -= amount;
        currentBet += amount;
        totalContribution += amount;

        if (chips == 0) {
            isAllIn = true;
        }
    }

    public void addChips(double amount) {
        chips += amount;
    }

    public void resetForNewStreet() {
        currentBet = 0.0;
    }

    public void resetForNewHand() {
        holeCards.clear();
        currentBet = 0.0;
        totalContribution = 0.0;
        isFolded = false;
        isAllIn = false;
        position = null;
    }
}