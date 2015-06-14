

type nat is (int x) where x >= 0

type Matrix is {
    int height,
    int width,
    [[int]] data
} where |data| == height &&
        no { i in data | |i| != width }

function Matrix(nat width, nat height, [[int]] data) -> (Matrix result)
requires (|data| == height) && no { i in data | |i| != width }
ensures result.width == width && result.height == height && result.data == data:
    //
    return {height: height, width: width, data: data}

function run(Matrix A, Matrix B) -> (Matrix C)
requires A.width == B.height
ensures (C.width == B.width) && (C.height == A.height):
    //
    [[int]] C_data = []
    int i = 0
    while i < A.height:
        [int] row = []
        int j = 0
        while j < B.width:
            int r = 0
            int k = 0
            while k < A.width:
                r = r + (A.data[i][k] * B.data[k][j])
                k = k + 1
            row = row ++ [r]
            j = j + 1
        C_data = C_data ++ [row]
        i = i + 1
    return Matrix(B.width, A.height, C_data)

public export method test() -> void:
    Matrix m1 = Matrix(2, 2, [[1, 0], [-3, 2]])
    Matrix m2 = Matrix(2, 2, [[-1, 4], [3, 5]])
    Matrix m3 = run(m1, m2)
    assume m3 == {data:[[-1, 4], [9, -2]],height:2,width:2}
    m1 = Matrix(3, 2, [[1, 2, 3], [4, 5, 6]])
    m2 = Matrix(2, 3, [[1, 2], [3, 4], [5, 6]])
    m3 = run(m1, m2)
    assume m3 == {data:[[22, 28], [49, 64]],height:2,width:2}
    m1 = Matrix(3, 2, [[1, 2, 3], [4, 5, 6]])
    m2 = Matrix(4, 3, [[1, 2, 3, 4], [5, 6, 7, 8], [9, 10, 11, 12]])
    m3 = run(m1, m2)
    assume m3 == {data:[[38, 44, 50, 56], [83, 98, 113, 128]],height:2,width:4}
