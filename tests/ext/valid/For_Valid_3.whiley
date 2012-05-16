import println from whiley.lang.System

int sum({nat} xs) ensures $ >= 0:
    r = 0
    for x in xs where r >= 0:
        r = r + x
    return r

void ::main(System.Console sys):
    z = sum({1,2,3,4,5})
    sys.out.println(Any.toString(z))
    
