function indexOf(int c1, int[] str) -> int | null:
    int i = 0
    while i < |str| where i >= 0:
        if c1 == str[i]:
            return i
        i = i + 1
    return null

public export method test() :
    assume indexOf('H', "Hello World") == 0
    assume indexOf('e', "Hello World") == 1
    assume indexOf('l', "Hello World") == 2
    assume indexOf('o', "Hello World") == 4
    assume indexOf(' ', "Hello World") == 5
    assume indexOf('W', "Hello World") == 6
    assume indexOf('r', "Hello World") == 8
    assume indexOf('d', "Hello World") == 10
    assume indexOf('z', "Hello World") == null
    assume indexOf('1', "Hello World") == null
