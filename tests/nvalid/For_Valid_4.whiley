import println from whiley.lang.System

type pos is int where $ > 0

function extract([int] ls) => pos
requires |ls| > 1:
    for l in ls:
        x = l - 1
        if x < 0:
            return 1
    return ls[0] + ls[1]

method main(System.Console sys) => void:
    rs = extract([-1, -2, 0, 1, 2, 3])
    sys.out.println(Any.toString(rs))
