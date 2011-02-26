import whiley.io.*

void System::main([string] args):
    if |args| == 0:
        this->usage()
        return
    file = this->openFile(args[0])
    contents = file->read()
    game = parseChessGame(contents)
    if game ~= SyntaxError:
        out->println("syntax error: " + game.msg)
    else:
        for m in game:
            out->println(str(m))

void System::usage():
    out->println("usage: chess file")

string str(Move m):
    if m ~= SingleMove:
        return str(m.piece) + str(m.from) + "-" + str(m.to)
    else if m ~= SingleTake:
        return str(m.piece) + str(m.from) + "x" + str(m.to)
    else:
        // check move
        return str(m.move) + "+"  

string str(Piece p):
    if p.kind == PAWN:
        return ""
    else:
        return [PIECE_CHARS[p.kind]]

string str(Pos p):
    return ['a' + p.col,'0' + p.row]
