public type Action<S> is method(S)->(Action<S>[])

public function alert<S>() -> Action<S>:
    return &(S st -> apply_alert())

method apply_alert<S>() -> Action<S>[]:
    return []

public export method test():
    Action<int> action = alert<int>()
    Action<int>[] result = action(0)
    assume |result| == 0