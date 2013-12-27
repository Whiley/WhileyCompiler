import println from whiley.lang.System

(int,int) f({int=>int} dict):
    k = 0
    v = 0
    for x,y in dict:
        k = k + x
        v = v + y
    return k,v

void ::main(System.Console sys):
    dict = {1=>2,3=>4,4=>5}
    k,v = f(dict)
    sys.out.println(Any.toString(k))
    sys.out.println(Any.toString(v))        
