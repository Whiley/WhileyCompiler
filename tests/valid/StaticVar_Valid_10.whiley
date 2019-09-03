type State is { int mode }
type Transformer is function(State)->(State)

function set(State s, int mode) -> (State r):
    s.mode = mode
    return s

public final Transformer SET1 = &(State s -> set(s,1))
public final Transformer SET2 = &(State s -> set(s,2))

public export method test():
    State s = {mode: 0}
    Transformer[] fns = [ SET1, SET2 ]
    Transformer f = fns[0]
    s = f(s)
    assume s.mode == 1
    f = fns[1]
    s = f(s)
    assume s.mode == 2
