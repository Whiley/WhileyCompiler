type State is { int mode }
type Transformer is function(State)->(State)

function push(State s, int mode) -> (State r):
    s.mode = mode
    return s

public final Transformer ADDER = &(State s -> push(s,1))

public export method test():
    State s = {mode: 0}
    Transformer fn = ADDER
    s = fn(s)
    assume s.mode == 1
