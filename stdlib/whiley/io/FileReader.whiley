package whiley.io

define FileReader as process { string fileName }

// not sure if this makes sense per se
FileReader System::openFile(string fileName):
    extern jvm:
        aload 1
        invokestatic wyil/jvm/rt/WhileyIO.openFile:(Lwyil/jvm/rt/WhileyList;)Lwyil/jvm/rt/WhileyProcess;
        areturn
    // the following line is dead code
    return spawn {fileName: ""}

void FileReader::close():
    extern jvm:
        aload 0
        invokestatic wyil/jvm/rt/WhileyIO.closeFile:(Lwyil/jvm/rt/WhileyProcess;)V;
    
// read at most max bytes 
[byte] FileReader::read(int max):
    extern jvm:
        aload 0
        aload 1
        invokestatic wyil/jvm/rt/WhileyIO.readFile:(Lwyil/jvm/rt/WhileyProcess;Ljava/math/BigInteger;)Lwyil/jvm/rt/WhileyList;
        areturn
    return []
