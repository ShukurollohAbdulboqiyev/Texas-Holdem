    import java.util.ArrayList;
    import java.util.Collections;

    public class Deck {

        ArrayList<Card> cards = new ArrayList<>();

        public Deck(){
            for(Rank rank : Rank.values()){
                for(Suit suit : Suit.values()){
                    cards.add(new Card(rank, suit));
                }
            }
        }

        public void shuffle(){
            Collections.shuffle(cards);
        }

        public Card draw(){
            Card card = cards.getFirst();
            cards.remove(card);
            return card;
        }
    }
