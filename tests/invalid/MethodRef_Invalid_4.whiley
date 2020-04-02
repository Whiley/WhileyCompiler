type nat is (int x) where x >= 0
type Transformer is function(int)->(nat)

method g(int y) -> (int z):
    return y

method f() -> Transformer:
    return &(int x -> g(x))
