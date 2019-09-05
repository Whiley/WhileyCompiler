type Open is { int kind, ... }

type State is {
    int kind,
    int|null current
}

function update(Open s, int mode) -> (Open r):
    // Adapted from #950
    if s is State:
        s.current = mode
    //
    return s

public export method test():
    assume update({kind:2,current:null},123) == {kind:2, current: 123}
    assume update({kind:1,current:234},123) == {kind:1, current: 123}
    assume update({kind:3},123) == {kind:3}