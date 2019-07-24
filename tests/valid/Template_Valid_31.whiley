public type Attribute<T> is { function handler(T)->(T) }
public type Node<T> is { Attribute<T>[] attributes }

public function h1<T>() -> Node<T>:
    return { attributes: [] }

public type State is { bool flag }

function view(State s) -> Node<State>:
    return h1<State>()

public export method test():
    //
    assume view({flag:false}) == { attributes: [] }