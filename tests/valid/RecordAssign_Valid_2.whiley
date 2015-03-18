import * from whiley.lang.*

type rec1 is {int x}

type rec2 is {rec1 current}

function f(rec2 r) -> rec2:
    r.current.x = 1
    return r

method main(System.Console console) -> void:
    rec2 r = {current: {x: 0}}
    console.out.println_s("BEFORE: " ++ Any.toString(r))
    console.out.println_s("AFTER: " ++ Any.toString(f(r)))
