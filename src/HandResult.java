import java.util.List;

public class HandResult {

    private final HandRank handRank;
    private final List<Integer> values;

    public HandResult(HandRank handRank, List<Integer> values) {
        this.handRank = handRank;
        this.values = values;
    }

    public HandRank getHandRank() {
        return handRank;
    }

    public List<Integer> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return handRank + " " + values;
    }
}