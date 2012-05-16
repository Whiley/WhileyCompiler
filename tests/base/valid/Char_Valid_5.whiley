import println from whiley.lang.System

int iof(string s, int i):
    return s[i] - 'a'

void ::main(System.Console sys):
    sys.out.println(Any.toString(iof("Hello",0)))
    sys.out.println(Any.toString(iof("Hello",1)))
    sys.out.println(Any.toString(iof("Hello",2)))
    sys.out.println(Any.toString(iof("Hello",3)))
    sys.out.println(Any.toString(iof("Hello",4)))
