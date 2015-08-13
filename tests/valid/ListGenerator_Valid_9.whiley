function f() -> (bool,bool)[]:
    return [(false,false); 2]

public export method test():
    assume f() == [(false,false),(false,false)]
