template<T>
type Decorator is { T data }

instantiate Decorator<int> as DecInt

public export method test():
    DecInt di = { data: 0 }
    assert di.data == 0