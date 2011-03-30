import whiley.io.*

void System::main([string] args):
    file = this->openFile(args[0])
    contents = file->read()
    out->println("GOT: " + hexStr(contents[0]))
    out->println("GOT: " + hexStr(contents[1]))
    out->println("GOT: " + hexStr(contents[2]))
    out->println("GOT: " + hexStr(contents[3]))
     
    
