import whiley.lang.*

function get({[int]=>int} env) -> int:
    return env["x"]

method main(System.Console sys) -> void:
    assume get({"x"=>1, "y"=>2}) == 1

