import whiley.lang.*

/**
 * Perform a merge sort of integer items.
 */
function loop2(int p, int q) -> (int r)
    requires p > 0 && q > 0
    ensures r == q*p:
    int qq = 0
    int i = 0

    while i< p
       where i <= p
       where qq == q*i:
       i = i+1
       qq = qq + q
    //assert i == p // uncomment this and it verifies!
    assert qq == q*p
    return qq

method main(System.Console sys):
    sys.out.println_s("p = " ++ Any.toString(loop2(5,10)))
