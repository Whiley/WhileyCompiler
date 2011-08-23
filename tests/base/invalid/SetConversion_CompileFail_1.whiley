import whiley.lang.*:*

void f({real} ls):
    sys.out.println(str(ls))

void ::main(System sys,[string] args):
    f({1,2,3,{}})
