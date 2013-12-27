import println from whiley.lang.System

define nat as int where $ >= 0

define Matrix as {
    int width,
    int height,
    [[int]] data
} where |data| == height && no { i in data | |i| != width }

Matrix Matrix(nat width, nat height, [[int]] data) 
    requires |data| == height && no { i in data | |i| != width },
    ensures $.width == width && $.height == height && $.data == data:
    //
    return {
        width: width,
        height: height,
        data: data
    }

Matrix run(Matrix A, Matrix B) requires A.width == B.height,
    ensures $.width == B.width && $.height == A.height:
    //
    C_data = []
    for i in 0 .. A.height:
        row = []
        for j in 0 .. B.width:
            r = 0
            for k in 0 .. A.width:
                r = r + (A.data[i][k] * B.data[k][j])
            row = row + [r]
        C_data = C_data + [row]
    //
    return Matrix(B.width,A.height,C_data)

void ::main(System.Console sys):
    m1 = Matrix(2,2,[[1,0],[-3,2]])
    m2 = Matrix(2,2,[[-1,4],[3,5]])
    m3 = run(m1,m2)
    sys.out.println(m3)    
    m1 = Matrix(3,2,[[1,2,3],[4,5,6]])
    m2 = Matrix(2,3,[[1,2],[3,4],[5,6]])
    m3 = run(m1,m2)
    sys.out.println(m3)    
    m1 = Matrix(3,2,[[1,2,3],[4,5,6]])
    m2 = Matrix(4,3,[[1,2,3,4],[5,6,7,8],[9,10,11,12]])
    m3 = run(m1,m2)
    sys.out.println(m3)    
