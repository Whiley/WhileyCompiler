import whiley.io.*

void System::main([string] args):
    fin = this->openFile("test.whiley")
    out->println(str(fin))
