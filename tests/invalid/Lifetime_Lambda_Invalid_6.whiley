// missing context lifetime

type mymethod is method()->(int)

public export method test()->mymethod:
    &this:int x = new 1
    return &[this](-> (*x) + (*(new 1)))
