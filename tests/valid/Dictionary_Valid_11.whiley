

type edict is {int=>int} | {real=>real}

function f(int x) -> edict:
    if x < 0:
        return {1=>2, 2=>3}
    else:
        return {1.0=>1.5, 2.0=>2.5}

public export method test() -> void:
    edict d = f(-1)
    assume d == {1=>2, 2=>3}
    d = f(2)
    assume d == {1.0=>1.5, 2.0=>2.5}

