import println from whiley.lang.System
import nat from whiley.lang.Int

int sum({nat} xs):
    r = 0
    for x in xs:
        r = r + x
    return r

void ::main(System.Console sys):
    z = sum({1,2,3,4,5})
    sys.out.println(Any.toString(z))
    
