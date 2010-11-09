import whiley.io.*

void System::main([string] args):
    fin = this->openFile("test.whiley")
    bytes = fin->read(3)
    out->println(bytes)
