import whiley.lang.*:*

define FileReader as {
    int position,
    [byte] data
}

define Reader as { 
    int(FileReader)::(int) read
}
