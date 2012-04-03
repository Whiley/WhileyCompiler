import println from whiley.lang.System

string f(int x):
    if(x < 10):
        return "LESS THAN"
    else if(x > 10):
        return "GREATER THAN"
    else:
        return "EQUALS"

void ::main(System.Console sys):
    sys.out.println(f(1))
    sys.out.println(f(10))
    sys.out.println(f(11))
    sys.out.println(f(1212))
    sys.out.println(f(-1212))
