type num is (int x) where 1 <= x && x <= 4
type bignum is (int x) where 1 <= x && x <= 7

function f(num x) -> int:
    int y = x
    return y

function contains(int[] items, int item) -> (bool r)
ensures r ==> some { i in 0 .. |items| | items[i] == item }:
    int i = 0
    //
    while i < |items| where i >= 0:
        if items[i] == item:
            return true
        i = i + 1
    //
    return false

function g(bignum[] zs, int z) -> int:
    if contains(zs,z) && contains([1,2,3,4],z):
        return f(z)
    else:
        return -1

public export method test() :
    assume g([1, 2, 3, 5], 3) == 3
