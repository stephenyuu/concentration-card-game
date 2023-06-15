import tester.*;

import java.awt.Color;

import javalib.impworld.*;
import javalib.worldimages.*;

// represents Constants that are used in the Game
// Some of these constants are not used by all the classes relevant to the game.
// But, since these were also used to help with testing, we have kept them all in one interface.
interface IGameConstants {
  // suits
  static String CLUBS = "♣";
  static String DIAMONDS = "♦";
  static String HEARTS = "♥";
  static String SPADES = "♠";

  // board dimensions
  static int ROWS = 4; // number of rows
  static int COLS = 13; // number of columns
  static int SPACE = 20; // space between each row and column

  // card dimensions
  static int SCALE = 3;
  static int CARD_WIDTH = 25 * SCALE; // width of each card. Standard cards are 25 x 35
  static int CARD_HEIGHT = 35 * SCALE; // height of each card. Standard cards are 25 x 35

  // game dimensions
  static int EDGE_OFFSET = SCALE * SPACE; // offset of game from edge
  static int GAME_WIDTH = 2 * EDGE_OFFSET + COLS * (CARD_WIDTH + SPACE);
  static int GAME_HEIGHT = 2 * EDGE_OFFSET + 3 * CARD_HEIGHT + ROWS * (CARD_HEIGHT + SPACE);

  // image for all facedown cards
  static WorldImage FACEDOWN_CARD = new RectangleImage(
      CARD_WIDTH, CARD_HEIGHT, OutlineMode.OUTLINE, Color.BLACK);

  // notification for match
  static WorldImage MATCH_WINDOW = new OverlayImage(
      new AboveImage(
          new TextImage("Match!", CARD_WIDTH / 2, FontStyle.BOLD, Color.BLACK),
          new TextImage("(Click anywhere to continue)", CARD_WIDTH / 4, 
              FontStyle.BOLD, Color.BLACK)),
      new RectangleImage(4 * CARD_WIDTH, 3 * CARD_HEIGHT, OutlineMode.OUTLINE, Color.BLACK));

  // notification for no match
  static WorldImage NO_MATCH_WINDOW = new OverlayImage(
      new AboveImage(
          new TextImage("No Match!", CARD_WIDTH / 2, FontStyle.BOLD, Color.BLACK),
          new TextImage("(Click anywhere to continue)", CARD_WIDTH / 4, 
              FontStyle.BOLD, Color.BLACK)),
      new RectangleImage(4 * CARD_WIDTH, 3 * CARD_HEIGHT, OutlineMode.OUTLINE, Color.BLACK));
}

class Concentration extends World implements IGameConstants {
  Deck deck; // standard deck of 52 cards
  Board board; // 4 x 13 array of cards on board

  int score; // number of pairs remaining. When 0, game ends

  int stage; // "stage" of the game. Int from 0 - 2
  // 0 : player picks first card
  // 1 : player picks second card
  // 2 : "match" / "no match" window appears and game pauses

  Card pick1; // first card picked by player
  Card pick2; // second card picked by player
  boolean match; // are the picked cards a pair

  // constructor
  Concentration() {
    this.initGame();
  }


  /* TEMPLATE:
   * fields:
   *  this.deck ... Deck
   *  this.board ... Board
   *  this.score ... int
   *  this.stage ... int
   *  this.pick1 ... Card
   *  this.pick2 ... Card
   *  this.match ... boolean
   * methods:
   *  this.initGame ... void
   *  this.onMouseReleased ... void
   *  this.onKeyEvent ... void
   *  this.makeScene ... WorldScene
   *  this.lastScene ... WorldScene
   * methods for fields:
   *  this.deck.initCardsDefault ... void
   *  this.deck.shuffle ... void
   *  this.deck.drawCard ... Card
   *  this.board.initCards ... void
   *  this.board.getClicked ... Card
   *  this.board.draw ... WorldScene
   *  this.board.removeCard ... void
   *  this.board.getClicked ... Card
   *  this.pick1.isMatch ... boolean
   *  this.pick1.sameCard ... boolean
   *  this.pick1.flip ... void
   *  this.pick1.setPosn ... void
   *  this.pick1.wasClicked ... boolean
   *  this.pick1.getImg ... WorldImage
   *  this.pick1.rankToString ... String
   *  this.pick2.isMatch ... boolean
   *  this.pick2.sameCard ... boolean
   *  this.pick2.flip ... void
   *  this.pick2.setPosn ... void
   *  this.pick2.wasClicked ... boolean
   *  this.pick2.getImg ... WorldImage
   *  this.pick2.rankToString ... String
   */

  // initialize all variables to starting values
  void initGame() {
    this.deck = new Deck();
    this.deck.shuffle();
    this.board = new Board(this.deck);

    this.score = 26;

    this.stage = 0;
    this.pick1 = new Card(0, "");
    this.pick2 = new Card(0, "");
    this.match = false;
  }

  // onMouseReleased function
  public void onMouseReleased(Posn pos) {
    
    /* TEMPLATE:
     * everything above plus:
     * fields of parameter:
     *  pos.x ... int
     *  pos.y ... int
     * methods for parameter:
     * methods for fields of parameter:
     */
    
    // if in "after-choosing-paused" stage
    if (this.stage == 2) {
      // reset game to "first pick" stage
      this.stage = 0;

      // check if chosen cards match
      if (this.match) {
        // remove matching cards from board
        this.board.removeCard(this.pick1);
        this.board.removeCard(this.pick2);

        // deincrement score
        this.score--;

        // if score eq 0, end game
        if (score == 0) {
          this.endOfWorld("You Win! Woooo!!!");
        }
      }
      // if cards didn't match
      else {
        // flip non-matching cards back over
        this.pick1.flip();
        this.pick2.flip();        
      }        
    }
    // if in one of the "choosing" stages
    else {
      // get the card that was picked
      // if the player does not click on a card, chosen will be a card with rank = 0 and suit = ""
      Card chosen = this.board.getClicked(pos);

      // check if the player clicked on an actual card
      if (chosen.rank != 0) {
        // if this is the first pick
        if (this.stage == 0) {
          // flip the chosen card over
          chosen.flip();

          // move onto the next stage
          this.stage = 1;

          // set pick1 to chosen card
          this.pick1 = chosen;
        }
        // if this is the second pick(we know it is the 2nd pick because the stage is not 0 or 2)
        // check that the second pick is different from the first
        else if (!this.pick1.sameCard(chosen)) {
          // flip the chosen card over
          chosen.flip();

          // move onto the next stage
          this.stage = 2;

          // set pick2 to chosen card
          this.pick2 = chosen;

          // check if the chosen cards are the same rank
          // and store the boolean in match
          this.match = this.pick1.isMatch(this.pick2);
        }
      }
    }
  }

  // onKeyEvent function
  public void onKeyEvent(String key) {
    // reset game if r is pressed
    if (key.equals("r")) {
      this.initGame();
    }
  }

  // makeScene function
  public WorldScene makeScene() {
    // create new scene using draw method in board with an empty scene as background
    WorldScene scene = this.board.draw(getEmptyScene());

    // if in the "match / no-match" stage
    if (this.stage == 2) {
      // draw yes-match window
      if (this.match) {
        scene.placeImageXY(MATCH_WINDOW, GAME_WIDTH / 2, GAME_HEIGHT - 2 * CARD_HEIGHT);
      }
      // draw no-match window
      else {
        scene.placeImageXY(NO_MATCH_WINDOW, GAME_WIDTH / 2, GAME_HEIGHT - 2 * CARD_HEIGHT);
      }
    }
    // return scene
    return scene;
  }

  // lastScene function
  public WorldScene lastScene(String msg) {
    WorldScene scene = getEmptyScene();
    scene.placeImageXY(new OverlayImage(
        new TextImage(msg, CARD_WIDTH, FontStyle.BOLD, Color.BLACK),
        new RectangleImage(GAME_WIDTH, GAME_HEIGHT, OutlineMode.OUTLINE, Color.BLACK)), 
        GAME_WIDTH / 2, GAME_HEIGHT / 2);

    return scene;
  }
}


// Examples for the other classes are in Cards.java file
// represents examples
class ExamplesConcentration implements IGameConstants {
  // runs the game
  void testBigBang(Tester t) {
    Concentration game = new Concentration();
    game.bigBang(GAME_WIDTH,GAME_HEIGHT);
  }
}
