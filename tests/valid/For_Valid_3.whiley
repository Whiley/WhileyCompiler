import whiley.lang.*

function f({int=>int} dict) -> (int, int):
    int k = 0
    int v = 0
    for x, y in dict:
        k = k + x
        v = v + y
    return (k, v)

method main(System.Console sys) -> void:
    int k
    int v
    {int=>int} dict = {1=>2, 3=>4, 4=>5}
    (k, v) = f(dict)
    sys.out.println(k)
    sys.out.println(v)
