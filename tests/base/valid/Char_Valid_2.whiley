import * from whiley.lang.*

char iof(string s, int i):
    return s[i]

void ::main(System sys,[string] args):
    sys.out.println(toString(iof("Hello",0)))
    sys.out.println(toString(iof("Hello",1)))
    sys.out.println(toString(iof("Hello",2)))
    sys.out.println(toString(iof("Hello",3)))
    sys.out.println(toString(iof("Hello",4)))
