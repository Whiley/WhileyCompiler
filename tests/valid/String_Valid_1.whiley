import whiley.lang.*

type nat is (int x) where x >= 0

function f(nat pos, [int] input) -> bool|null:
    if pos >= |input|:
        return null
    else:
        bool flag = input[pos] == 'O'
        return flag

method main(System.Console console):
    console.out.println(f(0, "Ox"))
    console.out.println(f(0, "1x"))
    console.out.println(f(1, "O"))
