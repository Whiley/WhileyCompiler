import println from whiley.lang.System

// this implements what is effectively a "raw" interface

define Reader as { 
    int::(int) read
}

int ::f(int amount):
    return 1

int ::m(Reader r, int x):
    return r.read(x)    

void ::main(System.Console sys):
    reader = { read: &f }
    data = m(reader,1)
    sys.out.println(Any.toString(data))


