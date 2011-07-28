int get({string->int} env):
    return env["x"]

void System::main([string] args):
    if |args| == 10:
        out.println("GOT HERE")
    else:
        env = get({"x"->1,"y"->2})
        out.println(str(env))
