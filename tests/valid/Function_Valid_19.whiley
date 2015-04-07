import whiley.lang.*

function f({int} xs, int lb, int ub) -> {int}
requires no { x in xs | (x < lb) || (x >= ub) } && (|xs| == (ub - lb)):
    assert (|xs| == 0) || (lb in xs)
    assert !(ub in xs)
    return xs

method main(System.Console sys) -> void:
    sys.out.println(f({10, 11, 12, 13, 14}, 10, 15))
    sys.out.println(f({}, 10, 10))
