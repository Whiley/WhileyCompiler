type IntReal is int | real

function f(int y) -> int:
    return y

method main():
    IntReal x
    //
    x = 123
    f(x)
    x = 1.234
    f(x)
