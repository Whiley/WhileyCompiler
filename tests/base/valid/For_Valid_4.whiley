import whiley.lang.*:*

(int,int) f({int->int} dict):
    k = 0
    v = 0
    for x,y in dict:
        k = k + x
        v = v + y
    return k,v

void System::main([string] args):
    dict = {1->2,3->4,4->5}
    k,v = f(dict)
    this.out.println(str(k))
    this.out.println(str(v))        
