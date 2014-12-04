function lastIndexOf([int] items, int item) => (int r)
requires |items| > 0
ensures r == -1 || items[r] == item:
    //
    int i = |items|
    do:
        i = i - 1
    while i >= 0 && items[i] != item 
        where i >= -1 && i < |items|
    //
    return i