define state as (string input, int pos)
define expr as (int op, expr lhs) | (string err)

(expr e, state st) parseIdentifier(state st):
    return (e:(err:"Got here"),st:st)
    
void System::main([string] args):
    (expr e, state st) r = parseIdentifier((input:"hello",pos:0))
    print str(r.e)
