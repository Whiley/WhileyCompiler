import println from whiley.lang.System

type nat is (int x) where x >= 0

function sum([nat] list) => nat:
    r = 0
    for l in list where r >= 0:
        assert r >= 0
        r = r + l
    return r

method main(System.Console sys) => void:
    rs = sum([0, 1, 2, 3])
    sys.out.println(Any.toString(rs))
