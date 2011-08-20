import whiley.lang.*:*

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

void System::main([string] args):
    this.out.println(f("N"))
    this.out.println(f("K"))
    this.out.println(f("Q"))
    this.out.println(f("B"))
    this.out.println(f("R"))
    this.out.println(f("Q"))
    this.out.println(f("e"))
    this.out.println(f("1"))