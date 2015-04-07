import * from whiley.lang.*

method main() -> int:
    int i = 0
    int r
    //
    while i < 10:
        r = r + i
    //
    return r
