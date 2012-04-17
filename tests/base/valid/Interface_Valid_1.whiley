import println from whiley.lang.System

define Reader as interface {
    [byte] read(int)
}

define FileReader as ref {
    int position,
    [byte] data
}

[byte] FileReader::read(int amount):
    end = position + amount
    r = this->data[position..end]
    this->position = this->position + amount
    return r
    
Reader openReader(FileReader fr):
    return fr // coerce to interface

void ::main(System.Console sys):
    fr = new { position: 0, data: [1,2,3,4,5] }
    reader = openReader(fr)
    data = reader.read(5)
    sys.out.println(Any.toString(data))

