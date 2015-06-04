

function count(int width, int height) -> int
requires width >= 0 && height >= 0:
    //
    int i = 0
    int size = width * height
    //
    while i < size where i <= size:
        i = i + 1
    //
    return i

public export method test():
    assume count(0,0) == 0
    assume count(1,1) == 1
    assume count(5,5) == 25

