import println from whiley.lang.System

define Matrix as [[int]] where no { i in 0..|$|,j in 0..|$| | |$[i]| != |$[j]| }

Matrix run(Matrix A, Matrix B) requires |A| > 0 && |B| > 0 && |B| == |A[0]|:
    C = []
    for i in 0 .. |A|:
        row = []
        for j in 0 .. |B[0]|:
            r = 0
            for k in 0 .. |B|:
                r = r + (A[i][k] * B[k][j])
            row = row + [r]
        C = C + [row]
    return C

void ::main(System.Console sys):
    m1 = [[1,2],[3,4]]
    m2 = [[5,6],[7,8]]
    m3 = run(m1,m2)
    sys.out.println(m3)    
