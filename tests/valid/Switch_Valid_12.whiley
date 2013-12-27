import println from whiley.lang.System

int f(int x) ensures $ >= 0 && $ <= 2:
    switch(x):
        case 1:
           return 0
        case 2:
           return 1
    return 2

void ::main(System.Console sys):
    sys.out.println(Any.toString(f(2)))
    sys.out.println(Any.toString(f(1)))
    sys.out.println(Any.toString(f(0)))



