import whiley.lang.System

type edict is {int=>int} | {real=>real}

function f(int x) => {real=>real}:
    if x < 0:
        return ({real=>real}) {1=>2, 2=>3}
    else:
        return {1.0=>1.5, 2.0=>2.5}

public method main(System.Console sys) => void:
    {real=>real} d = ({real=>real}) f(-1)
    d[3.0] = 4.0
    sys.out.println("Dictionary=" ++ d)
    d = f(2)
    d[3.0] = 4.0
    sys.out.println("Dictionary=" ++ d)
