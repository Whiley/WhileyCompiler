type Point is {int x, int y}

type Ref<T> is { T data }

function f(Ref<Point> rec) -> (int r):
    return rec.data
