import * from whiley.lang.*

string f([bool] x) requires |x| > 0 && x[0]:
    return toString(x)

void ::main(System sys,[string] args):
    sys.out.println(f([true]))
    sys.out.println(f([true,false]))
    sys.out.println(f([true,false,true]))
    
