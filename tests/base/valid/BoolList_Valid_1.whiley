import whiley.lang.*:*

void ::main(System sys,[string] args):
    ls = [true,false,true]
    sys.out.println(str(ls))
    x = ls[0]
    sys.out.println(str(x))
    ls[0] = false
    sys.out.println(str(ls))
