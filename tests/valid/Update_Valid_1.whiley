

type listdict is [int] | {int=>int}

function update(listdict l, int index, int value) -> listdict:
    l[index] = value
    return l

public export method test() -> void:
    l = [1, 2, 3]
    assume update(l, 1, 0) == [1,0,3]
    assume update(l, 2, 0) == [1,2,0]
    l = {1=>1, 2=>2, 3=>3}
    assume update(l, 1, 0) == {1=>0, 2=>2, 3=>3}
    assume update(l, 2, 0) == {1=>10, 2=>0, 3=>3}
