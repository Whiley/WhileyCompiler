import println from whiley.lang.System

define nat as int where $ >= 0

nat sum([nat] list):
    r = 0
    for l in list where r >= 0:
        assume r >= 0
        r = r + l
    return r

void ::main(System.Console sys):
    rs = sum([0,1,2,3])
    sys.out.println(Any.toString(rs))









