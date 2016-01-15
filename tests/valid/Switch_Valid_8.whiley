

public export method test() :
    int x = 1
    switch x:
        case 1:
            assume true
            return
        case 2:
            assume false
            return
    //
    assume false
