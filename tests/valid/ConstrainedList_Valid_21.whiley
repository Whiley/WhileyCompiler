import whiley.lang.*

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

type Cash is ([nat] coins) where |coins| == |Value|

function Cash([nat] coins) -> Cash
requires no { c in coins | c >= |Value| }:
    [int] cash = [0, 0, 0, 0, 0, 0, 0, 0]
    for i in coins where (|cash| == |Value|) && no { c in cash | c < 0 }:
        cash[i] = cash[i] + 1
    return cash

method main(System.Console sys) -> void:
    Cash cash = Cash([ONE_DOLLAR, FIVE_CENTS])
    sys.out.println(cash)
    cash = Cash([FIVE_DOLLARS, TEN_CENTS, FIFTY_CENTS])
    sys.out.println(cash)
    cash = Cash([ONE_DOLLAR, ONE_DOLLAR, TWENTY_CENTS])
    sys.out.println(cash)
