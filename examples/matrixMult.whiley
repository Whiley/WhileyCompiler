import whiley.io.*

// ========================================================
// Benchmark
// ========================================================

define Matrix as [[int]]
define Multiplier as process { int i, int j, int r, Matrix A, Matrix B }

void Multiplier::start():
    r = 0
    for k in 0 .. |A|:
        r = r + (A[j][k] * B[k][i])
    this.r = r

int Multiplier::get():
    return r

// ========================================================
// Parser
// ========================================================

(Matrix,Matrix) parseFile(string input):
    data,pos = parseLine(2,0,input)    
    nrows = data[0]
    ncols = data[1]
    A,pos = parseMatrix(nrows,ncols,pos,input)
    B,pos = parseMatrix(nrows,ncols,pos,input)
    return A,B

(Matrix,int) parseMatrix(int nrows, int ncols, int pos, string input):    
    rows = []
    for i in 0..nrows:
        row,pos = parseLine(ncols,pos,input)
        rows = rows + [row]
    return rows,pos
        
([int],int) parseLine(int count, int pos, string input):
    pos = skipWhiteSpace(pos,input)
    ints = []
    while pos < |input| && |ints| != count:       
        i,pos = parseInt(pos,input)
        ints = ints + i
        pos = skipWhiteSpace(pos,input)
    if |ints| != count:  
        throw { msg: "invalid input file" }
    return ints,pos

(int,int) parseInt(int pos, string input):
    start = pos
    while pos < |input| && isDigit(input[pos]):
        pos = pos + 1
    if pos == start:
        throw "Missing number"
    return str2int(input[start..pos]),pos

int skipWhiteSpace(int index, string input):
    while index < |input| && isWhiteSpace(input[index]):
        index = index + 1
    return index

bool isWhiteSpace(char c):
    return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '-'

// ========================================================
// Main
// ========================================================

Multiplier System::createMultiplier(int i, int j, Matrix A, Matrix B):
    return spawn { i: i, j: j, r: 0, A: A, B: B }

[[Multiplier]] System::createMultipliers(int n, Matrix A, Matrix B):
    rows = []
    for i in 0..n:
        row = []
        for j in 0..n:
            m = this.createMultiplier(j,i,A,B)
            row = row + m
        rows = rows + [row]
    return rows

Matrix System::run(Matrix A, Matrix B):
    // first, create the matrix of multipliers
    muls = this.createMultipliers(|A|,A,B)
    // second, start each multiplier running
    for i in 0 .. |muls|:
        mulRow = muls[i]
        for j in 0 .. |mulRow|:
            mulRow[j]!start()
    // finally, collect all the data
    rows = []
    for i in 0 .. |muls|:
        mulRow = muls[i]
        row = []
        for j in 0 .. |mulRow|:
            row = row + [mulRow[j].get()]
        rows = rows + [row]
    return rows

void System::printMat(Matrix A):
    for i in 0 .. |A|:
        row = A[i]
        for j in 0 .. |row|:
            out.print(str(row[j]))
            out.print(" ")
        out.println("")

void System::main([string] args):
    file = this.openReader(args[0])
    // first, read data
    input = ascii2str(file.read())
    // second, build the matrices
    A,B = parseFile(input)
    // third, run the benchmark
    C = this.run(A,B)    
    // finally, print the result!
    this.printMat(C)