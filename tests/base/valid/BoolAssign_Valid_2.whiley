void System::main([string] args):
    x = true
    y = false
    out<->println(str(x))
    out<->println(str(y))
    out<->println("AND")
    x = x && y
    out<->println(str(x))
    out<->println("NOT")
    out<->println(str(!x))
