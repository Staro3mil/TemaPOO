package fileio;



import com.fasterxml.jackson.databind.ObjectMapper;


import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Random;

public final class Function {
    /**Creates a deep copy of a deck of cards and returns it */
    public ArrayList<CardInput> copyDeck(ArrayList<CardInput> deck) {
        ArrayList<CardInput> cDeck = new ArrayList<>();

        for(int i = 0; i < deck.size(); i++) {
            CardInput copy;
            CardInput original;
            original = deck.get(i);
            copy = new CardInput();
            copy.setMana(original.getMana());
            copy.setColors(original.getColors());
            copy.setName(original.getName());
            copy.setDescription(original.getDescription());
            if (!original.getName().matches("Winterfell|Heart Hound|Firestorm")) {
                copy.setHealth(original.getHealth());
                copy.setAttackDamage(original.getAttackDamage());
            }
            cDeck.add(copy);
        }
        return cDeck;
    }


    /** This does all the work*/
    public ArrayNode gwentStone(final Input input, final ArrayNode output) {

        ArrayList<CardInput> deck1;

        ArrayList<CardInput> deck2;

        GameInput gameInput;

        StartGameInput startGameInput;

        CardInput heroP1;

        CardInput heroP2;

        int playerOneWins = 0;

        int playerTwoWins = 0;

        int totalGames = 0;

        ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        for (int games = 0; games < input.getGames().size(); games++) {

            gameInput = input.getGames().get(games);

            startGameInput = gameInput.getStartGame();

            heroP1 = startGameInput.getPlayerOneHero();

            heroP2 = startGameInput.getPlayerTwoHero();

            int ten = 10;

            int healthHero1 = ten + ten + ten;

            int healthHero2 = ten + ten + ten;

            Card heroOne = new Card();

            heroOne.setCard(heroP1);

            Card heroTwo = new Card();

            heroTwo.setCard(heroP2);

            deck1 = input.getPlayerOneDecks().getDecks().get(startGameInput.getPlayerOneDeckIdx());

            deck2 = input.getPlayerTwoDecks().getDecks().get(startGameInput.getPlayerTwoDeckIdx());

            ArrayList<CardInput> pDeck1 = copyDeck(deck1);

            ArrayList<CardInput> pDeck2 = copyDeck(deck2);

            Collections.shuffle(pDeck1, new Random(startGameInput.getShuffleSeed()));

            Collections.shuffle(pDeck2, new Random(startGameInput.getShuffleSeed()));

            int manaIndex1 = 2;

            int manaIndex2 = 2;

            int turn = startGameInput.getStartingPlayer();

            PlayerHand playerHand1 = new PlayerHand();

            PlayerHand playerHand2 = new PlayerHand();

            playerHand1.addCard(pDeck1.get(0));

            pDeck1.remove(0);

            playerHand2.addCard(pDeck2.get(0));

            pDeck2.remove(0);

            Board board = new Board();

            int round = 0;



            for (int i = 0; i < gameInput.getActions().size(); i++) {
                ActionsInput currAction = gameInput.getActions().get(i);
                if (round == 2) {
                    playerHand1.setMana(playerHand1.getMana() + manaIndex1);
                    if (manaIndex1 < ten) {
                        manaIndex1++;
                    }
                    playerHand2.setMana(playerHand2.getMana() + manaIndex2);
                    if (manaIndex2 < ten) {
                        manaIndex2++;
                    }
                    if (!pDeck1.isEmpty()) {
                        playerHand1.addCard(pDeck1.get(0));

                        pDeck1.remove(0);
                    }
                    if (!pDeck2.isEmpty()) {
                        playerHand2.addCard(pDeck2.get(0));

                        pDeck2.remove(0);
                    }

                    round = 0;
                }
                if (turn == 3) {
                    turn = 1;
                }
                ObjectNode command = objectMapper.createObjectNode();
                command.put("command", currAction.getCommand());
                CardShow cardShow = new CardShow();

                if (currAction.getCommand().equals("getPlayerDeck")) {
                    command.put("playerIdx", currAction.getPlayerIdx());
                    if (currAction.getPlayerIdx() == 1) {
                        command.set("output", cardShow.cardDeck(pDeck1));
                        //System.out.println("Player1 "+PDeck1.toString());

                    } else {
                        command.set("output", cardShow.cardDeck(pDeck2));
                        //System.out.println("Player2 "+PDeck2.toString());
                    }
                    output.add(command);
                    continue;
                }

                if (currAction.getCommand().equals("getPlayerHero")) {
                    command.put("playerIdx", currAction.getPlayerIdx());
                    if (currAction.getPlayerIdx() == 1) {
                        command.set("output", cardShow.cardHero(heroP1, healthHero1));

                    } else {
                        command.set("output", cardShow.cardHero(heroP2, healthHero2));

                    }
                    output.add(command);
                    continue;
                }

                if (currAction.getCommand().equals("getPlayerTurn")) {
                    command.put("output", turn);
                    output.add(command);
                    continue;

                }

                if (currAction.getCommand().equals("getCardAtPosition")) {
                    int x, y;
                    x = currAction.getX();
                    y = currAction.getY();
                    command.put("x", currAction.getX());
                    command.put("y", currAction.getY());
                    try {
                        command.set("output", cardShow.cardSingle(board.
                                getBoard().get(x).get(y).getCard()));
                    } catch (Exception e) {
                        command.put("output", "No card available at that position.");
                    }

                    output.add(command);
                    continue;

                }

                if (currAction.getCommand().equals("getCardsOnTable")) {
                    command.set("output", board.showBoard());

                    output.add(command);
                    continue;
                }

                if (currAction.getCommand().equals("getFrozenCardsOnTable")) {
                    command.set("output", board.showFrozen());

                    output.add(command);
                    continue;
                }

                if (currAction.getCommand().equals("getPlayerOneWins")) {
                    command.put("output", playerOneWins);

                    output.add(command);
                    continue;
                }

                if (currAction.getCommand().equals("getPlayerTwoWins")) {
                    command.put("output", playerTwoWins);

                    output.add(command);
                    continue;
                }

                if (currAction.getCommand().equals("getTotalGamesPlayed")) {
                    command.put("output", totalGames);

                    output.add(command);
                    continue;
                }

                if (currAction.getCommand().equals("placeCard")) {
                    String outcome;
                    int hand = currAction.getHandIdx();
                    if (turn == 1) {
                        outcome = board.placeCard(hand, turn, playerHand1);
                        if (outcome.equals("0")) {
                            command.put("handIdx", currAction.getHandIdx());
                            command.put("error", "Cannot place environment card on table.");
                            output.add(command);
                            continue;
                        } else if (outcome.equals("1")) {
                            command.put("handIdx", currAction.getHandIdx());
                            command.put("error", "Not enough mana to place card on table.");
                            output.add(command);
                            continue;
                        } else if (outcome.equals("2")) {
                            command.put("handIdx", currAction.getHandIdx());
                            command.put("error", "Cannot place card on table since row is full.");
                            output.add(command);
                            continue;
                        } else if (outcome.equals("3")) {
                            playerHand1.setMana(playerHand1.getMana()
                                    - playerHand1.getCards().get(currAction.getHandIdx()).getMana());
                            playerHand1.removeCard(currAction.getHandIdx());


                        }
                    } else {
                        outcome = board.placeCard(hand, turn, playerHand2);
                        if (outcome.equals("0")) {
                            command.put("handIdx", currAction.getHandIdx());
                            command.put("error", "Cannot place environment card on table.");
                            output.add(command);
                            continue;
                        } else if (outcome.equals("1")) {
                            command.put("handIdx", currAction.getHandIdx());
                            command.put("error", "Not enough mana to place card on table.");
                            output.add(command);
                            continue;
                        } else if (outcome.equals("2")) {
                            command.put("handIdx", currAction.getHandIdx());
                            command.put("error", "Cannot place card on table since row is full.");
                            output.add(command);
                            continue;
                        } else if (outcome.equals("3")) {
                            command.put("handIdx", currAction.getHandIdx());
                            playerHand2.setMana(playerHand2.getMana()
                                    - playerHand2.getCards().get(currAction.getHandIdx()).getMana());
                            playerHand2.removeCard(currAction.getHandIdx());

                        }
                    }

                    continue;

                }

                if (currAction.getCommand().equals("useEnvironmentCard")) {
                    String outcome;
                    int hand = currAction.getHandIdx();
                    int row = currAction.getAffectedRow();
                    if (turn == 1) {
                        outcome = board.useEnvCard(playerHand1, hand, row, turn);
                    } else {
                        outcome = board.useEnvCard(playerHand2, hand, row, turn);
                    }

                    if (outcome.equals("not env")) {
                        command.put("handIdx", hand);
                        command.put("affectedRow", row);
                        command.put("error", "Chosen card is not of type environment.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("no mana")) {
                        command.put("handIdx", hand);
                        command.put("affectedRow", row);
                        command.put("error", "Not enough mana to use environment card.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("own row")) {
                        command.put("handIdx", hand);
                        command.put("affectedRow", row);
                        command.put("error", "Chosen row does not belong to the enemy.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("no space")) {
                        command.put("handIdx", hand);
                        command.put("affectedRow", row);
                        command.put("error", "Cannot steal enemy card since the player's row is full.");
                        output.add(command);
                        continue;
                    }

                    continue;

                }

                if (currAction.getCommand().equals("useHeroAbility")) {
                    String outcome;
                    int row = currAction.getAffectedRow();
                    if (turn == 1) {
                        outcome = board.useHero(row, playerHand1, turn, heroOne);
                    } else {
                        outcome = board.useHero(row, playerHand2, turn, heroTwo);
                    }

                    if (outcome.equals("not ready")) {
                        command.put("affectedRow", row);
                        command.put("error", "Hero has already attacked this turn.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("no mana")) {
                        command.put("affectedRow", row);
                        command.put("error", "Not enough mana to use hero's ability.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("friendly fire")) {

                        command.put("affectedRow", row);
                        command.put("error", "Selected row does not belong to the enemy.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("not friendly")) {
                        command.put("affectedRow", row);
                        command.put("error", "Selected row does not belong to the current player.");
                        output.add(command);
                        continue;
                    }

                    continue;

                }

                if (currAction.getCommand().equals("cardUsesAttack")) {
                    String outcome;
                    int xDef = currAction.getCardAttacked().getX();
                    int yDef = currAction.getCardAttacked().getY();
                    int xAtk = currAction.getCardAttacker().getX();
                    int yAtk = currAction.getCardAttacker().getY();
                    Coordinates atk = currAction.getCardAttacker();
                    Coordinates def = currAction.getCardAttacked();

                    outcome = board.attackCard(xAtk, yAtk, xDef, yDef);

                    ObjectNode attacker = objectMapper.createObjectNode();
                    ObjectNode defender = objectMapper.createObjectNode();

                    attacker.put("x", atk.getX());
                    attacker.put("y", atk.getY());
                    defender.put("x", def.getX());
                    defender.put("y", def.getY());

                    if (outcome.equals("friendly fire")) {
                        command.set("cardAttacker", attacker);
                        command.set("cardAttacked", defender);
                        command.put("error", "Attacked card does not belong to the enemy.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("not ready")) {
                        command.set("cardAttacker", attacker);
                        command.set("cardAttacked", defender);
                        command.put("error", "Attacker card has already attacked this turn.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("frozen")) {
                        command.set("cardAttacker", attacker);
                        command.set("cardAttacked", defender);
                        command.put("error", "Attacker card is frozen.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("not tank")) {
                        command.set("cardAttacker", attacker);
                        command.set("cardAttacked", defender);
                        command.put("error", "Attacked card is not of type 'Tank'.");
                        output.add(command);
                        continue;
                    }

                    continue;

                }

                if (currAction.getCommand().equals("useAttackHero")) {
                    String outcome;
                    int xAtk = currAction.getCardAttacker().getX();
                    int yAtk = currAction.getCardAttacker().getY();
                    Coordinates atk = currAction.getCardAttacker();
                    if (turn == 1) {
                        outcome = board.attackHero(xAtk, yAtk, healthHero2);
                    } else {
                        outcome = board.attackHero(xAtk, yAtk, healthHero1);
                    }



                    ObjectNode attacker = objectMapper.createObjectNode();


                    attacker.put("x", atk.getX());
                    attacker.put("y", atk.getY());

                    if (outcome.equals("frozen")) {
                        command.set("cardAttacker", attacker);

                        command.put("error", "Attacker card is frozen.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("not ready")) {
                        command.set("cardAttacker", attacker);

                        command.put("error", "Attacker card has already attacked this turn.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("not tank")) {
                        command.set("cardAttacker", attacker);

                        command.put("error", "Attacked card is not of type 'Tank'.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("win")) {
                        command.remove("command");
                        if (turn == 1) {
                            command.put("gameEnded", "Player one killed the enemy hero.");
                            playerOneWins++;
                            totalGames++;
                        } else {
                            command.put("gameEnded", "Player two killed the enemy hero.");
                            playerTwoWins++;
                            totalGames++;
                        }

                        output.add(command);
                        continue;
                    }

                    if (turn == 1) {
                        healthHero2 = Integer.parseInt(outcome);
                    } else {
                        healthHero1 = Integer.parseInt(outcome);
                    }

                    continue;
                }

                if (currAction.getCommand().equals("cardUsesAbility")) {
                    String outcome;
                    int xDef = currAction.getCardAttacked().getX();
                    int yDef = currAction.getCardAttacked().getY();
                    int xAtk = currAction.getCardAttacker().getX();
                    int yAtk = currAction.getCardAttacker().getY();
                    Coordinates atk = currAction.getCardAttacker();
                    Coordinates def = currAction.getCardAttacked();

                    outcome = board.abilityCard(xAtk, yAtk, xDef, yDef);

                    ObjectNode attacker = objectMapper.createObjectNode();
                    ObjectNode defender = objectMapper.createObjectNode();

                    attacker.put("x", atk.getX());
                    attacker.put("y", atk.getY());
                    defender.put("x", def.getX());
                    defender.put("y", def.getY());

                    if (outcome.equals("friendly fire")) {
                        command.set("cardAttacker", attacker);
                        command.set("cardAttacked", defender);
                        command.put("error", "Attacked card does not belong to the enemy.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("not ready")) {
                        command.set("cardAttacker", attacker);
                        command.set("cardAttacked", defender);
                        command.put("error", "Attacker card has already attacked this turn.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("not ally")) {
                        command.set("cardAttacker", attacker);
                        command.set("cardAttacked", defender);
                        command.put("error", "Attacked card does not belong to the current player.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("frozen")) {
                        command.set("cardAttacker", attacker);
                        command.set("cardAttacked", defender);
                        command.put("error", "Attacker card is frozen.");
                        output.add(command);
                        continue;
                    } else if (outcome.equals("not tank")) {
                        command.set("cardAttacker", attacker);
                        command.set("cardAttacked", defender);
                        command.put("error", "Attacked card is not of type 'Tank'.");
                        output.add(command);
                        continue;
                    }

                    continue;

                }

                if (currAction.getCommand().equals("getCardsInHand")) {
                    if (currAction.getPlayerIdx() == 1) {
                        command.put("playerIdx", currAction.getPlayerIdx());
                        command.set("output", cardShow.cardDeck(playerHand1.getCards()));

                    } else {
                        command.put("playerIdx", currAction.getPlayerIdx());
                        command.set("output", cardShow.cardDeck(playerHand2.getCards()));
                    }
                    output.add(command);
                    continue;
                }

                if (currAction.getCommand().equals("getEnvironmentCardsInHand")) {
                    if (currAction.getPlayerIdx() == 1) {
                        command.put("playerIdx", currAction.getPlayerIdx());
                        command.set("output", cardShow.showEnvCards(playerHand1.getCards()));

                    } else {
                        command.put("playerIdx", currAction.getPlayerIdx());
                        command.set("output", cardShow.showEnvCards(playerHand2.getCards()));
                    }
                    output.add(command);
                    continue;
                }

                if (currAction.getCommand().equals("getPlayerMana")) {
                    if (currAction.getPlayerIdx() == 1) {
                        command.put("playerIdx", currAction.getPlayerIdx());
                        command.put("output", playerHand1.getMana());

                    } else {
                        command.put("playerIdx", currAction.getPlayerIdx());
                        command.put("output", playerHand2.getMana());
                    }
                    output.add(command);
                    continue;
                }

                if (currAction.getCommand().equals("endPlayerTurn")) {
                    if (turn == 1) {

                        heroOne.setReady(true);
                        for (int k = turn + turn; k < board.getBoard().size(); k++) {
                            for (int l = 0; l < board.getBoard().get(k).size(); l++) {
                                Card card = board.getBoard().get(k).get(l);
                                card.setFrozen(turn - turn);
                                card.setReady(true);

                            }
                        }
                        round++;

                    } else {

                        heroTwo.setReady(true);
                        for (int k = 0; k < turn; k++) {
                            for (int l = 0; l < board.getBoard().get(k).size(); l++) {
                                Card card = board.getBoard().get(k).get(l);
                                card.setFrozen(turn - turn);
                                card.setReady(true);
                            }
                        }
                        round++;
                    }

                    turn++;

                }

            }

        }


        return output;

    }
}
