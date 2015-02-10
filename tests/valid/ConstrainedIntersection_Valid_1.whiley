type nat is (int x) where x >= 0
type l10 is (int x) where x < 10

function f(int v) -> (int r)
ensures r >= 0:
    //
    if v is nat&l10:
        return 1
    //
    return 0

method main(System.Console console):
    console.out.println(f(1))
    console.out.println(f(9))
    console.out.println(f(10))
    console.out.println(f(-1))
