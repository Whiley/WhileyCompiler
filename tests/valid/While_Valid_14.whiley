import whiley.lang.*

function extract([int] ls) -> (int r)
ensures r >= |ls|:
    //
    int i = 0
    while i < |ls|:
        i = i + 1
    return i

method main(System.Console sys) -> void:
    int rs = extract([-2, -3, 1, 2, -23, 3, 2345, 4, 5])
    sys.out.println(rs)
