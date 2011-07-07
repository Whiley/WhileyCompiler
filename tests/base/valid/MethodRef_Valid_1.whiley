// this implements what is effectively a "raw" interface

define FileReader as process {
    int position,
    [byte] data
}

define Reader as { 
    FileReader thus,
    int(FileReader)::(int) read
}

[byte] FileReader::read(int amount):
    end = position + amount
    r = this.data[position..end]
    this.position = this.position + amount
    return r
    
Reader openReader(FileReader fr):
    return { thus: fr, read: &read } 

void System::main([string] args):
    fr = spawn { position: 0, data: [1,2,3,4,5] }
    reader = openReader(fr)
    target = reader.thus
    method = reader.read
    data = target.method(5)
    out.println(str(data))


