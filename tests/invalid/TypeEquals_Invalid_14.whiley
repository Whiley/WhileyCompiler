type State is {
    int|null current
}

method f(State s) -> {int current}:
    //
    if s.current is int:
        s.current = null
        return s
    else:
        return {current:0}
      
