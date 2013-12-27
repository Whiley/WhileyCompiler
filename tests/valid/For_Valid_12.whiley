import println from whiley.lang.System

define nat as int where $ >= 0
void ::main(System.Console sys):
    xs = [1,2,3]
    r = 0
    for x in xs where r >= 0:
        r = r + x    
    sys.out.println(Any.toString(r))
