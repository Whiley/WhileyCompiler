import whiley.lang.System

type nat is int

function sum([nat] ls) => nat:
    int i = 0
    int sum = 0
    while i < |ls|:
        sum = sum + ls[i]
        i = i + 1
    return sum

method main(System.Console sys) => void:
    sys.out.println(Any.toString(sum([])))
    sys.out.println(Any.toString(sum([1, 2, 3])))
    sys.out.println(Any.toString(sum([12387, 98123, 12398, 12309, 0])))
