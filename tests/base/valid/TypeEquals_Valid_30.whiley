int f({int->*} xs):
    if xs is {int->string}:
        return 1
    else:
        return -1

void System::main([string] args):
    s1 = {0->"Hello"}
    s2 = {1->"Hello"}
    s3 = {0->"Hello",1->"Hello"}
    s4 = {0->"Hello",1->"Hello",3->"Hello"}
    out.println(str(f(s1)))
    out.println(str(f(s2)))
    out.println(str(f(s3)))
    out.println(str(f(s4)))
    t1 = {0->0,1->1}
    out.println(str(f(t1)))

