

type RowCol is int

type Pos is {RowCol col, RowCol row}

type RankPos is {int row}

type FilePos is {int col}

type ShortPos is Pos | RankPos | FilePos | null

function pos2str(Pos p) -> int[]:
    return ['a' + p.col, '1' + p.row]

function shortPos2str(ShortPos p) -> int[]:
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

public export method test() :
    assume shortPos2str(null) == ""
    assume shortPos2str({row: 1}) == "2"
    assume shortPos2str({col: 1}) == "b"
    assume shortPos2str({col: 2, row: 1}) == "c2"
