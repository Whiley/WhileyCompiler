import * from whiley.lang.*

string f(int x):
    if x < 0:
        return "NEGATIVE"
    else if x == 0:
        return "ZERO"
    else:
        return "POSITIVE"

void ::main(System.Console sys):
    x = 1
    for s in sys.args:
        if s == "":
            x = 1.2
            break
        x = x + 1    
    sys.out.println(f(x))
