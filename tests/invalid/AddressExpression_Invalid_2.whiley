type fun is function()->int

function foo() -> int:
    return 42

public export method test():
    fun x = &foo((->)
