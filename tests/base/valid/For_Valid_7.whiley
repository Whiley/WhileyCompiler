import * from whiley.lang.*

bool run(int n, int x):
    solution = true
    for i in 0..n:
        if i == x:
            solution = false
            break
    return solution

void ::main(System sys,[string] args):
    b1 = run(10,4)
    sys.out.println("b1=" + str(b1))
    b2 = run(10,-1)
    sys.out.println("b2=" + str(b2))
    b3 = run(10,11)
    sys.out.println("b3=" + str(b3))