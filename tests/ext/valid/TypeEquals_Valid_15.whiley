import println from whiley.lang.System

define nat as int where $ >= 0

[nat] create(nat size, nat value):
    r = []
    i = 0
    while i < size where r is [nat]:
        r = r + [value]
        i = i + 1
    return r

void ::main(System.Console sys):
    sys.out.println(create(10,10))
    sys.out.println(create(5,0))
    sys.out.println(create(0,0))
    










    
    
    