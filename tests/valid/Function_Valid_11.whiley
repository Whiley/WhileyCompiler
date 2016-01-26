

type fr8nat is (int x) where x > 0

type fr8neg is (int x) where x < 0

function f(fr8nat y) -> bool:
    return true

function f(fr8neg x) -> bool:
    return false

public export method test() :
    fr8nat x = 1
    assume f(x) == true
    fr8neg y = -1
    assume f(y) == false
