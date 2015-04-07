import whiley.lang.*

constant GAME is [(0, 0), (1, 1), (0, 1), (2, 2), (0, 2), (2, 2)]

public method main(System.Console console) -> void:
    for tup in GAME:
        console.out.println_s("TUPLE: " ++ Any.toString(tup))
