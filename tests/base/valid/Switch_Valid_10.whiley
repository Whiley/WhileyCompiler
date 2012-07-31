import println from whiley.lang.System

string f(any x):
    switch x:
        case int:
            return "INTEGER"
        case [int]:
            return "LIST OF INTEGER"
        case {int}:
            return "SET OF INTEGER"
        default:
            return "OTHER"

public void ::main(System.Console console):
    console.println(f(12345))
    console.println(f([1,2,3]))
    console.println(f({1,2,3}))
