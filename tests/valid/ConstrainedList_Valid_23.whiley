

type Matrix is ([[int]] rows)
    where no {
        i in 0 .. |rows|, j in 0 .. |rows| | |rows[i]| != |rows[j]|
    }

function run(Matrix A, Matrix B) -> Matrix
requires (|A| > 0) && ((|B| > 0) && (|B| == |A[0]|)):
    [[int]] C = []
    int i = 0
    while i < |A|:
        [int] row = []
        int j = 0
        while j < |B[0]|:
            int r = 0
            int k = 0 
            while k < |B|:
                r = r + (A[i][k] * B[k][j])
                k = k + 1            
            row = row ++ [r]
            j = j + 1        
        C = C ++ [row]
        i = i + 1
    return C

public export method test() -> void:
    Matrix m1 = [[1, 2], [3, 4]]
    Matrix m2 = [[5, 6], [7, 8]]
    Matrix m3 = run(m1, m2)
    assume m3 == [[19, 22], [43, 50]]
