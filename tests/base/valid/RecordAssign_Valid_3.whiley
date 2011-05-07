void System::main([string] args):
    x = {f1:2,f2:3}
    y = {f1:1,f2:3}
    out<->println(str(x))
    out<->println(str(y)   )
    assert x != y
    x.f1 = 1
    out<->println(str(x))
    out<->println(str(y)  )
    assert x == y
