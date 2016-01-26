

type IntReal is int | real

function f(IntReal y) -> IntReal:
    return y

public export method test() :
    IntReal x = 123
    assume f(x) == 123
    x = 1.234
    assume f(x) == 1.234
