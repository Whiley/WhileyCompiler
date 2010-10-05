define sr8nat as int where $ > 0
define sr8tup as {sr8nat f, int g} where g > f 

void System::main([string] args):
    x = [{f:1,g:3},{f:4,g:8}]
    x[0].f = 2
    out->println(str(x))
    
