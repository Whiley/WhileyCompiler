type mymethod is method()->(int)

public export method test():
    &int x = new 3
    mymethod m = &(->(*x))
    int y = m()
    assume y == 3
