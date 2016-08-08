type IntInt is int&int
type AnyInt is any&int
type IntAny is int&any

function f1(IntInt i) -> (int r):
    return i

function f2(int i) -> (IntInt r):
    return i

function f3(AnyInt i) -> (int r):
    return i

function f4(int i) -> (AnyInt r):
    return i

function f5(IntAny i) -> (int r):
    return i

function f6(int i) -> (IntAny r):
    return i

public export method test() :
    assume f1(1) == 1
    assume f2(1) == 1
    assume f3(1) == 1
    assume f4(1) == 1
    assume f5(1) == 1
    assume f6(1) == 1    
