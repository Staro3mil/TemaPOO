package fileio;

import java.util.ArrayList;

public final class PlayerHand {
    private int nrCardsInHand;

    private int mana = 1;
    private ArrayList<CardInput> cards;




    public PlayerHand() {
        nrCardsInHand = 0;
        cards = new ArrayList<>();
    }
    /**Adds a card */
    public void addCard(final CardInput card) {
        this.cards.add(card);
        this.nrCardsInHand++;
    }
    /**Removes a card */
    public void removeCard(final int index) {
        this.cards.remove(index);
        this.nrCardsInHand--;
    }


    public int getNrCardsInHand() {
        return nrCardsInHand;
    }

    public void setNrCardsInHand(final int nrCardsInHand) {
        this.nrCardsInHand = nrCardsInHand;
    }

    public ArrayList<CardInput> getCards() {
        return cards;
    }

    public void setCards(final ArrayList<CardInput> cards) {
        this.cards = cards;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(final int mana) {
        this.mana = mana;
    }





    @Override
    public String toString() {
        return "InfoInput{"
                + "nr_cards_in_hand="
                + nrCardsInHand
                + ", cards="
                + cards
                + '}';
    }
}

