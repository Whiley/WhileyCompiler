type fn1_t is function(int)->(int,int)
type fn2_t is function(int)->{int f}
type fn3_t is function(int)->int[]

public export method test():
    fn1_t f1 = &(int x -> (x,x))
    fn2_t f2 = &(int x -> {f:x})
    fn3_t f3 = &(int x -> [x])
    //
    assume f1(1) == (1,1)
    assume f2(2) == {f:2}
    assume f3(3) == [3]
