import whiley.lang.*:*

int iof(string s, int i):
    return s[i] - 'a'

void System::main([string] args):
    this.out.println(str(iof("Hello",0)))
    this.out.println(str(iof("Hello",1)))
    this.out.println(str(iof("Hello",2)))
    this.out.println(str(iof("Hello",3)))
    this.out.println(str(iof("Hello",4)))
