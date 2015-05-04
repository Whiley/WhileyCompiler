

function indexOf([int] items, int ch) -> (int r)
ensures r == |items| || items[r] == ch:
    //
    int i = 0
    //
    while i < |items| && items[i] != ch
    where 0 <= i && i <= |items|:
        i = i + 1
    //
    return i

public export method test():
    assume indexOf("hello world",'h') == 0
    assume indexOf("hello world",'e') == 1
    assume indexOf("hello world",'l') == 2
    assume indexOf("hello world",'o') == 4
    assume indexOf("hello world",' ') == 5
    assume indexOf("hello world",'w') == 6
    assume indexOf("hello world",'r') == 8
    assume indexOf("hello world",'d') == 10
    assume indexOf("hello world",'z') == 11


