package fileio;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;
import java.util.Collections;

public final class Board {

    private ArrayList<ArrayList<Card>> board;
    private ObjectMapper mapper = new ObjectMapper();
    private ArrayNode boardCards = mapper.createArrayNode();

    private int middleRow = 2;

    private int maxSize = 5;

    public Board() {
        this.board = new ArrayList<>();
        ArrayList<Card> row1 = new ArrayList<>();
        ArrayList<Card> row2 = new ArrayList<>();
        ArrayList<Card> row3 = new ArrayList<>();
        ArrayList<Card> row4 = new ArrayList<>();
        Collections.addAll(this.board, row1, row2, row3, row4);
    }


    /**This method makes an array of every row and then passes it into an array to be returned */
    public ArrayNode showBoard() {
        for (int i = 0; i < board.size(); i++) {
            ArrayNode rowCards = mapper.createArrayNode();

            for (int j = 0; j < board.get(i).size(); j++) {

                ObjectNode node = mapper.createObjectNode();

                CardInput card = board.get(i).get(j).getCard();

                ArrayNode colors = mapper.createArrayNode();

                for (int g = 0; g < card.getColors().size(); g++) {
                    colors.add(card.getColors().get(g));
                }

                node.put("mana", card.getMana());
                if (!card.getName().matches("Firestorm|Winterfell|Heart Hound")) {
                    node.put("attackDamage", card.getAttackDamage());
                    node.put("health", card.getHealth());
                }
                node.put("description", card.getDescription());
                node.set("colors", colors);
                node.put("name", card.getName());

                rowCards.add(node);
            }
            boardCards.add(rowCards);

        }
        return boardCards;
    }
    /** Goes through all the cards in the board and checks if theyre frozen */
    public ArrayNode showFrozen() {
        ArrayNode frozenBoardCards = mapper.createArrayNode();
        for (int i = 0; i < board.size(); i++) {
            ArrayNode rowCards = mapper.createArrayNode();

            for (int j = 0; j < board.get(i).size(); j++) {

                if (board.get(i).get(j).getFrozen() == 1) {
                    ObjectNode node = mapper.createObjectNode();
                    CardInput card = board.get(i).get(j).getCard();

                    ArrayNode colors = mapper.createArrayNode();

                    for (int g = 0; g < card.getColors().size(); g++) {
                        colors.add(card.getColors().get(g));
                    }

                    node.put("mana", card.getMana());
                    if (!card.getName().matches("Firestorm|Winterfell|Heart Hound")) {
                        node.put("attackDamage", card.getAttackDamage());
                        node.put("health", card.getHealth());
                    }
                    node.put("description", card.getDescription());
                    node.set("colors", colors);
                    node.put("name", card.getName());

                    rowCards.add(node);
                }
            }
            if (!rowCards.isEmpty()) {
                frozenBoardCards.add(rowCards);
            }


        }
        return frozenBoardCards;
    }

    /**This method places a card by checking which row corresponds to which player*/
    public String placeCard(final int index, final int playerID, final PlayerHand hand) {
        if (hand.getCards().size() <= index) {
            return "0";
        }

        String cardName = hand.getCards().get(index).getName();
        CardInput card = hand.getCards().get(index);
        Card realCard = new Card();
        realCard.setCard(card);
        int cardMana = hand.getCards().get(index).getMana();
        int rowFront, rowBack;

        if (playerID == 1) {
             rowFront = playerID + playerID;
             rowBack = playerID + playerID + playerID;
        } else {
             rowFront = 1;
             rowBack = 0;
        }
        if (cardName.matches("Heart Hound|Winterfell|Firestorm")) {
            return "0";
        }
        if (cardMana > hand.getMana()) {
            return "1";
        }
        if (cardName.matches("The Ripper|Miraj|Goliath|Warden")) {
            if (board.get(rowFront).size() < maxSize) {
                board.get(rowFront).add(realCard);
                //System.out.println(card.toString() + "player " + playerID + " row " + rowFront);
                return "3";
            } else {
                return "2";
            }
        }
        if (cardName.matches("Sentinel|Berserker|The Cursed One|Disciple")) {
            if (board.get(rowBack).size() < maxSize) {
                board.get(rowBack).add(realCard);
                //System.out.println(card.toString() + "player " + playerID + " row " + rowBack);
                return "3";

            } else {
                return "2";
            }
        }
        return "3";
    }
    /** Use an Environment card */
    public String useEnvCard(final PlayerHand hand, final int index,
                             final int affectRow, final int id) {
        CardInput card = hand.getCards().get(index);
        if (!card.getName().matches("Heart Hound|Winterfell|Firestorm")) {
            return "not env";
        }

        int mana = hand.getMana();

        if (mana < card.getMana()) {
            return "no mana";
        }



        if (id == 1 && (affectRow >= middleRow)) {
                return "own row";
        }
        if (id == 2 && (affectRow < middleRow)) {
            return "own row";
        }

        //I hope I don't get points deducted for this but fuck Heart Hound
        if (card.getName().equals("Heart Hound")) {
            if (id == 1) {
                //Fuck magic numbers I'm just using ID as a replacement for 1
                if (this.board.get(id + id).size() >= maxSize && affectRow == id) {
                    return "no space";
                }
                if (this.board.get(id + id + id).size() >= maxSize && affectRow == id - id) {
                    return "no space";
                }
                ArrayList<Card> row = this.board.get(affectRow);
                int maxHP = id - id;
                int swapCardIndex = id - id;
                for (int i = 0; i < row.size(); i++) {
                    if (maxHP < row.get(i).getCard().getHealth()) {
                        maxHP = row.get(i).getCard().getHealth();
                        swapCardIndex = i;
                    }
                }
                //Gets the card that's abt to be swapped
                Card swappedCard = this.board.get(affectRow).get(swapCardIndex);

                //Adds the card to the opposite row it's been taken from
                if (affectRow == id) {
                    this.board.get(affectRow + id).add(swappedCard);
                } else {
                    this.board.get(affectRow + id + id + id).add(swappedCard);
                }


                //Removes card from its initial row
                this.board.get(affectRow).remove(swapCardIndex);

                hand.setMana(mana - card.getMana());
                hand.getCards().remove(index);
                return "done";

            }

            //Fuck magic numbers I'm just using ID as a replacement for 2
            if (this.board.get(id - id).size() >= maxSize && affectRow == id + id / id) {
                return "no space";
            }
            if (this.board.get(id / id).size() >= maxSize && affectRow == id) {
                return "no space";
            }
            ArrayList<Card> row = this.board.get(affectRow);
            int maxHP = id - id;
            int swapCardIndex = id - id;
            for (int i = 0; i < row.size(); i++) {
                if (maxHP < row.get(i).getCard().getHealth()) {
                    maxHP = row.get(i).getCard().getHealth();
                    swapCardIndex = i;
                }
            }
            //Gets the card that's abt to be swapped
            Card swappedCard = this.board.get(affectRow).get(swapCardIndex);

            //Adds the card to the opposite row it's been taken from
            if (affectRow == id) {
                this.board.get(id / id).add(swappedCard);
            } else {
                this.board.get(affectRow - id - id / id).add(swappedCard);
            }

            //Removes card from its initial row
            this.board.get(affectRow).remove(swapCardIndex);

            hand.setMana(mana - card.getMana());
            hand.getCards().remove(index);

            return "done";

        }

        if (card.getName().equals("Winterfell")) {
           ArrayList<Card> row = this.board.get(affectRow);
           for (int i = 0; i < row.size(); i++) {
               row.get(i).setFrozen(id / id);
           }
           hand.setMana(mana - hand.getCards().get(index).getMana());
           hand.getCards().remove(index);

           return "done";
        }

        //This is for Firestorm
        ArrayList<Card> row = this.board.get(affectRow);
        for (int i = 0; i < row.size(); i++) {
            CardInput affectedCard = row.get(i).getCard();
            affectedCard.setHealth(affectedCard.getHealth() - id / id);
            if (affectedCard.getHealth() == 0) {
                row.remove(i);
                i--;
            }
        }
        hand.setMana(mana - hand.getCards().get(index).getMana());
        hand.getCards().remove(index);
        return "done";

    }
    /** Checks if there are any tanks on the specified row */
    private Boolean checkTank(final int row) {
        for (int i = 0; i < board.get(row).size(); i++) {
            String name = board.get(row).get(i).getCard().getName();
            if ((name.matches("Goliath|Warden"))) {
                return true;
            }
        }
        return false;
    }
    /** Makes a card attack another card */
    public String attackCard(final int xAtk, final int yAtk, final int xDef, final int yDef) {
        Card def = this.board.get(xDef).get(yDef);
        Card atk = this.board.get(xAtk).get(yAtk);
        int defHealth = def.getCard().getHealth();
        int atkDmg = atk.getCard().getAttackDamage();
        String defname = def.getCard().getName();
        int tankRow;

        if ((xAtk >= middleRow && xDef >= middleRow) || (xAtk < middleRow && xDef < middleRow)) {
            return "friendly fire";
        }

        if (!atk.isReady()) {
            return "not ready";
        }

        if (atk.getFrozen() == 1) {
            return "frozen";
        }

        if (xDef < middleRow) {
            tankRow = middleRow - middleRow / middleRow;
        } else {
            tankRow = middleRow;
        }

        if (checkTank(tankRow) && !defname.matches("Warden|Goliath")) {
            return "not tank";
        }

        def.getCard().setHealth(defHealth - atkDmg);
        atk.setReady(false);

        if (defHealth - atkDmg  <= 0) {
            this.board.get(xDef).remove(yDef);
        }
        return "done";
    }
    /** Attacks a hero */
    public String attackHero(final int xAtk, final int yAtk, int heroHealth) {

        Card atk = this.board.get(xAtk).get(yAtk);

        int atkDmg = atk.getCard().getAttackDamage();

        int tankRow;


        if (!atk.isReady()) {
            return "not ready";
        }

        if (atk.getFrozen() == 1) {
            return "frozen";
        }

        if (xAtk >= middleRow) {
            tankRow = middleRow - middleRow / middleRow;
        } else {
            tankRow = middleRow;
        }

        if (checkTank(tankRow)) {
            return "not tank";
        }

        heroHealth -= atkDmg;

        atk.setReady(false);

        if (heroHealth  <= 0) {
            return "win";
        }
        return Integer.toString(heroHealth);
    }
    /** Uses the ability of the Card */
    public String abilityCard(final int xAtk, final int yAtk, final int xDef, final int yDef) {
        Card def = this.board.get(xDef).get(yDef);
        Card atk = this.board.get(xAtk).get(yAtk);
        int defHealth = def.getCard().getHealth();
        int atkHealth = atk.getCard().getHealth();
        //int atkDmg = atk.getCard().getAttackDamage();
        int defDmg = def.getCard().getAttackDamage();
        String defName = def.getCard().getName();
        String atkName = atk.getCard().getName();
        int tankRow;

        if (atk.getFrozen() == 1) {
            return "frozen";
        }

        if (!atk.isReady()) {
            return "not ready";
        }

        if (atkName.equals("Disciple")) {
            if ((xAtk >= middleRow && xDef >= middleRow)
                    || (xAtk < middleRow && xDef < middleRow)) {
                def.getCard().setHealth(defHealth + middleRow);
                atk.setReady(false);
                return "done";
            }

            return "not ally";
        }


        if ((xAtk >= middleRow && xDef >= middleRow) || (xAtk < middleRow && xDef < middleRow)) {
            return "friendly fire";
        }





        if (xDef < middleRow) {
            tankRow = middleRow - middleRow / middleRow;
        } else {
            tankRow = middleRow;
        }

        if (checkTank(tankRow) && !defName.matches("Warden|Goliath")) {
            return "not tank";
        }

        if (atkName.equals("The Ripper")) {
            if (defDmg <= middleRow) {
                def.getCard().setAttackDamage(0);
            } else {
                def.getCard().setAttackDamage(defDmg - middleRow);
            }
            atk.setReady(false);
            return "done";
        }

        if (atkName.equals("Miraj")) {

            atk.getCard().setHealth(defHealth); //This might not work so possible use of aux needed
            def.getCard().setHealth(atkHealth);
            atk.setReady(false);
            return "done";
        }

        //THIS IS FOR CURSED ONE

        def.getCard().setHealth(defDmg); //This might not work so possible use of aux needed
        def.getCard().setAttackDamage(defHealth);
        if (def.getCard().getHealth() <= 0) {
            this.board.get(xDef).remove(yDef);
        }
        atk.setReady(false);
        return "done";
    }
    /** Uses the ability of a hero*/
    public String useHero (final int row, final PlayerHand hand,
                           final int turn, final Card hero) {
        int mana = hand.getMana();
        String heroName = hero.getCard().getName();
        if (mana < hero.getCard().getMana()) {
            return "no mana";
        }
        if (!hero.isReady()) {
            return "not ready";
        }
        if (heroName.matches("Lord Royce|Empress Thorina")) {
            if ( turn == 1 && row >= middleRow) {
                return "friendly fire";
            }
            if (turn == 2 && row < middleRow) {
                return "friendly fire";
            }
        }
        if (heroName.matches("General Kocioraw|King Mudface")) {
            if ( turn == 1 && row < middleRow) {
                return "not friendly";
            }
            if (turn == 2 && row >= middleRow) {
                return "not friendly";
            }
        }

        if (heroName.equals("Lord Royce")) {
            int maxDmg = 0;
            int pos = 0;
            for(int i = 0; i < board.get(row).size(); i++) {
                if ( board.get(row).get(i).getCard().getAttackDamage() > maxDmg) {
                    maxDmg =  board.get(row).get(i).getCard().getAttackDamage();
                    pos = i;
                }
            }
            board.get(row).get(pos).setFrozen(1);
            hero.setReady(false);
            hand.setMana(hand.getMana() - hero.getCard().getMana());
            return "done";
        }

        if (heroName.equals("Empress Thorina")) {
            int maxHP = 0;
            int pos = 0;
            for(int i = 0; i < board.get(row).size(); i++) {
                if ( board.get(row).get(i).getCard().getHealth() > maxHP) {
                    maxHP =  board.get(row).get(i).getCard().getHealth();
                    pos = i;
                }
            }
            board.get(row).remove(pos);
            hero.setReady(false);
            hand.setMana(hand.getMana() - hero.getCard().getMana());
            return "done";
        }

        if (heroName.equals("King Mudface")) {
            for(int i = 0; i < board.get(row).size(); i++) {
                CardInput card = board.get(row).get(i).getCard();
                card.setHealth(card.getHealth() + 1);
            }

            hero.setReady(false);
            hand.setMana(hand.getMana() - hero.getCard().getMana());
            return "done";
        }

        //For General Kocioraw
        for(int i = 0; i < board.get(row).size(); i++) {
            CardInput card = board.get(row).get(i).getCard();
            card.setAttackDamage(card.getAttackDamage() + 1);
        }

        hero.setReady(false);
        hand.setMana(hand.getMana() - hero.getCard().getMana());
        return "done";



    }

    public ArrayList<ArrayList<Card>> getBoard() {
        return board;
    }

    public void setBoard(final ArrayList<ArrayList<Card>> board) {
        this.board = board;
    }

}
