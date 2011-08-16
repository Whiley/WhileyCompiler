string rep(char old, char new, string str):
    i = 0
    while i < |str|:
        if str[i] == old:
            str[i] = new
        i = i + 1
    return str    


void System::main([string] args):
    this.out.println(rep('e','w',"Hello"))
    this.out.println(rep('H','z',"Hello"))
    this.out.println(rep('o','1',"Hello"))
