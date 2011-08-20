import whiley.lang.*:*

int get({string->int} env):
    return env["x"]

void System::main([string] args):
    if |args| == 10:
        this.out.println("GOT HERE")
    else:
        env = get({"x"->1,"y"->2})
        this.out.println(str(env))
