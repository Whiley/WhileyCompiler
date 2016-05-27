type intList is int | int[]

type tup is {intList data, int mode}

function f({int data, int mode}[] x) -> {int data, int mode}[]:
    return x

method g() -> {int data, int mode}[]:
    tup[] tups = [{data: 1, mode: 0}, {data: [1, 2, 3], mode: 1}]
    tups[0].data = 1
    return f(tups)
