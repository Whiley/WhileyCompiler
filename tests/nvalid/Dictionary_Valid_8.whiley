import println from whiley.lang.System

type DL1 is {int=>int}

type DL2 is {real=>int}

function update(DL1 ls) => DL2:
    ls[1.2] = 1
    return ls

method main(System.Console sys) => void:
    x = {0=>1, 1=>2}
    x = update(x)
    sys.out.println(Any.toString(x))
