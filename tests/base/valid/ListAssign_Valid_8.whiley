[[int]] update([[int]] ls):
    ls[0][0] = 10
    return ls

([[int]],[[int]]) f([[int]] ls):
    nls = update(ls)
    return ls,nls

void System::main([string] args):
    ls = [[1,2,3,4]]
    ls,nls = f(ls)
    out->println(str(ls))
    out->println(str(nls))
