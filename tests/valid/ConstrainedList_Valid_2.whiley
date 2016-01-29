type nat is (int x) where x >= 0

function abs(int[] items) -> nat[]
requires |items| > 0:
    return abs(items, 0)

function abs(int[] items, nat index) -> nat[]
requires (index <= |items|) && all { i in 0 .. index | items[i] >= 0 }:
    if index == |items|:
        return items
    else:
        items[index] = abs(items[index])
        return abs(items, index + 1)

function abs(int x) -> nat:
    if x >= 0:
        return x
    else:
        return -x

public export method test() :
    nat[] xs = [1, -3, -5, 7, -9, 11]
    xs = abs(xs)
    assume xs == [1,3,5,7,9,11]
