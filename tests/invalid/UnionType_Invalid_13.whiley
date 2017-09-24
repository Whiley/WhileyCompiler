type arr_t is (int[]|bool[] t)

function f(arr_t xs) -> arr_t:
    //
    xs[0] = 1
    return xs
