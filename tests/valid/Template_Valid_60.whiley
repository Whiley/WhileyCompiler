type Box<T> is null | { T value }

function empty<T>() -> (Box<T> r)
ensures r is null:
    return null

function box<T>(T value) -> (Box<T> r)
ensures !(r is null) && r.value == value:
    return { value: value }

function get<T>(Box<T> box, T dEfault) -> (T r)
ensures (box is null) ==> (r == dEfault)
ensures !(box is null) ==> (r == box.value):
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
