type nat is (int x) where x >= 0
type Transformer is function(int)->(int)

method g(int y) -> (int z):
    return y

method f() -> Transformer:
    return &(nat x -> g(x))
