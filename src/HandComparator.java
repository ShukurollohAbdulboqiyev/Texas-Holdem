public class HandComparator {

    public int compare(HandResult first, HandResult second) {
        int rankComparison = Integer.compare(
                first.getHandRank().ordinal(),
                second.getHandRank().ordinal()
        );

        if (rankComparison != 0) {
            return rankComparison;
        }

        for (int i = 0; i < first.getValues().size(); i++) {
            int valueComparison = Integer.compare(
                    first.getValues().get(i),
                    second.getValues().get(i)
            );

            if (valueComparison != 0) {
                return valueComparison;
            }
        }

        return 0;
    }
}