define tup as (int x, rec link) where x > 0
define rec as int|tup

void f(tup l):
    rec r = (x:1,link:l)
    print str(r)

void System::main([string] args):
    rec w = (x:1,link:2)
    f(w)
