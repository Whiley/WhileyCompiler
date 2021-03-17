// This function is infeasible when a==1.
// So we must prove feasibility before using axioms about the function results.

function f(int a) -> (int r)
requires 0 < a
requires a <= 2
ensures (2 * r) == a:
    //
    int b = f(1)
    return 1

public export method test():
    assert f(2) == 1