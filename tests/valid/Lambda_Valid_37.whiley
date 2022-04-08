public type Action<S> is method(S)->(S[])

method dup<S>(S item) -> S[]:
    return [item,item]

public export method test():
    Action<int> action = &(int st -> dup<int>(0))
    int[] result = action(0)
    assume result == [0,0]