import whiley.lang.System

type nint is null | int

function move(int from, int to, [[nint]] list) => [[nint]]:
    tmp = list[from][from + 1]
    list[from][from + 1] = null
    list[to][to + 1] = tmp
    return list

method main(System.Console sys) => void:
    ls = [[1, 2, 3], [4, 5, 6], [7, 8, 9]]
    ls = move(0, 1, ls)
    sys.out.println(Any.toString(ls))
