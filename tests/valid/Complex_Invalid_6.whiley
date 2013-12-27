import println from whiley.lang.System

define nat as int where $ >= 0

// Return the maximum of two integers.
int max(int a, int b)
// The result is either "a" or "b"
ensures $ == a || $ == b,
// The result is greater-than-or-equal to "a" and "b"
ensures a <= $ && b <= $:
    //
    if a < b:
        return b
    else:
        return a

// Return the difference between two integers, which is always a
// natural number.
nat diff(int a, int b)
// The result is the positive difference between "a" and "b"
ensures $ == max(a - b, b - a):
    //
    if(a > b):
        diff = a - b
    else:
        diff = b - a
    //
    return diff

void ::main(System.Console console):
    list = -10 .. 10
    for i in list:
        for j in list:
            console.out.println("DIFF(" + i + "," + j + ") = " + diff(i,j))
    //
