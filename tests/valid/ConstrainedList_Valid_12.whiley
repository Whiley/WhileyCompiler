import println from whiley.lang.System

define nat as int where $ >= 0

int get([nat] ls, int i) requires i >=0 && i <= |ls|, ensures $ >= 0:
    if(i == |ls|):
        return 0
    else:
        return ls[i]

void ::main(System.Console sys):
    xs = [1,3,5,7,9,11]
    c = get(xs,0)
    sys.out.println(Any.toString(c))
    
