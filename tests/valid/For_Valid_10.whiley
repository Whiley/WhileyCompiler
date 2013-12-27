import println from whiley.lang.System

define nat as int where $ >= 0

nat extract([int] ls) requires |ls| > 0:
    for l in ls:
        if l < 0:
            return 0
    // at this point, we should be guaranteed that 
    // ls is a list of nats.
    return ls[0]

void ::main(System.Console sys):
    rs = extract([-2,-3,1,2,-23,3,2345,4,5])
    sys.out.println(Any.toString(rs))


    



