void System::main([string] args):
    x = true
    y = false
    this.out.println(str(x))
    this.out.println(str(y))
    this.out.println("AND")
    x = x && y
    this.out.println(str(x))
    this.out.println("NOT")
    this.out.println(str(!x))
