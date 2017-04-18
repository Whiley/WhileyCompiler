property nat(int x) where x >= 0

type nat is (int x) where nat(x)

function id(nat x) -> (nat y):
    return x

public export method test():
    assume id(0) == 0
    assume id(1) == 1
    assume id(2) == 2
