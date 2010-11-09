package whiley.io

define FileInputStream as process { string fileName }

// not sure if this makes sense per se
FileInputStream System::openFile(string fileName):
    extern jvm:
        aload 1
        invokestatic wyil/jvm/rt/WhileyIO.openFile:(Lwyil/jvm/rt/WhileyList;)Lwyil/jvm/rt/WhileyProcess;
        areturn
    // the following line is dead code
    return spawn {fileName: ""}
