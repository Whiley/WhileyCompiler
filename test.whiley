// A simple, recursive expression tree
define expr as (int num) | (string err)

expr parseIdentifier():
    return (err:"Hello")

void System::main([string] args):
    expr e = parseIdentifier()


