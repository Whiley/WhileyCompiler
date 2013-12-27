import println from whiley.lang.System

define nat as int where $ >= 0

nat sum(int start, int end):
    r = 0
    for i in start .. end where r >= 0:
        r = r + 1
    return r


void ::main(System.Console sys):
    sys.out.println(Any.toString(sum(0,10)))
    sys.out.println(Any.toString(sum(10,13)))

    
