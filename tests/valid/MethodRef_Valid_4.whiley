final int SUBTRACT = 1

public type OtherState is { int mode, int accumulator }

function push(int mode, OtherState s) -> (OtherState r):
    s.mode = SUBTRACT
    return s

public type Transformer is function(OtherState)->(OtherState)
public final Transformer ADDER = &(OtherState s -> push(1,s))

public export method test():
    OtherState s = { mode: 0, accumulator: 0}
    Transformer t = ADDER
    assume t(s) == { mode: 1, accumulator: 0}