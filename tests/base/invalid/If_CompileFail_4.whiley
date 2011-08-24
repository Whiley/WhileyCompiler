import whiley.lang.*:*

int f(int x):
    if(x < 10):
        return 1
    else if(x > 10):
        return 2

void ::main(System sys,[string] args):
    sys.out.println(str(f(1)))
    sys.out.println(str(f(10)))
    sys.out.println(str(f(11)))
    sys.out.println(str(f(1212)))
    sys.out.println(str(f()-1212))
