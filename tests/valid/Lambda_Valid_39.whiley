public type Action<S> is method(S)->(S[])

method dup<S>(S item) -> S[]:
    return [item,item]

method run<S>(S item):
    Action<S> action = &(S st -> dup<S>(st))
    S[] result = action(item)
    assume result == [item,item]

public export method test():
    run<int>(0)