import println from whiley.lang.System

define pos as int where $ > 0

pos extract([int] ls) requires |ls| > 1:
    for l in ls:
        x = l - 1
        if x < 0:
            return 1
    // at this point, we should be guaranteed that 
    // ls is a list of pos.
    return ls[0] + ls[1]

void ::main(System.Console sys):
    rs = extract([-1,-2,0,1,2,3])
    sys.out.println(Any.toString(rs))


    



