package fileio;

public final class Card {

    private CardInput card;

    private int frozen = 0;

    private boolean ready = true;

    public boolean isReady() {
        return ready;
    }

    public void setReady(final boolean ready) {
        this.ready = ready;
    }

    public CardInput getCard() {
        return card;
    }

    public void setCard(final CardInput card) {
        this.card = card;
    }

    public int getFrozen() {
        return frozen;
    }

    public void setFrozen(final int frozen) {
        this.frozen = frozen;
    }
}
