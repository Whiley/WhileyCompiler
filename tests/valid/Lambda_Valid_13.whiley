type fptr is function(int)->(int)

function apply(fptr f, int x) -> (int r):
    //
    return f(x)

function id(int x) -> (int y):
    return x

function inc(int x) -> (int y):
    return x+1

public export method test():
    assert apply(&id,1) == 1
    assert apply(&inc,1) == 2
    assert apply(&(int x -> x),123) == 123
    assert apply(&(int x -> x+1),123) == 124
    