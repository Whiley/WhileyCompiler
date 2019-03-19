type Point is {int x, int y}

type Ref<T> is { T data }

function f(Ref<Point> rec) -> (bool r):
    return rec.data.x + rec.data.y
