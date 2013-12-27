import println from whiley.lang.System

int get({string=>int} env):
    return env["x"]

void ::main(System.Console sys):
    if |sys.args| == 10:
        sys.out.println("GOT HERE")
    else:
        env = get({"x"=>1,"y"=>2})
        sys.out.println(Any.toString(env))
