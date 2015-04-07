import whiley.lang.*

type nat is (int x) where x >= 0

function f([[nat]] xs) -> [nat]
requires |xs| > 0:
    return xs[0]

method main(System.Console sys) -> void:
    [nat] rs = f([[1, 2, 3], [4, 5, 6]])
    sys.out.println(rs)
