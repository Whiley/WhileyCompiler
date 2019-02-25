type Decorator<T> is { T data }

public export method test():
    Decorator<int> di = { data: 0 }
    assert di.data == 0
    Decorator<bool> db = { data: false }
    assert db.data == false