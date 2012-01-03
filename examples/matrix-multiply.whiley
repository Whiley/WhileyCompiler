import * from whiley.lang.*
import * from whiley.io.File

// ========================================================
// Benchmark
// ========================================================

define Matrix as [[int]]
define Multiplier as process { int i, int j, int r, Matrix A, Matrix B }

void Multiplier::start():
    r = 0
    for k in 0 .. |this.A|:
        r = r + (this.A[this.j][k] * this.B[k][this.i])
    this.r = r

int Multiplier::get():
    return this.r

// ========================================================
// Parser
// ========================================================

(Matrix,Matrix) parseFile(string input) throws SyntaxError:
    data,pos = parseLine(2,0,input)    
    nrows = data[0]
    ncols = data[1]
    A,pos = parseMatrix(nrows,ncols,pos,input)
    B,pos = parseMatrix(nrows,ncols,pos,input)
    return A,B

(Matrix,int) parseMatrix(int nrows, int ncols, int pos, string input) throws SyntaxError:    
    rows = []
    for i in 0..nrows:
        row,pos = parseLine(ncols,pos,input)
        rows = rows + [row]
    return rows,pos
        
([int],int) parseLine(int count, int pos, string input) throws SyntaxError:
    pos = skipWhiteSpace(pos,input)
    ints = []
    while pos < |input| && |ints| != count:       
        i,pos = parseInt(pos,input)
        ints = ints + [i]
        pos = skipWhiteSpace(pos,input)
    if |ints| != count:  
        throw SyntaxError("invalid input file",pos,pos)
    return ints,pos

(int,int) parseInt(int pos, string input) throws SyntaxError:
    start = pos
    while pos < |input| && Char.isDigit(input[pos]):
        pos = pos + 1
    if pos == start:
        throw SyntaxError("Missing number",pos,pos)
    return Int.parse(input[start..pos]),pos

int skipWhiteSpace(int index, string input):
    while index < |input| && isWhiteSpace(input[index]):
        index = index + 1
    return index

bool isWhiteSpace(char c):
    return c == ' ' || c == '\t' || c == '\r' || c == '\n' || c == '-'

// ========================================================
// Main
// ========================================================

Multiplier ::createMultiplier(int i, int j, Matrix A, Matrix B):
    return spawn { i: i, j: j, r: 0, A: A, B: B }

[[Multiplier]] ::createMultipliers(int n, Matrix A, Matrix B):
    rows = []
    for i in 0..n:
        row = []
        for j in 0..n:
            m = createMultiplier(j,i,A,B)
            row = row + [m]
        rows = rows + [row]
    return rows

Matrix ::run(Matrix A, Matrix B):
    // first, create the matrix of multipliers
    muls = createMultipliers(|A|,A,B)
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

void ::printMat(System sys, Matrix A):
    for i in 0 .. |A|:
        row = A[i]
        for j in 0 .. |row|:
            sys.out.print(row[j])
            sys.out.print(" ")
        sys.out.println("")

void ::main(System sys, [string] args):
    file = File.Reader(args[0])
    // first, read data
    input = String.fromASCII(file.read())
    // second, build the matrices
    try:
        A,B = parseFile(input)
        // third, run the benchmark
        C = run(A,B)    
        // finally, print the result!
        printMat(sys,C)
    catch(SyntaxError e):
        sys.out.println("syntax error: " + e.msg)
