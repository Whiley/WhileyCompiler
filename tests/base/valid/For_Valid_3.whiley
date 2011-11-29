import * from whiley.lang.*

int sum({nat} xs):
    r = 0
    for x in xs:
        r = r + x
    return r

void ::main(System sys,[string] args):
    z = sum({1,2,3,4,5})
    sys.out.println(toString(z))
    
