

type Matrix is ([[int]] rows)
    where no {
        i in 0 .. |rows|, j in 0 .. |rows| | |rows[i]| != |rows[j]|
    }

function run(Matrix A, Matrix B) -> Matrix
requires (|A| > 0) && ((|B| > 0) && (|B| == |A[0]|)):
    [[int]] C = []
    for i in 0 .. |A|:
        [int] row = []
        for j in 0 .. |B[0]|:
            int r = 0
            for k in 0 .. |B|:
                r = r + (A[i][k] * B[k][j])
            row = row ++ [r]
        C = C ++ [row]
    return C

public export method test() -> void:
    Matrix m1 = [[1, 2], [3, 4]]
    Matrix m2 = [[5, 6], [7, 8]]
    Matrix m3 = run(m1, m2)
    assume m3 == [[19, 22], [43, 50]]
