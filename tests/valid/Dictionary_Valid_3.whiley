import whiley.lang.*

function f(int x) -> {int=>int}:
    return {1=>x, 3=>2}

function get(int i, {int=>int} map) -> int:
    return map[i]

method main(System.Console sys) -> void:
    {int=>int} m1 = f(1)
    {int=>int} m2 = f(2)
    {int=>int} m3 = f(3)
    m1[2] = 4
    m2[1] = 23498
    sys.out.println(get(1, m1))
    sys.out.println(get(2, m1))
    sys.out.println(get(1, m2))
    sys.out.println(get(1, m3))
    sys.out.println(get(3, m3))
