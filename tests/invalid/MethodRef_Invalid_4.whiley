type Transformer is function(int)->(int)

method g(int y) -> (int z):
    return y

method f() -> Transformer:
    return &(int x -> g(x))
