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
    out.println(f("N"))
    out.println(f("K"))
    out.println(f("Q"))
    out.println(f("B"))
    out.println(f("R"))
    out.println(f("Q"))
    out.println(f("e"))
    out.println(f("1"))