import java.util.*;

public class HandEvaluation {

    public HandRank evaluateHand(Player player, Table table) {
        List<Card> cards = new ArrayList<>();
        Map<Rank, Integer> rankCount = new HashMap<>();

        // Get all 7 cards
        cards.addAll(player.getHoleCards());
        cards.addAll(table.getCommunityCards());

        // Count how many times each rank appears
        for (Card card : cards) {
            Rank rank = card.getRank();
            int count = rankCount.getOrDefault(rank, 0);
            rankCount.put(rank, count + 1);
        }

        return null;
    }

    private boolean hasStraightFlush(List<Card> cards) {
        Map<Suit, Integer> suitCount = new HashMap<>();

        // Count cards of each suit
        for (Card card : cards) {
            Suit suit = card.getSuit();
            int count = suitCount.getOrDefault(suit, 0);
            suitCount.put(suit, count + 1);
        }

        // Check every suit
        for (Suit suit : suitCount.keySet()) {

            if (suitCount.get(suit) >= 5) {

                List<Card> suitedCards = new ArrayList<>();

                // Get only cards of this suit
                for (Card card : cards) {
                    if (card.getSuit() == suit) {
                        suitedCards.add(card);
                    }
                }

                // Check if these cards contain a straight
                if (hasStraight(suitedCards)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean hasStraight(List<Card> cards) {

        List<Integer> values = new ArrayList<>();

        // Convert ranks to numbers and remove duplicates
        for (Card card : cards) {
            int value = card.getRank().ordinal();

            if (!values.contains(value)) {
                values.add(value);
            }
        }

        // Sort from lowest to highest
        Collections.sort(values);

        // Check normal straights
        int consecutive = 1;

        for (int i = 1; i < values.size(); i++) {
            if (values.get(i) == values.get(i - 1) + 1) {
                consecutive++;

                if (consecutive == 5) {
                    return true;
                }
            } else {
                consecutive = 1;
            }
        }

        // Check A-2-3-4-5
        return values.contains(12)
                && values.contains(0)
                && values.contains(1)
                && values.contains(2)
                && values.contains(3);
    }

    private boolean hasFourOfAKind(Map<Rank, Integer>rankCount){
        for(int count : rankCount.values()){
            if(count == 4){
                return true;
            }
        }
        return false;
    }

    private boolean hasFullHouse(Map<Rank, Integer> rankCount) {
        int threeCount = 0;
        int pairs = 0;

        for (int count : rankCount.values()) {
            if (count >= 3) {
                threeCount++;
            } else if (count == 2) {
                pairs++;
            }
        }

        return threeCount >= 1 && (pairs >= 1 || threeCount >= 2);
    }

    private boolean hasFlush(List<Card> cards){
        Map<Suit, Integer> suitCount = new HashMap<>();

        // Count cards of each suit
        for (Card card : cards) {
            Suit suit = card.getSuit();
            int count = suitCount.getOrDefault(suit, 0);
            suitCount.put(suit, count + 1);
        }

        for (int count : suitCount.values()){
            if(count >= 5){
                return true;
            }
        }

        return false;
    }

    private boolean hasThreeOfAKind(Map<Rank, Integer> rankCount){
        for (int count : rankCount.values()){
            if(count == 3){
                return true;
            }
        }

        return false;
    }

    private boolean hasTwoPair(Map<Rank, Integer> rankCount){
        int pairCount = 0;

        for(int card : rankCount.values()){
            if(card == 2){
                pairCount++;
            }
        }

        return pairCount == 2;
    }

    private boolean hasPair(Map<Rank, Integer> rankCount){
        int pairCount = 0;

        for(int card : rankCount.values()){
            if(card == 2){
                pairCount++;
            }
        }

        return pairCount == 1;
    }



}