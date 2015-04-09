import whiley.lang.*

constant SIZE is 5

method main(System.Console sys) -> void:
    [{int}] components = []
    while |components| < SIZE:
        components = components ++ [{}]
    assume components == [{},{},{},{},{}]
