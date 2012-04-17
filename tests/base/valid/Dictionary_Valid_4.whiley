import println from whiley.lang.System

{int=>int} f(int x):
    return {1=>x, 3=>2}

int get(int i, {int=>int} map):
    return map[i]

void ::main(System.Console sys):
    m1 = f(1)
    m2 = f(2)
    m3 = f(3)
    
    m1[2] = 4
    m2[1] = 23498
    
    sys.out.println(Any.toString(get(1,m1)))
    sys.out.println(Any.toString(get(2,m1)))
    sys.out.println(Any.toString(get(1,m2)))
    sys.out.println(Any.toString(get(1,m3)))
    sys.out.println(Any.toString(get(3,m3)))
