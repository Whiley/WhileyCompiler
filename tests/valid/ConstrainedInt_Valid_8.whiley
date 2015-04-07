import whiley.lang.*

constant c_num is {1,2,3,4}

type num is (int x) where x in c_num

type bignum is (int x) where x in {1, 2, 3, 4, 5, 6, 7}

function f(num x) -> int:
    int y = x
    return y

function g({bignum} zs, int z) -> int:
    if (z in zs) && (z in c_num):
        return f(z)
    else:
        return -1

method main(System.Console sys) -> void:
    sys.out.println(g({1, 2, 3, 5}, 3))
