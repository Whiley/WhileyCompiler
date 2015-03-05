import whiley.lang.System
import SyntaxError from whiley.lang.Errors

type nat is (int x) where x >= 0

function f(nat pos, ASCII.string input) -> bool|null:
    if pos >= |input|:
        return null
    else:
        bool flag = input[pos] == 'O'
        return flag

method main(System.Console console):
    console.out.println(Any.toString(f(0, "Ox")))
    console.out.println(Any.toString(f(0, "1x")))
    console.out.println(Any.toString(f(1, "O")))
