import println from whiley.lang.System

define RowCol as int // where 0 <= $ && $ <= 8
define Pos as { RowCol col, RowCol row } 

define RankPos as { int row }
define FilePos as { int col }
define ShortPos as Pos | RankPos | FilePos | null

string pos2str(Pos p):
    return "" + (char) ('a' + p.col) + (char) ('1' + p.row)

string shortPos2str(ShortPos p):
    if p is null:
        return ""
    else if p is RankPos:
        return "" + (char) ('1' + p.row)
    else if p is FilePos:
        return "" + (char) ('a' + p.col)
    else: 
        return pos2str(p)

public void ::main(System.Console sys):
    sys.out.println(shortPos2str(null))
    sys.out.println(shortPos2str({ row: 1}))
    sys.out.println(shortPos2str({ col: 1}))
    sys.out.println(shortPos2str({ row: 1, col: 2}))
