define state as {string input, int pos}
define SyntaxError as {string msg}

[Move]|SyntaxError parseChessGame(string input):
    pos = 0
    finished = false
    moves = []
    while pos < |input| && !finished:        
        line = parseLine(input,pos)
        split = splitLine(line)
        whiteMove = parseMove(split[0], true)
        moves = moves + [whiteMove]
        if |split| == 2:
            blackMove = parseMove(split[1], false) 
            moves = moves + [blackMove]
        else:   
            finished = true
        pos = nextLine(input,pos+|line|)
    return moves

string parseLine(string input, int pos):
    start = pos
    while pos < |input| && input[pos] != '\n':
        pos = pos + 1
    return input[start..pos]

int nextLine(string input, int pos):
    while pos < |input| && (input[pos] == '\n' || input[pos] == '\r'):
        pos = pos + 1
    return pos

[string] splitLine(string input):
    pos = 0
    while pos < |input| && input[pos] != ' ':
        pos = pos + 1
    splits = [input[0..pos]]
    pos = pos + 1
    if pos < |input|:
        start = pos
        while pos < |input| && input[pos] != ' ':
            pos = pos + 1
        splits = splits + [input[start..pos]]    
    return splits        

Move parseMove(string input, bool isWhite):
    // first, we check for castling moves
    if |input| >= 5 && input[0..5] == "O-O-O":
        move = { isWhite: isWhite, kingSide: false }
        index = 5
    else if |input| >= 3 && input[0..3] == "O-O":
        move = { isWhite: isWhite, kingSide: true }
        index = 3
    else:
        // not a castling move
        index = parseWhiteSpace(0,input)
        piece = parsePiece(input[index],isWhite)
        if piece.kind != PAWN:
            index = index + 1
        from = parsePos(input[index..index+2])
        index = index + 2
        if input[index] == 'x':
            index = index + 1
            taken = parsePiece(input[index],!isWhite)
            if taken.kind != PAWN:
                index = index + 1
            to = parsePos(input[index..index+2])
            index = index + 2
            move = { piece: piece, from: from, to: to, taken: taken }
        else:
            to = parsePos(input[index+1..index+3])
            move = { piece: piece, from: from, to: to }
            index = index + 3
    // finally, test for a check move
    if index < |input| && input[index] == '+':
        move = {check: move} 
    return move

Piece parsePiece(char lookahead, bool isWhite):
    if lookahead == 'N':
        piece = KNIGHT
    else if lookahead == 'B':
        piece = BISHOP
    else if lookahead == 'R':
        piece = ROOK
    else if lookahead == 'K':
        piece = KING
        index = 1
    else if lookahead == 'Q':
        piece = QUEEN
    else:    
        // ignoring castling for now
        piece = PAWN
    return {kind: piece, colour: isWhite}
    
Pos parsePos(string input):
    c = input[0] - 'a'
    r = input[1] - '1'
    return { col: c, row: r }

int parseWhiteSpace(int index, string input):
    while index < |input| && isWhiteSpace(input[index]):
        index = index + 1
    return index


bool isWhiteSpace(char c):
    return c == ' ' || c == '\t'


