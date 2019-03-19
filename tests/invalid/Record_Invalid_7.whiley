public type Vector is {
    int[] items,
    int length
} where length <= |items|

function f(Vector[] vec) -> int:
    // made a typo here :)
    vec.length = 1
    //
    return 0