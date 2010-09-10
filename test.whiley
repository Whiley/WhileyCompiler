define tup as (int x, rec link) where x > 0
define rec as int|tup

void System::main([string] args):
    rec r = (x:1,link:(x:-1,link:2))
    print str(r)    
