constant odd is [1,3,5]

type odd is (int x) where x == 1 || x == 3 || x == 5
type even is (int x) where x == 2 || x == 4 || x == 6

type oddeven is odd|even

function f(oddeven x) -> even:
    if x is odd:
        return 2
    return x

public export method test() :
    int y = 1
    y = f(1)
    assume y == 2
