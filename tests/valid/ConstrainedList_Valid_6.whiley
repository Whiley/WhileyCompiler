type nat is (int x) where x >= 0

final nat ONE_CENT = 0
final nat FIVE_CENTS = 1
final nat TEN_CENTS = 2
final nat TWENTY_CENTS = 3
final nat FIFTY_CENTS = 4
final nat ONE_DOLLAR = 5
final nat FIVE_DOLLARS = 6
final nat TEN_DOLLARS = 7
final int[] Value = [1, 5, 10, 20, 50, 100, 500, 1_000]

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
