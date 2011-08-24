import whiley.lang.*:*

(int,int) f(int x):
    return (x,x+2)

void ::main(System sys,[string] args):
    x,y = f(1)
    sys.out.println(str(x))
    sys.out.println(str(y))
