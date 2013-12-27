import println from whiley.lang.System

// this implements what is effectively a "raw" interface

define FileReader as ref {
    int position
}

define Reader as { 
    FileReader thus,
    int(FileReader)::(int) read
}

int FileReader::read(int amount):
    r = amount + this->position
    return r
    
Reader ::openReader():
    proc = new { position: 123 }
    return { thus: proc, read: &read } 

void ::main(System.Console sys):
    reader = openReader()
    target = reader.thus
    method = reader.read
    data = target.method(5)
    sys.out.println(Any.toString(data))


