define sr9nat as int
define sr9tup as {sr9nat f, int g}
define sr9arr as [{sr9nat f, int g}]

void System::main([string] args):
    x = [{f:1,g:2},{f:1,g:8}]
    x[0].f = 2 
    out.println(str(x))
