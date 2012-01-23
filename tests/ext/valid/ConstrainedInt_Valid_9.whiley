import println from whiley.lang.System

define pos as int where $ > 0
define neg as int where $ < 0
define posneg as pos|neg

bool isPosNeg(any v):
    if v is posneg:
        return true
    else: 
        return false

void ::main(System.Console sys):
    sys.out.println(isPosNeg(1))
    sys.out.println(isPosNeg(0))
    sys.out.println(isPosNeg(-1))
