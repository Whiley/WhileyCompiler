import println from whiley.lang.System

type nat is int where $ >= 0

function max(int a, int b) => int
ensures ($ == a) || ($ == b)
ensures (a <= $) && (b <= $):
    if a < b:
        return b
    else:
        return a

function diff(int a, int b) => nat
ensures $ == max(a - b, b - a):
    if a > b:
        diff = a - b
    else:
        diff = b - a
    return diff

method main(System.Console console) => void:
    list = -10 .. 10
    for i in list:
        for j in list:
            console.out.println((((("DIFF(" + i) + ",") + j) + ") = ") + diff(i, j))
