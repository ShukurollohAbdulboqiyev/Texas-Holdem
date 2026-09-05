import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandEvaluation {
    public HandRank evaluate(Player player, Table table) {

        List<Card> cards = new ArrayList<>();

        cards.addAll(player.getHoleCards());
        cards.addAll(table.getCommunityCards());

        Map<Rank, Integer> rankCounts = new HashMap<>();

        for (Card card : cards) {
            rankCounts.put(
                    card.getRank(), rankCounts.getOrDefault(card.getRank(), 0) + 1
            );
        }

        boolean flush = hasFlush(cards);
        boolean straight = hasStraight(cards);

        if (flush && straight) {
            return HandRank.STRAIGHT_FLUSH;
        }

        if (hasFourOfAKind(rankCounts)) {
            return HandRank.FOUR_OF_A_KIND;
        }

        if (hasFullHouse(rankCounts)) {
            return HandRank.FULL_HOUSE;
        }

        if (flush) {
            return HandRank.FLUSH;
        }

        if (straight) {
            return HandRank.STRAIGHT;
        }

        if (hasThreeOfAKind(rankCounts)) {
            return HandRank.THREE_OF_A_KIND;
        }

        if (hasTwoPair(rankCounts)) {
            return HandRank.TWO_PAIR;
        }

        if (hasPair(rankCounts)) {
            return HandRank.PAIR;
        }

        return HandRank.HIGH_CARD;
    }

    private boolean hasPair(Map<Rank, Integer> rankCounts) {
        return rankCounts.containsValue(2);
    }

    private boolean hasTwoPair(Map<Rank, Integer> rankCounts) {
        int pairs = 0;

        for (int count : rankCounts.values()) {
            if (count == 2) {
                pairs++;
            }
        }

        return pairs >= 2;
    }

    private boolean hasThreeOfAKind(Map<Rank, Integer> rankCounts) {
        return rankCounts.containsValue(3);
    }

    private boolean hasFourOfAKind(Map<Rank, Integer> rankCounts) {
        return rankCounts.containsValue(4);
    }

    private boolean hasFullHouse(Map<Rank, Integer> rankCounts) {
        boolean three = false;
        boolean pair = false;

        for (int count : rankCounts.values()) {
            if (count >= 3) {
                three = true;
            } else if (count >= 2) {
                pair = true;
            }
        }
        return three && pair;
    }

    private boolean hasFlush(List<Card> cards) {
        Map<Suit, Integer> suitCounts = new HashMap<>();

        for (Card card : cards) {
            suitCounts.put(card.getSuit(), suitCounts.getOrDefault(card.getSuit(), 0) + 1
            );
        }
        return suitCounts.containsValue(5);
    }

    private boolean hasStraight(List<Card> cards) {
        List<Integer> values = new ArrayList<>();

        for (Card card : cards) {
            int value = card.getRank().ordinal();

            if (!values.contains(value)) {
                values.add(value);
            }
        }

        values.sort(Comparator.naturalOrder());

        if (values.contains(12)
                && values.contains(2)
                && values.contains(3)
                && values.contains(4)
                && values.contains(5)) {
            return true;
        }

        for (int i = 0; i <= values.size() - 5; i++) {
            if (values.get(i + 1) == values.get(i) + 1
                    && values.get(i + 2) == values.get(i) + 2
                    && values.get(i + 3) == values.get(i) + 3
                    && values.get(i + 4) == values.get(i) + 4) {
                return true;
            }
        }

        return false;
    }
}