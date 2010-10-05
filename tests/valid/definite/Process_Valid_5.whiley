
void System::main([string] args):
    // the should override the implicit field 
    // scope of the field "out" in System.
    out = 1
    out->println(str(out))
