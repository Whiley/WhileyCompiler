// This is cut-down version of the full card benchmark.  This is to
// test the basic machinery without the associated performance problems.
type nat is (int x) where x >= 0

// =======================================
// Card Definition
// =======================================

// Suites
int HEARTS = 1
int CLUBS = 2

// Ordinals
int TWO = 2
int THREE = 3
int FOUR = 4

type Card is { int suite, int ordinal }
// Suite between hearts and clubs
where HEARTS <= suite && suite <= CLUBS
// Ordinal between 2 and ACE (high)
where 2 <= ordinal && ordinal <= FOUR

// =======================================
// Card Constants
// =======================================

Card TWO_HEARTS = { suite: HEARTS, ordinal: TWO }
Card THREE_HEARTS = { suite: HEARTS, ordinal: THREE }
Card FOUR_HEARTS = { suite: HEARTS, ordinal: FOUR }

Card TWO_CLUBS = { suite: CLUBS, ordinal: TWO }
Card THREE_CLUBS = { suite: CLUBS, ordinal: THREE }
Card FOUR_CLUBS = { suite: CLUBS, ordinal: FOUR }

// =======================================
// DECK
// =======================================

type Deck is (Card[] cards)
// Exactly 6 cards in a deck
where |cards| == 6

// A real test of verifier performance!!
Deck deck = [
        // Hearts
        TWO_HEARTS,
        THREE_HEARTS,
        FOUR_HEARTS,
        // Clubs
        TWO_CLUBS,
        THREE_CLUBS,
        FOUR_CLUBS
    ]

// =======================================
// RANDOM
// =======================================
type Random is {nat index, nat[] sequence}
// index is valid position in random sequence
where index < |sequence|
// Sequence is valid card index
where all { i in 0..|sequence| | sequence[i] < 6 }

function Random(nat[] sequence) -> Random
requires |sequence| > 0
requires all { i in 0..|sequence| | sequence[i] < 6 }:
    //
    return { index: 0, sequence: sequence }

function next(Random r) -> (nat index, Random nr)
ensures index < 6:
    // Get item from random sequence
    nat result = r.sequence[r.index]
    // Move to next item
    nat tmp = r.index + 1
    // Check for overflow
    if tmp == |r.sequence|:
        tmp = 0
    //
    r.index = tmp
    //
    return result, r

// =======================================
// SHUFFLE
// =======================================

function swap(Deck deck, nat from, nat to) -> Deck
requires from < 6 && to < 6:
    // Swap two cards around
    Card tmp = deck[from]
    deck[from] = deck[to]
    deck[to] = tmp
    //
    return deck

// "Randomly" shuffle the deck
function shuffle(Deck deck, Random rand, nat count) -> Deck:
    //
    nat i = 0
    //
    while i < count:
       nat from
       nat to  
       (from,rand) = next(rand)
       (to,rand) = next(rand)
       deck = swap(deck,from,to)
       i = i + 1
    //
    return deck
    
// =======================================
// TEST
// =======================================

public export method test():
    // Not a very long "random" sequence
    Random rand = Random([3,4,2,5,1,2])
    // Shuffle the deck ten times
    Deck d = shuffle(deck,rand,10)
    //
    assert |d| == 6

