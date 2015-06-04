

constant odd is {1,3,5}

type odd is (int x) where x in odd

type even is (int x) where x in {2, 4, 6}

type oddeven is odd|even

function f(oddeven x) -> even:
    if x in odd:
        return 2
    return x

public export method test() -> void:
    int y = 1
    y = f(1)
    assume y == 2
