type IntInt is int&int
type BoolInt is ((int|bool)&int x)
type IntBool is (int&(int|bool) x)

function f1(IntInt i) -> (int r):
    return i

function f2(int i) -> (IntInt r):
    return i

function f3(BoolInt i) -> (int r):
    return i

function f4(int i) -> (BoolInt r):
    return i

function f5(IntBool i) -> (int r):
    return i

function f6(int i) -> (IntBool r):
    return i

public export method test() :
    assume f1(1) == 1
    assume f2(1) == 1
    assume f3(1) == 1
    assume f4(1) == 1
    assume f5(1) == 1
    assume f6(1) == 1    
