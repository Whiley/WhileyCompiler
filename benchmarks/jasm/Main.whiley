import whiley.io.*

void System::main([string] args):
    if |args| == 0:
        this->usage()
        return
    file = this->openFile(args[0])
    contents = file->read()
    parseClassFile(contents)

void System::usage():
    out->println("usage: jasm [options] file(s)")