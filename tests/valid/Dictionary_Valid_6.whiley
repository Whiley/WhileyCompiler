import whiley.lang.System

function get({[int]=>int} env) -> int:
    return env["x"]

method main(System.Console sys) -> void:
    if |sys.args| == 10:
        sys.out.println_s("GOT HERE")
    else:
        int env = get({"x"=>1, "y"=>2})
        sys.out.println(env)
