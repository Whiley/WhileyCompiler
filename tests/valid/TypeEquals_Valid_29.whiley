import whiley.lang.System

type RowCol is int

type Pos is {RowCol col, RowCol row}

type RankPos is {int row}

type FilePos is {int col}

type ShortPos is Pos | RankPos | FilePos | null

function pos2str(Pos p) -> ASCII.string:
    return ['a' + p.col, '1' + p.row]

function shortPos2str(ShortPos p) -> ASCII.string:
    if p is null:
        return ""
    else:
        if p is RankPos:
            return ['1' + p.row]
        else:
            if p is FilePos:
                return ['a' + p.col]
            else:
                return pos2str(p)

public method main(System.Console sys) -> void:
    sys.out.println(shortPos2str(null))
    sys.out.println(shortPos2str({row: 1}))
    sys.out.println(shortPos2str({col: 1}))
    sys.out.println(shortPos2str({col: 2, row: 1}))
