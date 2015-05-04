

function get({[int]=>int} env) -> int:
    return env["x"]

public export method test() -> void:
    assume get({"x"=>1, "y"=>2}) == 1

