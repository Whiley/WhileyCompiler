import * from whiley.lang.*

string iof(int i):
    return "" + ('a' + i) + ('1' + i)

void ::main(System sys,[string] args):
    sys.out.println(str(iof(0)))
    sys.out.println(str(iof(1)))
    sys.out.println(str(iof(2)))
    sys.out.println(str(iof(3)))
    sys.out.println(str(iof(4)))
