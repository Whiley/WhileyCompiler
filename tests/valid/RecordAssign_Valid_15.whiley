type State is {
    int|null current
}

function update(State s, int mode) -> (State r):
    // Adapted from #950
    if s.current is int:
        s.current = null
    else:
        s.current = mode
    //
    return s

public export method test():
    assume update({current:null},123).current == 123
    assume update({current:123},123).current == null