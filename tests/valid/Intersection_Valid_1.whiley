type IntInt is int&int
type NotBoolInt is ((!bool)&int x)
type IntNotBool is (int&(!bool) x)

function f1(IntInt i) -> (int r):
    return i

function f2(int i) -> (IntInt r):
    return i

function f3(NotBoolInt i) -> (int r):
    return i

function f4(int i) -> (NotBoolInt r):
    return i

function f5(IntNotBool i) -> (int r):
    return i

function f6(int i) -> (IntNotBool r):
    return i

public export method test() :
    assume f1(1) == 1
    assume f2(1) == 1
    assume f3(1) == 1
    assume f4(1) == 1
    assume f5(1) == 1
    assume f6(1) == 1    
