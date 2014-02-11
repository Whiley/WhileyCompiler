import whiley.lang.System

type edict is {int=>int} | {real=>real}

function f(int x) => edict:
    if x < 0:
        return {1=>2, 2=>3}
    else:
        return {1=>1.5, 2=>2.5}

function g(int x) => {int|real => int|real}:
    return f(x)

public method main(System.Console sys) => void:
    edict d = g(-1)
    sys.out.println("Dictionary=" ++ d)
    d = g(2)
    sys.out.println("Dictionary=" ++ d)
