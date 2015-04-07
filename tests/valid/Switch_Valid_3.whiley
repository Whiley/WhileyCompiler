import whiley.lang.*

function f([int] input) -> int
requires |input| > 0:
    //
    int c = input[0]
    int r
    switch c:
        case 'N':
            r = 1
        case 'B':
            r = 2
        case 'R':
            r = 3
        case 'Q':
            r = 4
        case 'K':
            r = 5
        default:
            r = 6
    //
    return r

method main(System.Console sys) -> void:
    sys.out.println(f("N"))
    sys.out.println(f("B"))
    sys.out.println(f("R"))    
    sys.out.println(f("Q"))
    sys.out.println(f("K"))
    sys.out.println(f("e"))
    sys.out.println(f("1"))
