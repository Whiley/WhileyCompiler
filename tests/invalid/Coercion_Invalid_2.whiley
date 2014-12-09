import * from whiley.lang.*

type List1 is {null | List1 next, int | null data}

type List2 is {null | List2 next, int data}

type List3 is {null | List3 next, null data}

type List4 is List3 | List2

function f(List1 r) -> List4:
    return r

method main(System.Console sys) -> void:
    list = {next: null, data: 1}
    list = {next: list, data: null}
    ans = f(list)
    sys.out.println(Any.toString(ans))
