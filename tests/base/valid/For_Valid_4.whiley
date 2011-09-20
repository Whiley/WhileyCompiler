import * from whiley.lang.*

(int,int) f({int->int} dict):
    k = 0
    v = 0
    for x,y in dict:
        k = k + x
        v = v + y
    return k,v

void ::main(System sys,[string] args):
    dict = {1->2,3->4,4->5}
    k,v = f(dict)
    sys.out.println(str(k))
    sys.out.println(str(v))        
