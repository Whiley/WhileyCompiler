import * from whiley.lang.*
import * from whiley.lang.*
import * from whiley.lang.*

Type.nat f(int x):
    if x < 0:
        return 0
    else:
        return x

public void ::main(System sys,[string] args):
    sys.out.println(String.str(f(1)))
