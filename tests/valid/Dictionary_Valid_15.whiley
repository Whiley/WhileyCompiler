

type edict is {int=>int} | {real=>real}

function f(int x) -> {real=>real}:
    if x < 0:
        return ({real=>real}) {1=>2, 2=>3}
    else:
        return {1.0=>1.5, 2.0=>2.5}

public export method test() -> void:
    {real=>real} d = ({real=>real}) f(-1)
    d[3.0] = 4.0
    assume d == {1.0=>2.0, 2.0=>3.0, 3.0=>4.0}
    d = f(2)
    d[3.0] = 4.0
    assume d == {1.0=>1.5, 2.0=>2.5, 3.0=>4.0}
