import println from whiley.lang.System

type nat is int where $ >= 0

function create(nat size, nat value) => [nat]:
    r = []
    i = 0
    while i < size where r is [nat]:
        r = r + [value]
        i = i + 1
    return r

method main(System.Console sys) => void:
    sys.out.println(create(10, 10))
    sys.out.println(create(5, 0))
    sys.out.println(create(0, 0))
