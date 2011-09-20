import * from whiley.lang.*

[int] reverse([int] ls):
    i = |ls|
    r = []
    // now do the reverse!
    while i > 0:
        i = i - 1
        r = r + [ls[i]]
    return r

void ::main(System sys,[string] args):
    rs = reverse([1,2,3,4,5])
    sys.out.println(str(rs))
