public bool ::method(int x, int y):
    return x < y

public void ::main(System.Console console):
    // the following should fail because an assertion must be "pure".
    // That is, since it is optional and may be eliminated at
    // compile-time, it cannot have any side-effects.
    assert method(10,20)

