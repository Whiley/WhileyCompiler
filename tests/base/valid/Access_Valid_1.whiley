import println from whiley.lang.System

define listdict as [int]|{int=>string}

any index(listdict l, int index):
    return l[index]

void ::main(System.Console sys):
    l = [1,2,3]
    sys.out.println(index(l,1))
    sys.out.println(index(l,2))
    l = {1=>"hello",2=>"cruel",3=>"world"}
    sys.out.println(index(l,0))
    sys.out.println(index(l,2))

