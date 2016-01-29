

type nat is (int x) where x >= 0

function f(nat[][] xs) -> nat[]
requires |xs| > 0:
    return xs[0]

public export method test() :
    nat[] rs = f([[1, 2, 3], [4, 5, 6]])
    assume rs == [1,2,3]
