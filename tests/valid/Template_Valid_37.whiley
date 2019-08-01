// from std::vector
public type Vector<T> is {
    T[] items,
    int length
} where 0 <= length && length <= |items|

public function Vector<T>() -> Vector<T>:
    return { items: [], length: 0 }

// from #912
type State is { Vector<int> stack }

function State() -> State:
    return { stack: Vector<int>() }

public export method test():
    // Simple check
    assume State().stack == { items: [], length: 0 }