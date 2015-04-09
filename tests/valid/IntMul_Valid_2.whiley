import whiley.lang.*

method main(System.Console console):
    int x = 8
    int y = 1
    int z = x * x - y
    assert z == 63
    z = (x * x) - y
    assert z == 63
    z = x * (x - y)
    assert z == 56
