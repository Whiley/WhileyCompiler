type State is { int current }
type Transformer<T> is function(State)->(State)
type Node<T> is { Transformer<T> fn }

method push(State s) -> (State r):
    return s

method f<T>(Transformer<T> fn) -> Node<T>:
    return { fn: fn }

method main() -> Node<State>[]:
    return [f<State>(&(State st -> push(st)))]