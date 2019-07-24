type handler<T> is function(T)->(T)
type State is { bool flag }

function id(int x) -> (int y):
    return x

function invert(State s) -> (State r):
    return {flag: !s.flag}

public export method test():
    handler<int> f_i = &id
    handler<State> f_s = &invert
    //
    assume f_i(0) == 0
    assume f_s({flag:false}) == {flag:true}
