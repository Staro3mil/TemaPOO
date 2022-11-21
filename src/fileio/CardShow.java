package fileio;


import com.fasterxml.jackson.databind.ObjectMapper;


import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;



public final class CardShow {
    /** This method shows all the cards in an array of cards */
    public ArrayNode cardDeck(final ArrayList<CardInput> deck) {

        ObjectMapper mapper = new ObjectMapper();

        ArrayNode array = mapper.createArrayNode();

        for (int i = 0; i < deck.size(); i++) {

            ObjectNode node = mapper.createObjectNode();
            CardInput card = deck.get(i);

            ArrayNode colors = mapper.createArrayNode();

            for (int j = 0; j < card.getColors().size(); j++) {
                colors.add(card.getColors().get(j));
            }

            node.put("mana", card.getMana());
            if (!card.getName().matches("Firestorm|Winterfell|Heart Hound")) {
                node.put("attackDamage", card.getAttackDamage());
                node.put("health", card.getHealth());
            }
            node.put("description", card.getDescription());
            node.set("colors", colors);
            node.put("name", card.getName());

            array.add(node);


        }

        return array;

    }
    /** Returns a single ObjectNode for a single card, usually a hero */
    public ObjectNode cardSingle(final CardInput card) {

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode node = mapper.createObjectNode();

        ArrayNode colors = mapper.createArrayNode();

        for (int i = 0; i < card.getColors().size(); i++) {
            colors.add(card.getColors().get(i));
        }
        node.put("mana", card.getMana());
        node.put("attackDamage", card.getAttackDamage());
        node.put("health", card.getHealth());
        node.put("description", card.getDescription());
        node.set("colors", colors);
        node.put("name", card.getName());
        return node;



    }
    /** This method shows the hero */
    public ObjectNode cardHero(final CardInput card, final int health) {

        ObjectMapper mapper = new ObjectMapper();

        ObjectNode node = mapper.createObjectNode();

        ArrayNode colors = mapper.createArrayNode();

        for (int i = 0; i < card.getColors().size(); i++) {
            colors.add(card.getColors().get(i));
        }
        node.put("mana", card.getMana());
        node.put("description", card.getDescription());
        node.set("colors", colors);
        node.put("name", card.getName());
        node.put("health", health);
        return node;



    }
    /**Returns the Env cards in the player's hand*/
    public ArrayNode showEnvCards(final ArrayList<CardInput> deck) {

        ObjectMapper mapper = new ObjectMapper();

        ArrayNode array = mapper.createArrayNode();

        for (int i = 0; i < deck.size(); i++) {

            ObjectNode node = mapper.createObjectNode();
            CardInput card = deck.get(i);

            if (card.getName().matches("Firestorm|Winterfell|Heart Hound")) {

            ArrayNode colors = mapper.createArrayNode();

            for (int j = 0; j < card.getColors().size(); j++) {
                colors.add(card.getColors().get(j));
            }

            node.put("mana", card.getMana());
            node.put("description", card.getDescription());
            node.set("colors", colors);
            node.put("name", card.getName());

            array.add(node);
            }

        }

        return array;

    }

}
