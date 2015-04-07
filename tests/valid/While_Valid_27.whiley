import whiley.lang.*

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

method main(System.Console console):
    console.out.println(count(0,0))
    console.out.println(count(1,1))
    console.out.println(count(5,5))

