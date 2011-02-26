define state as {string input, int pos}
define SyntaxError as {string msg}

[Move]|SyntaxError parseChessGame(string input):
    pos = 0
    finished = false
    moves = []
    while pos < |input| || finished:        
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
    while pos < |input| && (input[pos] == '\n' && input[pos] == '\r'):
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
    lookahead = input[0]
    index = 1
    
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
        index = 0
    // now, add piece color
    piece = {kind: piece, colour: isWhite}
    from = parsePos(input[index..index+2])
    if input[index+2] == 'x':
        isTake = true
    else:
        isTake = false
    to = parsePos(input[index+3..index+5])
    return { piece: piece, from: from, to: to }

Pos parsePos(string input):
    c = input[0] - 'a'
    r = input[1] - '0'
    return { col: c, row: r }
