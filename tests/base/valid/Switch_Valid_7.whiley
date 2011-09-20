import * from whiley.lang.*

define R1 as { int x }
define R2 as { int x, int y }

string f(string input):
    c = input[0]       
    switch c:
        case 'N':
            r = "GOT KNIGHT"
            break
        case 'B':
            r = "GOT BISHOP"
            break
        case 'R':
            r = "GOT ROOK"
            break
        case 'Q':
            r = "GOT QUEEN"
            break
        case 'K':
            r = "GOT KING"
            break
        default:
            r = "GOT NOTHING"
    return r

void ::main(System sys,[string] args):
    sys.out.println(f("N"))
    sys.out.println(f("K"))
    sys.out.println(f("Q"))
    sys.out.println(f("B"))
    sys.out.println(f("R"))
    sys.out.println(f("Q"))
    sys.out.println(f("e"))
    sys.out.println(f("1"))