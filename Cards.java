import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import javalib.impworld.WorldScene;
import javalib.worldimages.*;
import tester.Tester;

//---------------------- CARD CLASS -------------------------------------------

// represents a Card
class Card implements IGameConstants {
  int rank; // 1 = ace, ... number cards ... , 11 = jack, 12 = queen, 13 = king
  String suit;
  boolean faceUp;
  Color color;
  Posn pos;

  //---------------------- CONSTRUCTOR ----------------------------------------

  Card(int val, String suit) {
    this.pos = new Posn(0, 0);

    this.rank = val;
    this.suit = suit;

    this.faceUp = false;

    if (this.suit.equals(CLUBS) || this.suit.equals(SPADES)) {
      this.color = Color.BLACK;
    }
    else {
      this.color = Color.RED;
    }
  }

  /* TEMPLATE:
   * fields:
   *  this.rank ... int
   *  this.suit ... String
   *  this.faceUp ... boolean
   *  this.color ... Color
   *  this.pos ... Posn
   * methods:
   *  this.isMatch ... boolean
   *  this.sameCard ... boolean
   *  this.flip ... void
   *  this.setPosn ... void
   *  this.wasClicked ... boolean
   *  this.getImg ... WorldImage
   *  this.rankToString ... String 
   * methods for fields:
   *  
   */

  //---------------------- CARD FUNCTIONS -------------------------------------

  // returns if this card and the other card have the same rank and color
  // cards of rank zero are not valid and should return false
  public boolean isMatch(Card other) {
    return this.rank != 0 && this.rank == other.rank && this.color.equals(other.color);
  }

  //returns if this card and the other card are the same
  public boolean sameCard(Card other) {
    return this.rank == other.rank && this.suit.equals(other.suit);
  }

  // flip the Card
  public void flip() {
    this.faceUp = !this.faceUp;
  }

  // set the Position of this Card to given coordinates
  public void setPosn(int x, int y) {
    this.pos = new Posn(x, y);
  }

  // return if the mouse position is in this Card
  public boolean wasClicked(Posn p) {
    int x = p.x + CARD_WIDTH / 2;
    int y = p.y + CARD_HEIGHT / 2;
    return (x >= this.pos.x) && (x <= (this.pos.x + CARD_WIDTH))
        && (y > this.pos.y) && (y <= (this.pos.y + CARD_HEIGHT)); 
  }

  // return the image of this card
  public WorldImage getImg() {
    if (this.faceUp) {
      return new OverlayImage(new TextImage(
          this.rankToString() + " of " + this.suit, CARD_WIDTH / 4, FontStyle.BOLD, this.color),
          new RectangleImage(CARD_WIDTH,CARD_HEIGHT, OutlineMode.OUTLINE, this.color));
    }
    else {
      return FACEDOWN_CARD;
    }
  }

  // return the string representation of this card's rank
  public String rankToString() {
    switch (this.rank) {
      case 1:
        return "A"; // 1 = Ace
      case 11:
        return "J"; // 11 = Jack
      case 12:
        return "Q"; // 12 = Queen
      case 13:
        return "K"; // 13 = King
      default:
        return this.rank + ""; // other numbers
    }
  }
}

//---------------------- DECK CLASS -------------------------------------------

// represents a standard deck
class Deck implements IGameConstants {
  ArrayList<Card> cards;
  Random rand;

  //---------------------- CONSTRUCTORS ---------------------------------------

  // default constructor
  Deck() {
    this.initCardsDefault();
    this.rand = new Random();
  }

  Deck(int seed) {
    this.initCardsDefault();
    this.rand = new Random(seed);
  }

  /* TEMPLATE:
   * fields:
   *  this.cards ... ArrayList<Card>
   *  this.rand ... Random
   * methods:
   *  this.initCardsDefault ... void
   *  this.shuffle ... void
   *  this.drawCard ... Card
   * methods for fields:
   */

  //---------------------- DECK FUNCTIONS -------------------------------------

  // Creates a sorted standard 52 card deck (Ace = 1)
  void initCardsDefault() {
    this.cards = new ArrayList<Card>(52);

    for (int i = 1; i < 14; i++) {
      this.cards.add(new Card(i, CLUBS));
      this.cards.add(new Card(i, DIAMONDS));
      this.cards.add(new Card(i, HEARTS));
      this.cards.add(new Card(i, SPADES));
    }
  }

  // shuffles this deck
  void shuffle() {
    ArrayList<Card> temp = new ArrayList<Card>();

    for (int i = this.cards.size(); i > 0; i--) {
      temp.add(this.cards.remove(this.rand.nextInt(i)));
    }

    this.cards = temp;
  }

  // draw a Card from this deck. If empty, return null.
  Card drawCard() {
    if (this.cards.isEmpty()) {
      return new Card(0, ""); // return an invalid card
    }
    else {
      return this.cards.remove(0);
    }
  }
}

//---------------------- BOARD CLASS ------------------------------------------

// represents the game board
class Board implements IGameConstants {
  ArrayList<Card> cards;

  //---------------------- CONSTRUCTORS ---------------------------------------

  // constructor
  Board(Deck deck) {
    this.initCards(deck);
  }

  /* TEMPLATE:
   * fields:
   *  this.cards ... ArrayList<Card>
   * methods:
   *  this.initCards ... void
   *  this.getClicked ... Card
   *  this.draw ... WorldScene
   *  this.removeCard ... void
   * methods for fields:
   */

  // Used in Constructor
  // create a 4 x 13 board using the given deck
  // expects a 52 card deck as input
  void initCards(Deck d) {

    /* TEMPLATE:
     * everything above plus:
     * fields of parameter:
     *  d.cards ... ArrayList<Card>
     *  d.rand ... Random
     * methods for parameter:
     *  d.drawCard ... Card
     * methods for fields of parameter:
     */

    // create a new ArrayList of size ROWS * COLS (52 by default constants)
    this.cards = new ArrayList<Card>(ROWS * COLS);

    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {
        // draw a card from given deck
        this.cards.add(d.drawCard());
        // set the position of the card based on its row and column indices
        this.cards.get(r * COLS + c)
            .setPosn(c * (CARD_WIDTH + SPACE) + EDGE_OFFSET + CARD_WIDTH / 2,
            r * (CARD_HEIGHT + SPACE) + EDGE_OFFSET + CARD_HEIGHT / 2);
      }
    }
  }

  //---------------------- BOARD FUNCTIONS ------------------------------------

  // get the card that's been clicked
  Card getClicked(Posn p) {

    /* TEMPLATE:
     * everything above plus:
     * fields of parameter:
     *  p.x ... int
     *  p.y ... int
     * methods for parameter:
     * methods for fields of parameter:
     */

    for (Card c : this.cards) {
      // check if current card was clicked and return it if true
      if (c.wasClicked(p)) {
        return c;
      }
    }

    // return invalid card if no card has been clicked
    return new Card(0, "");
  }


  // draw this Collection onto the WorldScene
  public WorldScene draw(WorldScene background) {
    for (Card c : this.cards) {
      background.placeImageXY(c.getImg(), c.pos.x, c.pos.y);
    }

    return background;
  }

  // remove a card from this board
  void removeCard(Card c) {

    /* TEMPLATE:
     * everything above plus:
     * fields of parameter:
     *  c.rank ... int
     *  c.suit ... String
     *  c.faceUp ... boolean
     *  c.color ... Color
     *  c.pos ... Posn
     * methods for parameter:
     * methods for fields of parameter:
     */

    this.cards.remove(c);
  }
}

//---------------------- EXAMPLES CLASS ---------------------------------------

//represents Examples and Tests of Cards, Decks, and Boards
class ExamplesCards implements IGameConstants {

  // represents examples of valid Card
  Card card1;
  Card card2;
  Card card3;
  Card card4;
  Card card5;
  Card card6;
  Card card7;
  Card card8;
  Card card9;
  Card card10;
  Card card11;

  // represents examples of invalid Card
  Card cardInvalid;

  // represents examples of Posn
  Posn posn1;
  Posn posn2;
  Posn posn3;

  // represents examples of Deck
  Deck deck1;
  Deck deck2;
  Deck deck3;
  Deck deck4;
  Deck deck5;

  // represents examples of Board
  Board board1;

  //-------------------- INITDATA FUNCTION ------------------------------------

  //initializes examples
  void initData() {

    // represents examples of valid Card
    this.card1 = new Card(1, SPADES);
    this.card2 = new Card(3, CLUBS);
    this.card3 = new Card(10, DIAMONDS);
    this.card4 = new Card(13, HEARTS);
    this.card5 = new Card(1, CLUBS);
    this.card6 = new Card(10, HEARTS);
    this.card7 = new Card(1,SPADES);
    this.card8 = new Card(1, CLUBS);
    this.card9 = new Card(13, SPADES);
    this.card10 = new Card(8, HEARTS);
    this.card11 = new Card(1, DIAMONDS);

    // represents examples of invalid Card
    this.cardInvalid = new Card(0, "");

    // represents examples of Posn
    this.posn1 = new Posn(0, 0);
    this.posn2 = new Posn(40, 50);
    this.posn3 = new Posn(170, 200);

    // represents examples of Deck
    this.deck1 = new Deck();
    this.deck2 = new Deck(8);
    this.deck3 = new Deck(13);
    this.deck4 = new Deck(50);
    this.deck5 = new Deck();

    // represents examples of board
    this.board1 = new Board(this.deck5);
  }

  //-------------------- CARD FUNCTION TESTS ----------------------------------

  // represents tests for isMatch
  void testIsMatch(Tester t) {
    this.initData();

    t.checkExpect(this.card1.isMatch(this.card5), true);
    t.checkExpect(this.card1.isMatch(this.card2), false);
    t.checkExpect(this.card1.isMatch(this.cardInvalid), false);
    t.checkExpect(this.cardInvalid.isMatch(this.cardInvalid), false);
  }

  // represents tests for sameCard
  void testSameCard(Tester t) {
    this.initData();

    t.checkExpect(this.card1.sameCard(this.card7), true);
    t.checkExpect(this.card1.sameCard(this.card2), false);
    t.checkExpect(this.card1.sameCard(this.cardInvalid), false);
    t.checkExpect(this.cardInvalid.sameCard(this.cardInvalid), true);
  }

  // represents tests for flip
  void testFlip(Tester t) {
    this.initData();

    t.checkExpect(this.card1.faceUp, false);
    this.card1.flip();
    t.checkExpect(this.card1.faceUp, true);
    this.card1.flip();
    t.checkExpect(this.card1.faceUp, false);

  }

  // represents tests for setPosn
  void testSetPosn(Tester t) {
    this.initData();

    t.checkExpect(this.card2.pos, new Posn(0, 0));
    this.card2.setPosn(8, 10);
    t.checkExpect(this.card2.pos, new Posn(8, 10));
  }

  // represents tests for wasClicked
  void testWasClicked(Tester t) {
    this.initData();

    t.checkExpect(this.card1.wasClicked(this.posn1), true);

    t.checkExpect(this.card1.pos, new Posn(0, 0));
    this.card1.setPosn(60, 60);
    t.checkExpect(this.card1.pos, new Posn(60, 60));
    t.checkExpect(this.card1.wasClicked(this.posn1), false);
    t.checkExpect(this.card1.wasClicked(this.posn2), true);


    t.checkExpect(this.card2.pos, new Posn(0,0));
    this.card2.setPosn(155, 185);
    t.checkExpect(this.card2.pos, new Posn(155, 185));
    t.checkExpect(this.card2.wasClicked(this.posn1), false);
    t.checkExpect(this.card2.wasClicked(this.posn2), false);
    t.checkExpect(this.card2.wasClicked(this.posn3), true);

    t.checkExpect(this.cardInvalid.wasClicked(this.posn1), true);
    t.checkExpect(this.cardInvalid.wasClicked(this.posn2), false);
  }

  // represents tests for getImg
  void testGetImg(Tester t) {
    this.initData();

    t.checkExpect(this.card1.faceUp, false);
    t.checkExpect(this.card1.getImg(), FACEDOWN_CARD);
    this.card1.flip();
    t.checkExpect(this.card1.faceUp, true);
    t.checkExpect(this.card1.getImg(), 
        new OverlayImage(new TextImage(
            "A" + " of " + SPADES, CARD_WIDTH / 4, FontStyle.BOLD, Color.black),
            new RectangleImage(CARD_WIDTH,CARD_HEIGHT, OutlineMode.OUTLINE, Color.black)));

    t.checkExpect(this.card2.faceUp, false);
    t.checkExpect(this.card2.getImg(), FACEDOWN_CARD);
    this.card2.flip();
    t.checkExpect(this.card2.faceUp, true);
    t.checkExpect(this.card2.getImg(), 
        new OverlayImage(new TextImage(
            3 + " of " + CLUBS, CARD_WIDTH / 4, FontStyle.BOLD, Color.black),
            new RectangleImage(CARD_WIDTH,CARD_HEIGHT, OutlineMode.OUTLINE, Color.black)));

    t.checkExpect(this.cardInvalid.faceUp, false);
    t.checkExpect(this.cardInvalid.getImg(), FACEDOWN_CARD);
    this.cardInvalid.flip();
    t.checkExpect(this.cardInvalid.getImg(),
        new OverlayImage(new TextImage(
            0 + " of " + "", CARD_WIDTH / 4, FontStyle.BOLD, Color.red),
            new RectangleImage(CARD_WIDTH,CARD_HEIGHT, OutlineMode.OUTLINE, Color.red)));
  }

  // represents tests for rankToString
  void testRankToString(Tester t) {
    this.initData();

    t.checkExpect(this.card1.rankToString(), "A");
    t.checkExpect(new Card(11, SPADES).rankToString(), "J");
    t.checkExpect(new Card(12, HEARTS).rankToString(), "Q");
    t.checkExpect(new Card(13, CLUBS).rankToString(), "K");
    t.checkExpect(this.card2.rankToString(), "3");
  }

  //-------------------- DECK FUNCTION TESTS ----------------------------------

  // represents tests for initCardsDefault
  void testInitCardsDefault(Tester t) {
    this.initData();

    // checks to see if first 4 elements of initialized deck are correct
    t.checkExpect(this.deck1.cards.get(0), new Card(1, CLUBS));
    t.checkExpect(this.deck1.cards.get(1), new Card(1, DIAMONDS));
    t.checkExpect(this.deck1.cards.get(2), new Card(1, HEARTS));
    t.checkExpect(this.deck1.cards.get(3), new Card(1, SPADES));

    //checks to see if the last 4 elements of initialized deck are correct
    t.checkExpect(this.deck1.cards.get(48), new Card(13, CLUBS));
    t.checkExpect(this.deck1.cards.get(49), new Card(13, DIAMONDS));
    t.checkExpect(this.deck1.cards.get(50), new Card(13, HEARTS));
    t.checkExpect(this.deck1.cards.get(51), new Card(13, SPADES));

    //checks to see if elements in middle of initialied deck is correct
    t.checkExpect(this.deck1.cards.get(20), new Card(6, CLUBS));
    t.checkExpect(this.deck1.cards.get(8), new Card(3, CLUBS));
    t.checkExpect(this.deck1.cards.get(39), new Card(10, SPADES));
  }

  // represents tests for shuffle
  void testShuffle(Tester t) {
    this.initData();

    t.checkExpect(this.deck2.cards.get(0), new Card(1, CLUBS));
    t.checkExpect(this.deck2.cards.get(1), new Card(1, DIAMONDS));
    t.checkExpect(this.deck2.cards.get(51), new Card(13, SPADES));
    this.deck2.shuffle();
    t.checkExpect(this.deck2.cards.get(0), new Card(11, CLUBS));
    t.checkExpect(this.deck2.cards.get(1), new Card(11, DIAMONDS));
    t.checkExpect(this.deck2.cards.get(51), new Card(13, SPADES));

    this.initData();

    t.checkExpect(this.deck3.cards.get(0), new Card(1, CLUBS));
    this.deck3.shuffle();
    t.checkExpect(this.deck3.cards.get(0), new Card(4, CLUBS));

    this.initData();

    t.checkExpect(this.deck4.cards.get(0), new Card(1, CLUBS));
    this.deck4.shuffle();
    t.checkExpect(this.deck4.cards.get(0), new Card(10, DIAMONDS));
  }

  // represents tests for drawCard
  void testDrawCard(Tester t) {
    this.initData();

    t.checkExpect(this.deck1.cards.get(0), new Card(1, CLUBS));
    t.checkExpect(this.deck1.drawCard(), new Card(1, CLUBS));
    t.checkExpect(this.deck1.cards.get(0), new Card(1, DIAMONDS));

    t.checkExpect(this.deck1.drawCard(), new Card(1, DIAMONDS));
    t.checkExpect(this.deck1.cards.get(0), new Card(1, HEARTS));

    t.checkExpect(this.deck1.drawCard(), new Card(1, HEARTS));
    t.checkExpect(this.deck1.cards.get(0), new Card(1, SPADES)); 
  }

  //-------------------- BOARD FUNCTION TESTS ---------------------------------

  // represents tests for initCards
  void testInitCards(Tester t) {
    this.initData();

    this.card8.setPosn(97, 112);
    t.checkExpect(this.board1.cards.get(0), this.card8);

    this.card9.setPosn(1237, 487);
    t.checkExpect(this.board1.cards.get(51), this.card9);

    this.card10.setPosn(477, 362);
    t.checkExpect(this.board1.cards.get(30), this.card10);
  }

  // represents tests for getClicked
  void testGetClicked(Tester t) {
    this.initData();

    this.card8.setPosn(97, 112);
    t.checkExpect(this.board1.getClicked(new Posn(80, 80)), this.card8);

    this.card9.setPosn(1237,  487);
    t.checkExpect(this.board1.getClicked(new Posn(1210, 450)), this.card9);

    t.checkExpect(this.board1.getClicked(new Posn(1000000, 1000000)), this.cardInvalid);
  }

  // represents tests for draw
  void testDraw(Tester t) {
    this.initData();

    WorldScene boardScene = new WorldScene(GAME_WIDTH, GAME_HEIGHT);
    for (int r = 0; r < ROWS; r++) {
      for (int c = 0; c < COLS; c++) {
        boardScene.placeImageXY(FACEDOWN_CARD,
            c * (CARD_WIDTH + SPACE) + EDGE_OFFSET + CARD_WIDTH / 2,
            r * (CARD_HEIGHT + SPACE) + EDGE_OFFSET + CARD_HEIGHT / 2);
      }
    }

    t.checkExpect(this.board1.draw(new WorldScene(GAME_WIDTH, GAME_HEIGHT)), boardScene);
  }

  // represents tests for removeCard
  void testRemoveCard(Tester t) {
    this.initData();

    Card temp = this.board1.cards.get(1);
    this.card8.setPosn(97, 112);

    t.checkExpect(this.board1.cards.get(0), this.card8);
    this.board1.removeCard(this.board1.cards.get(0));
    t.checkExpect(this.board1.cards.get(0), temp);

    temp = this.board1.cards.get(30);
    this.card10.setPosn(477, 362);

    t.checkExpect(this.board1.cards.get(29), this.card10);
    this.board1.removeCard(this.board1.cards.get(29));
    t.checkExpect(this.board1.cards.get(29), temp);
  }
}