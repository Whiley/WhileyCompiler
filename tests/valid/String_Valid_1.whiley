import whiley.lang.System
import SyntaxError from whiley.lang.Errors

type nat is (int x) where x >= 0

function f(nat pos, string input) => bool throws SyntaxError:
    if pos >= |input|:
        throw SyntaxError("Missing flag", pos, pos)
    else:
        bool flag = input[pos] == 'O'
        return flag

method main(System.Console console) => void:
    try:
        console.out.println(f(0, "Ox"))
        console.out.println(f(0, "1x"))
        console.out.println(f(1, "O"))
    catch(SyntaxError e):
        console.out.println("SYNTAX ERROR")
