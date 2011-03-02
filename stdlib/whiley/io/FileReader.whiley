package whiley.io

define FileReader as process { string fileName }

// not sure if this makes sense per se
FileReader System::openFile(string fileName):
    extern jvm:
        aload 1
        invokestatic wyil/jvm/rt/WhileyIO.openFile:(Ljava/util/ArrayList;)Lwyil/jvm/rt/WhileyProcess;
        areturn
    // the following line is dead code
    return spawn {fileName: ""}

void FileReader::close():
    extern jvm:
        aload 0
        invokestatic wyil/jvm/rt/WhileyIO.closeFile:(Lwyil/jvm/rt/WhileyProcess;)V;

// read the whole file
[byte] FileReader::read():
    extern jvm:
        aload 0
        invokestatic wyil/jvm/rt/WhileyIO.readFile:(Lwyil/jvm/rt/WhileyProcess;)Ljava/util/ArrayList;
        areturn
    return []
    
// read at most max bytes 
[byte] FileReader::read(int max):
    extern jvm:
        aload 0
        aload 1
        invokestatic wyil/jvm/rt/WhileyIO.readFile:(Lwyil/jvm/rt/WhileyProcess;Lwyil/jvm/rt/BigRational;)Ljava/util/ArrayList;
        areturn
    return []
