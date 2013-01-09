import println from whiley.lang.System

[int] f([int] ls) ensures $ == []:
    if |ls| == 0:
        return ls
    else:
        return []

void ::main(System.Console sys):
    items = [5,4,6,3,7,2,8,1]
    sys.out.println(f(items))
    sys.out.println(f([]))