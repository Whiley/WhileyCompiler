type Transformer is function(int)->(int)

final int CONSTANT = 1

function g(int y) -> (int z):
    return CONSTANT+y

method f() -> Transformer:
    return &(int x -> g(x))

public export method test():
    Transformer t = f()
    assume t(1) == 2