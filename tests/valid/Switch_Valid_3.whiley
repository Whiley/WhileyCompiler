

function f(int[] input) -> int
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

public export method test() :
    assume f("N") == 1
    assume f("B") == 2
    assume f("R") == 3
    assume f("Q") == 4
    assume f("K") == 5
    assume f("e") == 6
    assume f("1") == 6
