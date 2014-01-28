import whiley.lang.System

type DL1 is {int=>int}

type DL2 is {int=>real}

function update(DL1 ls) => DL2:
    ls[0] = 1.234
    return ls

method main(System.Console sys) => void:
    x = {0=>1, 1=>2}
    x = update(x)
    sys.out.println(Any.toString(x))
