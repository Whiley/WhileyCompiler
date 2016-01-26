

type List1 is {null | List1 next, int | null data}

type List2 is {null | List1 next, int data}

type List3 is {null | List1 next, null data}

type List4 is List3 | List2

function f(List1 r) -> List4:
    return r

public export method test() :
    List1 list = {next: null, data: 1}
    list = {next: list, data: null}
    List4 ans = f(list)
    assume ans == {next: {next: null, data:1}, data: null}
