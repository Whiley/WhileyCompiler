public type State<S> is { S field }

public type Action<S> is {
    method(&State<S>)->(Action<S>[]) apply
}

public function alert<S>() -> Action<S>:
    return Action{apply: &(&State<S> st -> [])}

public export method test():
    &State<int> st = new {field: 0}
    Action<int> as = alert<int>()
    Action<int>[] result = as.apply(st)
    assume |result| == 0