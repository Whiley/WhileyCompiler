import * from whiley.lang.*

define imsg as int|{string msg}

string getMessage(imsg m):
    if m is {string msg}:
        return m.msg
    else:
        return str(m)

void ::main(System sys,[string] args):
    sys.out.println(getMessage({msg:"HELLO WORLD"}))
    sys.out.println(getMessage(1))
