int f({(int,string)} xs):
    if xs ~= {int->string}:
        return 1
    else:
        return -1

void System::main([string] args):
    s1 = {(1,"Hello")}
    s2 = {(1,"Hello"),(1,"World")}
    s3 = {(1,"Hello"),(2,"Hello")}
    s4 = {1->"Hello",2->"Hello"}
    out.println(str(f(s1)))
    out.println(str(f(s2)))
    out.println(str(f(s3)))
    out.println(str(f(s4)))

