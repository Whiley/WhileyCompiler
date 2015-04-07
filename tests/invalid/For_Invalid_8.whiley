
type nat is (int x) where x >= 0

method main(int arg) -> int:
    [int] xs = [1, 2, 3]
    int r = arg - 1
    for x in xs where r >= 0:
        r = r + x
    return r
