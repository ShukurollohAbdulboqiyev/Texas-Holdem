import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandEvaluation {

    public HandResult evaluateHand(Player player, Table table) {
        List<Card> cards = new ArrayList<>();

        cards.addAll(player.getHoleCards());
        cards.addAll(table.getCommunityCards());

        HandResult bestResult = null;

        for (int a = 0; a < cards.size() - 4; a++) {
            for (int b = a + 1; b < cards.size() - 3; b++) {
                for (int c = b + 1; c < cards.size() - 2; c++) {
                    for (int d = c + 1; d < cards.size() - 1; d++) {
                        for (int e = d + 1; e < cards.size(); e++) {

                            List<Card> fiveCards = List.of(
                                    cards.get(a),
                                    cards.get(b),
                                    cards.get(c),
                                    cards.get(d),
                                    cards.get(e)
                            );

                            HandResult result = evaluateFiveCards(fiveCards);

                            if (bestResult == null || compareResults(result, bestResult) > 0) {
                                bestResult = result;
                            }
                        }
                    }
                }
            }
        }

        return bestResult;
    }

    private HandResult evaluateFiveCards(List<Card> cards) {
        Map<Rank, Integer> rankCount = new HashMap<>();

        for (Card card : cards) {
            rankCount.put(
                    card.getRank(),
                    rankCount.getOrDefault(card.getRank(), 0) + 1
            );
        }

        boolean flush = hasFlush(cards);
        int straightHigh = getStraightHighCard(cards);

        List<Integer> fourOfAKind = new ArrayList<>();
        List<Integer> threeOfAKind = new ArrayList<>();
        List<Integer> pairs = new ArrayList<>();

        for (Map.Entry<Rank, Integer> entry : rankCount.entrySet()) {
            int value = rankValue(entry.getKey());
            int count = entry.getValue();

            if (count == 4) {
                fourOfAKind.add(value);
            } else if (count == 3) {
                threeOfAKind.add(value);
            } else if (count == 2) {
                pairs.add(value);
            }
        }

        fourOfAKind.sort(Collections.reverseOrder());
        threeOfAKind.sort(Collections.reverseOrder());
        pairs.sort(Collections.reverseOrder());

        if (flush && straightHigh == 14) {
            return new HandResult(HandRank.ROYAL_FLUSH, List.of(14));
        }

        if (flush && straightHigh > 0) {
            return new HandResult(HandRank.STRAIGHT_FLUSH, List.of(straightHigh));
        }

        if (!fourOfAKind.isEmpty()) {
            int fourValue = fourOfAKind.get(0);
            int kicker = getHighestOtherCard(cards, fourValue);

            return new HandResult(
                    HandRank.FOUR_OF_A_KIND,
                    List.of(fourValue, kicker)
            );
        }

        if (!threeOfAKind.isEmpty()
                && (!pairs.isEmpty() || threeOfAKind.size() >= 2)) {

            int threeValue = threeOfAKind.get(0);
            int pairValue;

            if (threeOfAKind.size() >= 2) {
                pairValue = threeOfAKind.get(1);
            } else {
                pairValue = pairs.get(0);
            }

            return new HandResult(
                    HandRank.FULL_HOUSE,
                    List.of(threeValue, pairValue)
            );
        }

        if (flush) {
            List<Integer> values = new ArrayList<>();

            for (Card card : cards) {
                values.add(rankValue(card.getRank()));
            }

            values.sort(Collections.reverseOrder());

            return new HandResult(HandRank.FLUSH, values);
        }

        if (straightHigh > 0) {
            return new HandResult(HandRank.STRAIGHT, List.of(straightHigh));
        }

        if (!threeOfAKind.isEmpty()) {
            int threeValue = threeOfAKind.get(0);
            List<Integer> values = new ArrayList<>();

            values.add(threeValue);
            addHighestCardsExcluding(cards, values, threeValue, 2);

            return new HandResult(HandRank.THREE_OF_A_KIND, values);
        }

        if (pairs.size() >= 2) {
            int highPair = pairs.get(0);
            int lowPair = pairs.get(1);
            int kicker = getHighestOtherCard(cards, highPair, lowPair);

            return new HandResult(
                    HandRank.TWO_PAIR,
                    List.of(highPair, lowPair, kicker)
            );
        }

        if (pairs.size() == 1) {
            int pairValue = pairs.get(0);
            List<Integer> values = new ArrayList<>();

            values.add(pairValue);
            addHighestCardsExcluding(cards, values, pairValue, 3);

            return new HandResult(HandRank.PAIR, values);
        }

        List<Integer> values = new ArrayList<>();

        for (Card card : cards) {
            values.add(rankValue(card.getRank()));
        }

        values.sort(Collections.reverseOrder());

        return new HandResult(HandRank.HIGH_CARD, values);
    }

    private boolean hasFlush(List<Card> cards) {
        Suit firstSuit = cards.get(0).getSuit();

        for (Card card : cards) {
            if (card.getSuit() != firstSuit) {
                return false;
            }
        }

        return true;
    }

    private int getStraightHighCard(List<Card> cards) {
        List<Integer> values = new ArrayList<>();

        for (Card card : cards) {
            int value = rankValue(card.getRank());

            if (!values.contains(value)) {
                values.add(value);
            }
        }

        Collections.sort(values);

        if (values.contains(14)
                && values.contains(2)
                && values.contains(3)
                && values.contains(4)
                && values.contains(5)) {
            return 5;
        }

        for (int i = 0; i <= values.size() - 5; i++) {
            boolean straight = true;

            for (int j = 1; j < 5; j++) {
                if (values.get(i + j) != values.get(i) + j) {
                    straight = false;
                    break;
                }
            }

            if (straight) {
                return values.get(i + 4);
            }
        }

        return 0;
    }

    private int getHighestOtherCard(List<Card> cards, int... excludedValues) {
        int highest = 0;

        for (Card card : cards) {
            int value = rankValue(card.getRank());
            boolean excluded = false;

            for (int excludedValue : excludedValues) {
                if (value == excludedValue) {
                    excluded = true;
                    break;
                }
            }

            if (!excluded) {
                highest = Math.max(highest, value);
            }
        }

        return highest;
    }

    private void addHighestCardsExcluding(
            List<Card> cards,
            List<Integer> values,
            int excludedValue,
            int amount
    ) {
        List<Integer> kickers = new ArrayList<>();

        for (Card card : cards) {
            int value = rankValue(card.getRank());

            if (value != excludedValue) {
                kickers.add(value);
            }
        }

        kickers.sort(Collections.reverseOrder());

        for (int i = 0; i < amount && i < kickers.size(); i++) {
            values.add(kickers.get(i));
        }
    }

    private int rankValue(Rank rank) {
        return rank.ordinal() + 2;
    }

    private int compareResults(HandResult first, HandResult second) {
        if (first.getHandRank().ordinal() != second.getHandRank().ordinal()) {
            return Integer.compare(
                    first.getHandRank().ordinal(),
                    second.getHandRank().ordinal()
            );
        }

        List<Integer> firstValues = first.getValues();
        List<Integer> secondValues = second.getValues();

        for (int i = 0; i < firstValues.size(); i++) {
            int comparison = Integer.compare(
                    firstValues.get(i),
                    secondValues.get(i)
            );

            if (comparison != 0) {
                return comparison;
            }
        }

        return 0;
    }
}