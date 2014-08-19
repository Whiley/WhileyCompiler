import whiley.lang.System

type R1 is {int x}

type R2 is {int y, int x}

function f(string input) => string
requires |input| > 0:
    char c = input[0]
    string r
    switch c:
        case 'N':
            r = "GOT KNIGHT"
        case 'B':
            r = "GOT BISHOP"
        case 'R':
            r = "GOT ROOK"
        case 'Q':
            r = "GOT QUEEN"
        case 'K':
            r = "GOT KING"
        default:
            r = "GOT NOTHING"
    return r

method main(System.Console sys) => void:
    sys.out.println(f("N"))
    sys.out.println(f("K"))
    sys.out.println(f("Q"))
    sys.out.println(f("B"))
    sys.out.println(f("R"))
    sys.out.println(f("Q"))
    sys.out.println(f("e"))
    sys.out.println(f("1"))
