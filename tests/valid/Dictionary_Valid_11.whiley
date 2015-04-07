import whiley.lang.*

type edict is {int=>int} | {real=>real}

function f(int x) -> edict:
    if x < 0:
        return {1=>2, 2=>3}
    else:
        return {1.0=>1.5, 2.0=>2.5}

public method main(System.Console sys) -> void:
    edict d = f(-1)
    sys.out.println_s("Dictionary=" ++ Any.toString(d))
    d = f(2)
    sys.out.println_s("Dictionary=" ++ Any.toString(d))
