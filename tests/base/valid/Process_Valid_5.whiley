
void System::main([string] args):
    // the should override the implicit field 
    // scope of the field "out" in System.
    out = 1
    
    // we're forced to used this here since out is now a local
    // variable.
    this.out.println(str(out))
