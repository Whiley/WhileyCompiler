type nat is (int x) where x >= 0

// =======================================
// Card Definition
// =======================================

// Suites
int HEARTS = 1
int CLUBS = 2
int DIAMONDS = 3
int SPADES = 4

// Ordinals
int TWO = 2
int THREE = 3
int FOUR = 4
int FIVE = 5
int SIX = 6
int SEVEN = 7
int EIGHT = 8
int NINE = 9
int TEN = 10
int JACK = 11
int QUEEN = 12
int KING = 13
int ACE = 14

type Card is {
    int suite,
    int ordinal
}
// Suite between hearts and clubs
where HEARTS <= suite && suite <= SPADES
// Ordinal between 2 and ACE (high)
where 2 <= ordinal && ordinal <= ACE

// Define a "comparator" for cards
property lessThan(Card c1, Card c2)
where (c1.suite < c2.suite) || (c1.suite == c1.suite && c1.ordinal < c2.ordinal)

property sorted(Card[] cards)
where |cards| == 0 || all { i in 1..|cards| | lessThan(cards[i-1],cards[i]) }

// =======================================
// Card Constants
// =======================================

Card TWO_HEARTS = { suite: HEARTS, ordinal: TWO }
Card THREE_HEARTS = { suite: HEARTS, ordinal: THREE }
Card FOUR_HEARTS = { suite: HEARTS, ordinal: FOUR }
Card FIVE_HEARTS = { suite: HEARTS, ordinal: FIVE }
Card SIX_HEARTS = { suite: HEARTS, ordinal: SIX }
Card SEVEN_HEARTS = { suite: HEARTS, ordinal: SEVEN }
Card EIGHT_HEARTS = { suite: HEARTS, ordinal: EIGHT }
Card NINE_HEARTS = { suite: HEARTS, ordinal: NINE }
Card TEN_HEARTS = { suite: HEARTS, ordinal: TEN }
Card JACK_HEARTS = { suite: HEARTS, ordinal: JACK }
Card QUEEN_HEARTS = { suite: HEARTS, ordinal: QUEEN }
Card KING_HEARTS = { suite: HEARTS, ordinal: KING }
Card ACE_HEARTS = { suite: HEARTS, ordinal: ACE }

Card TWO_CLUBS = { suite: CLUBS, ordinal: TWO }
Card THREE_CLUBS = { suite: CLUBS, ordinal: THREE }
Card FOUR_CLUBS = { suite: CLUBS, ordinal: FOUR }
Card FIVE_CLUBS = { suite: CLUBS, ordinal: FIVE }
Card SIX_CLUBS = { suite: CLUBS, ordinal: SIX }
Card SEVEN_CLUBS = { suite: CLUBS, ordinal: SEVEN }
Card EIGHT_CLUBS = { suite: CLUBS, ordinal: EIGHT }
Card NINE_CLUBS = { suite: CLUBS, ordinal: NINE }
Card TEN_CLUBS = { suite: CLUBS, ordinal: TEN }
Card JACK_CLUBS = { suite: CLUBS, ordinal: JACK }
Card QUEEN_CLUBS = { suite: CLUBS, ordinal: QUEEN }
Card KING_CLUBS = { suite: CLUBS, ordinal: KING }
Card ACE_CLUBS = { suite: CLUBS, ordinal: ACE }

Card TWO_DIAMONDS = { suite: DIAMONDS, ordinal: TWO }
Card THREE_DIAMONDS = { suite: DIAMONDS, ordinal: THREE }
Card FOUR_DIAMONDS = { suite: DIAMONDS, ordinal: FOUR }
Card FIVE_DIAMONDS = { suite: DIAMONDS, ordinal: FIVE }
Card SIX_DIAMONDS = { suite: DIAMONDS, ordinal: SIX }
Card SEVEN_DIAMONDS = { suite: DIAMONDS, ordinal: SEVEN }
Card EIGHT_DIAMONDS = { suite: DIAMONDS, ordinal: EIGHT }
Card NINE_DIAMONDS = { suite: DIAMONDS, ordinal: NINE }
Card TEN_DIAMONDS = { suite: DIAMONDS, ordinal: TEN }
Card JACK_DIAMONDS = { suite: DIAMONDS, ordinal: JACK }
Card QUEEN_DIAMONDS = { suite: DIAMONDS, ordinal: QUEEN }
Card KING_DIAMONDS = { suite: DIAMONDS, ordinal: KING }
Card ACE_DIAMONDS = { suite: DIAMONDS, ordinal: ACE }

Card TWO_SPADES = { suite: SPADES, ordinal: TWO }
Card THREE_SPADES = { suite: SPADES, ordinal: THREE }
Card FOUR_SPADES = { suite: SPADES, ordinal: FOUR }
Card FIVE_SPADES = { suite: SPADES, ordinal: FIVE }
Card SIX_SPADES = { suite: SPADES, ordinal: SIX }
Card SEVEN_SPADES = { suite: SPADES, ordinal: SEVEN }
Card EIGHT_SPADES = { suite: SPADES, ordinal: EIGHT }
Card NINE_SPADES = { suite: SPADES, ordinal: NINE }
Card TEN_SPADES = { suite: SPADES, ordinal: TEN }
Card JACK_SPADES = { suite: SPADES, ordinal: JACK }
Card QUEEN_SPADES = { suite: SPADES, ordinal: QUEEN }
Card KING_SPADES = { suite: SPADES, ordinal: KING }
Card ACE_SPADES = { suite: SPADES, ordinal: ACE }

// =======================================
// Hand
// =======================================

type Hand is { Card[] cards }
// Cards in a hand should be properly arranged!!
where sorted(cards)

function Hand(Card[] cards) -> Hand
requires sorted(cards):
    //
    return {cards: cards}

// =======================================
// TEST
// =======================================

public export method test():
    //
    assume Hand([TWO_HEARTS]) == {cards: [TWO_HEARTS]}
    assume Hand([TEN_HEARTS,THREE_CLUBS]) == {cards: [TEN_HEARTS,THREE_CLUBS]}
    assume Hand([TWO_HEARTS,FOUR_CLUBS,QUEEN_SPADES]) == {cards: [TWO_HEARTS,FOUR_CLUBS,QUEEN_SPADES]}

