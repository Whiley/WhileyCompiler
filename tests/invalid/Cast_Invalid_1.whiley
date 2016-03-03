type boolrec is {bool b}

public export method test():
    boolrec r = {b: true}
    assume (bool) r.b