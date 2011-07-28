bool run(int n, int x):
    solution = true
    for i in 0..n:
        if i == x:
            solution = false
            break
    return solution

void System::main([string] args):
    b1 = run(10,4)
    out.println("b1=" + str(b1))
    b2 = run(10,-1)
    out.println("b2=" + str(b2))
    b3 = run(10,11)
    out.println("b3=" + str(b3))