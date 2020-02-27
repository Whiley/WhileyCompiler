type nat is (int x) where x >= 0

nat ONE_CENT = 0
nat FIVE_CENTS = 1
nat TEN_CENTS = 2
nat TWENTY_CENTS = 3
nat FIFTY_CENTS = 4
nat ONE_DOLLAR = 5
nat FIVE_DOLLARS = 6
nat TEN_DOLLARS = 7
int[] Value = [1, 5, 10, 20, 50, 100, 500, 1_000]

type Cash is (nat[] coins) where |coins| == |Value|

function Cash(nat[] coins) -> Cash
requires all { i in 0..|coins| | coins[i] < |Value| }:
    int[] cash = [0, 0, 0, 0, 0, 0, 0, 0]
    nat i = 0
    while i < |coins|
        where |cash| == |Value|
        where all { k in 0..|cash| | cash[k] >= 0 }:
        nat coin = coins[i]
        cash[coin] = cash[coin] + 1
        i = i + 1
    return (Cash) cash

public export method test() :
    Cash cash = Cash([ONE_DOLLAR, FIVE_CENTS])
    assume cash == [0, 1, 0, 0, 0, 1, 0, 0]
    cash = Cash([FIVE_DOLLARS, TEN_CENTS, FIFTY_CENTS])
    assume cash == [0, 0, 1, 0, 1, 0, 1, 0]
    cash = Cash([ONE_DOLLAR, ONE_DOLLAR, TWENTY_CENTS])
    assume cash == [0, 0, 0, 1, 0, 2, 0, 0]
