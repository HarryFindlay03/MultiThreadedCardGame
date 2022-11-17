import cards.*;

import java.io.ByteArrayOutputStream;
import java.util.Random;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class testCardGame {
    CardGame cardGame;
    int numPlayers;

    @BeforeEach
    void setUp() throws InvalidPackException, FileNotFoundException {
        numPlayers = 4;
        cardGame = new CardGame(numPlayers, "packs/" + numPlayers + ".txt");
        cardGame.gameSetup();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 2, 4, 5, 49, -999})
    void testGameSetup(int numPlayers) throws InvalidPackException, FileNotFoundException {
        String filename = "packs/" + String.valueOf(numPlayers) + ".txt";
        if(numPlayers <= 0) {
            assertThrows(InvalidPackException.class, () -> {
                CardGame cg = new CardGame(numPlayers, filename);
            });
        } else {
            CardGame cg = new CardGame(numPlayers, filename);
            cg.gameSetup();
            assertEquals(numPlayers, cg.getPlayersInGame().size());
            assertEquals(numPlayers, cg.getDecksInGame().size());
        }
    }

    @Test
    void testPlayers() {
        assertEquals(numPlayers, cardGame.getPlayersInGame().size());
    }

    @Test
    void testDecks() {
        assertEquals(numPlayers, cardGame.getDecksInGame().size());
    }

    @Test
    void testInvalidInput() {
        assertThrows(InvalidPackException.class, () -> {
           CardGame invalidCardGame = new CardGame(-1, "test.txt");
        });
    }

    @Test
    void testPackLength() {
        int expectedPackLength = cardGame.getPlayersInGame().size() * 8 - (4 * cardGame.getPlayersInGame().size()) - (4 * cardGame.getDecksInGame().size());
        assertEquals(expectedPackLength, cardGame.getPack().size());
    }

    @ParameterizedTest
    @ValueSource(ints = {-99, 10, 5, 4, 6, 7})
    void testPack(int numPlayers) throws InvalidPackException, FileNotFoundException {
        if(numPlayers <= 0) {
            assertThrows(InvalidPackException.class, () -> {
                CardGame invalidCardGame = new CardGame(numPlayers, "test.txt");
            });
        } else {
            String filename = "packs/" + String.valueOf(numPlayers) + ".txt";
            CardGame cg = new CardGame(numPlayers, filename);
            cg.gameSetup();
            int expectedPackLength = cardGame.getPlayersInGame().size() * 8 - (4 * cardGame.getPlayersInGame().size()) - (4 * cardGame.getDecksInGame().size());
            assertEquals(expectedPackLength, cg.getPack().size());
        }
    }

    @Test
    void testFileNotFound() {
        assertThrows(FileNotFoundException.class, () -> {
            CardGame cg = new CardGame(4, "fff.txt");
        });
    }

    @Test
    void testPlayerHand() {
        for(int i = 0; i < cardGame.getPlayersInGame().size(); i++) {
            assertEquals(4, cardGame.getPlayersInGame().get(i).getPlayerHand().size());
        }
    }

    @Test
    void testDeckCards() {
        for(int i = 0; i < cardGame.getPlayersInGame().size(); i++) {
            assertEquals(4, cardGame.getDecksInGame().get(i).getDeckCards().size());
        }
    }

    @Nested
    class gameplayTests {
        ArrayList<Player> playersInGame;
        ArrayList<Deck> decksInGame;

        @BeforeEach
        void setUp() {
            playersInGame = cardGame.getPlayersInGame();
            decksInGame = cardGame.getDecksInGame();
        }

        @AfterEach
        void tearDown() {
            playersInGame.clear();
            decksInGame.clear();
        }

        @Test
        void testPickUpCard() {
            for(int i = 0; i < playersInGame.size(); i++) {
                cardGame.pickUpCard(playersInGame.get(i));
                assertEquals(5, playersInGame.get(i).getPlayerHand().size());
                assertEquals(3, decksInGame.get(i).getDeckCards().size());
            }
        }

        @Test
        void testDiscardCard() {
            for(int i = 0; i < playersInGame.size(); i++) {
                cardGame.discardCard(playersInGame.get(i));
                assertEquals(3, playersInGame.get(i).getPlayerHand().size());

                if(playersInGame.get(i).getPlayerId() + 1 == playersInGame.size()) {
                    assertEquals(5, decksInGame.get(0).getDeckCards().size());
                }
                else {
                    assertEquals(5, decksInGame.get(i + 1).getDeckCards().size());
                }
            }
        }

        @Test
        void testGetCardToDiscard() {
            for(int i = 0; i < playersInGame.size(); i++) {
                Player player = playersInGame.get(i);
                Card card = cardGame.getCardToDiscard(player);
                assertNotEquals(player.getPreference(), card.getCardValue());
            }
        }

        @Test
        void testCheckWin() {
            playersInGame.get(0).setWinningPlayerHand();
            assertEquals(true, cardGame.checkWin(playersInGame.get(0)));
        }

        @Test
        void testPlayer1WinsImmediately() throws InvalidPackException, FileNotFoundException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CardGame cg = new CardGame(2, "tests/res/2_pl1wins.txt");

            cg.gameSetup();
            for(Player p : cg.getPlayersInGame()) {
                System.out.printf("Player %d\n", p.getPlayerId());
            }
            cg.gameRun();

            assertEquals(cardGame.getPlayersInGame().get(0), cardGame.getWinningPlayer()); //Player 1
        }

        @Test
        void testGameWithSamePlayers() throws InvalidPackException, FileNotFoundException {
            CardGame cg;
            for(int i = 1; i < 101; i++) {
                cg = new CardGame(i, String.format("packs/%d.txt", i));
                cg.gameSetup();
                cg.gameRun();
            }
        }
    }
}
