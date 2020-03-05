property nat(int x) where x >= 0
property pos(int x) where x > 0

type nat is (int x) where nat(x)
type pos is (int x) where pos(x)

function f1(pos x) -> (nat y):
    return (nat) x

function f2(nat x) -> (pos y):
    return (pos) x
