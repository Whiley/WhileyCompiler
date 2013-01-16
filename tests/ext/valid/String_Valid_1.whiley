import println from whiley.lang.System
import SyntaxError from whiley.lang.Errors

define nat as int where $ >= 0

bool f(nat pos, string input) throws SyntaxError:
    if pos >= |input|:
        throw SyntaxError("Missing flag",pos,pos) 
    else:
        flag = (input[pos] == 'O')
        return flag

void ::main(System.Console console):
    try:
        console.out.println(f(0,"Ox"))
        console.out.println(f(0,"1x"))
        console.out.println(f(1,"O"))
    catch(SyntaxError e):
        console.out.println("SYNTAX ERROR")
    
            










    
    
    