import println from whiley.lang.System

int f({int=>any} xs):
    if xs is {int=>string}:
        return 1
    else:
        return -1

void ::main(System.Console sys):
    s1 = {0=>"Hello"}
    s2 = {1=>"Hello"}
    s3 = {0=>"Hello",1=>"Hello"}
    s4 = {0=>"Hello",1=>"Hello",3=>"Hello"}
    sys.out.println(Any.toString(f(s1)))
    sys.out.println(Any.toString(f(s2)))
    sys.out.println(Any.toString(f(s3)))
    sys.out.println(Any.toString(f(s4)))
    t1 = {0=>0,1=>1}
    sys.out.println(Any.toString(f(t1)))

