

type nat is (int x) where x >= 0

constant ONE_CENT is 0

constant FIVE_CENTS is 1

constant TEN_CENTS is 2

constant TWENTY_CENTS is 3

constant FIFTY_CENTS is 4

constant ONE_DOLLAR is 5

constant FIVE_DOLLARS is 6

constant TEN_DOLLARS is 7

constant Value is [1, 5, 10, 20, 50, 100, 500, 1000]

type Cash is (nat[] coins) where |coins| == |Value|

function Cash(nat[] coins) -> Cash
requires all { i in 0..|coins| | coins[i] < |Value| }:
    Cash cash = [0, 0, 0, 0, 0, 0, 0, 0]
    int i = 0
    while i < |coins|
        where i >= 0 && |cash| == |Value|
        where all { j in 0..|cash| | cash[j] >= 0 }:
        //
        int c = coins[i]
        cash[c] = cash[c] + 1
        i = i + 1
    return cash

public export method test() :
    assume Cash([ONE_DOLLAR, FIVE_CENTS]) == [0, 1, 0, 0, 0, 1, 0, 0]
    assume Cash([FIVE_DOLLARS, TEN_CENTS, FIFTY_CENTS]) == [0, 0, 1, 0, 1, 0, 1, 0]
    assume Cash([ONE_DOLLAR, ONE_DOLLAR, TWENTY_CENTS]) == [0, 0, 0, 1, 0, 2, 0, 0]
