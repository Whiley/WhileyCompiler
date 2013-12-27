

{int} sum({int} xs):
    rs = {}
    for x in xs where |rs| <= 2:
        rs = rs + {x}
    return rs

void ::main(System.Console sys):
    z = sum({1,2,3,4,5})
    debug Any.toString(z)
    
