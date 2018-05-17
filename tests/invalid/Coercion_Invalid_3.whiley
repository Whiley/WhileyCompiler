type pos is (int x) where x > 0
type nat is (int x) where x >= 0
type neg is (int x) where x < 0

function f({nat f} r) -> {pos f}|{neg f}:
    return r
