

type edict is {int=>int} | {real=>real}
type odict is {int|real=>int|real}

function f(int x) -> edict:
    if x < 0:
        return {1=>2, 2=>3}
    else:
        return {1.0=>1.5, 2.0=>2.5}

function g(int x) -> odict:
    return (odict) f(x)

public export method test() -> void:
    odict d = g(-1)
    assume d == {1=>2, 2=>3}
    d = g(2)
    assume d == {1.0=>1.5, 2.0=>2.5}
