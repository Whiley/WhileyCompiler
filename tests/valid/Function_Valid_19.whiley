import println from whiley.lang.System

{int} f({int} xs, int lb, int ub) 
requires
    // every element in xs is between lb and ub 
    no { x in xs | x < lb || x >= ub } &&
    // size of xs is difference between ub and lb
    |xs| == ub - lb:
    
    assert |xs| == 0 || lb in xs
    assert !(ub in xs)

    return xs

void ::main(System.Console sys):
    sys.out.println(f({10,11,12,13,14},10,15))
    sys.out.println(f({},10,10))
    










    
    
    