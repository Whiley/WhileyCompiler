type fun_t is function()->int

function select(int[] items, int i) -> int:
    if i >= 0 && i < |items|:
        return items[i]
    else:
        return 0

public export method test():
    int[] xs = [11,22,33]
    fun_t[] fs = [&(->255); 3]
    //
    int i = 0
    while i < |xs| where i >= 0 && |xs| == |fs|:
        fs[i] = &( -> select(xs,i))
        i = i + 1
    //
    fun_t f0 = fs[0]
    assume f0() == 11
    fun_t f1 = fs[1]
    assume f1() == 22
    fun_t f2 = fs[2]
    assume f2() == 33    