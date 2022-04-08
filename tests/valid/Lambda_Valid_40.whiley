public type Action<S> is method(S)->(Action<S>[])

public function alert<S>(S item) -> Action<S>:
    return &(S st -> apply_alert(item))

method apply_alert<S>(S item) -> Action<S>[]:
    return []

public export method test():
    Action<int> action = alert<int>(123)
    Action<int>[] result = action(0)
    assume |result| == 0