import whiley.lang.*:*

// this implements what is effectively a "raw" interface

define FileReader as process {
    int position
}

define Reader as { 
    FileReader thus,
    int(FileReader)::(int) read
}

int FileReader::read(int amount):
    r = amount + this.position
    return r
    
Reader System::openReader():
    proc = spawn { position: 123 }
    return { thus: proc, read: &read } 

void System::main([string] args):
    reader = this.openReader()
    target = reader.thus
    method = reader.read
    data = target.method(5)
    this.out.println(str(data))


