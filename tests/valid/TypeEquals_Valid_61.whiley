// Example from #936
type Point is {int x, int y}
type Location is {int x, int y}

function isPoint(Point|Location pl) -> (bool r):
    if pl is Point:
        return true
    else: 
        return false

public export method test():
    Point p = {x:1, y:2}
    Location l1 = {x:1, y:2}    
    Location l2 = {x:100, y:2033}
    //
    assume isPoint(p)
    assume !isPoint(l1)
    assume !isPoint(l2)