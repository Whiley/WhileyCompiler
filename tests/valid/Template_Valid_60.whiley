type Box<T> is null | { T value }

function empty<T>() -> Box<T>:
    return null

function box<T>(T value) -> Box<T>:
    return { value: value }

function get<T>(Box<T> box, T dEfault) -> T:
   if box is null:
      return dEfault
   else:
      return box.value

public export method test():
    Box<int> b1 = empty()
    Box<int> b2 = box(1)
    //
    assert get(b1,0) == 0
    assert get(b2,0) == 1