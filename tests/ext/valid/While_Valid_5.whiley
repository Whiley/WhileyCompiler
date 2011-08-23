import whiley.lang.*:*

[int] extract([int] ls) ensures |$| > 0:
    i = 0
    r = [1]
    // now do the reverse!
    while i < |ls| where |r| > 0:
        r = r + [1]
        i = i + 1
    return r

void ::main(System sys,[string] args):
    rs = extract([1,2,3,4,5,6,7])
    sys.out.println(str(rs))
    rs = extract([])
    sys.out.println(str(rs))
