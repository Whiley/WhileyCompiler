// missing context lifetime

type mymethod is method()->(int)

public export method test()->mymethod:
    &this:int x = this:new 1
    return &(->*x)
