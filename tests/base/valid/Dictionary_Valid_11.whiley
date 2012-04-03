import println from whiley.lang.System
import println from whiley.io.File

public void ::main(System.Console sys):
    l = {1=>2,2=>3}
    sys.out.println("Dictionary: " + Any.toString(l))
    sys.out.println("Length: " + Any.toString(|l|))
    l[3] = 123
    sys.out.println("Dictionary: " + Any.toString(l))
    sys.out.println("Length: " + Any.toString(|l|))
