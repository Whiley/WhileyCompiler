import * from whiley.lang.*

void ::main(System sys,[string] args):    
    if |args| == 1:
        x = 1
    else:
        x = [1,2,3]
    sys.out.println(Any.toString(x))

