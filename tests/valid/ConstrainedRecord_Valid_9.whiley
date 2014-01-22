import println from whiley.lang.System

type nat is int where $ >= 0

type Matrix is {int height, int width, [[int]] data} where (|data| == height) && no { i in data | |i| != width }

function Matrix(nat width, nat height, [[int]] data) => Matrix
requires (|data| == height) && no { i in data | |i| != width }
ensures ($.width == width) && (($.height == height) && ($.data == data)):
    return {height: height, width: width, data: data}

function run(Matrix A, Matrix B) => Matrix
requires A.width == B.height
ensures ($.width == B.width) && ($.height == A.height):
    C_data = []
    for i in 0 .. A.height:
        row = []
        for j in 0 .. B.width:
            r = 0
            for k in 0 .. A.width:
                r = r + (A.data[i][k] * B.data[k][j])
            row = row + [r]
        C_data = C_data + [row]
    return Matrix(B.width, A.height, C_data)

method main(System.Console sys) => void:
    m1 = Matrix(2, 2, [[1, 0], [-3, 2]])
    m2 = Matrix(2, 2, [[-1, 4], [3, 5]])
    m3 = run(m1, m2)
    sys.out.println(m3)
    m1 = Matrix(3, 2, [[1, 2, 3], [4, 5, 6]])
    m2 = Matrix(2, 3, [[1, 2], [3, 4], [5, 6]])
    m3 = run(m1, m2)
    sys.out.println(m3)
    m1 = Matrix(3, 2, [[1, 2, 3], [4, 5, 6]])
    m2 = Matrix(4, 3, [[1, 2, 3, 4], [5, 6, 7, 8], [9, 10, 11, 12]])
    m3 = run(m1, m2)
    sys.out.println(m3)
