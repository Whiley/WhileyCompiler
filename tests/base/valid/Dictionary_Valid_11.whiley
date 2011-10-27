import * from whiley.lang.*
import * from whiley.io.File

public void ::main(System sys, [string] args):
    l = {1->2,2->3}
    sys.out.println("Dictionary: " + String.toString(l))
    sys.out.println("Length: " + String.toString(|l|))
    l[3] = 123
    sys.out.println("Dictionary: " + String.toString(l))
    sys.out.println("Length: " + String.toString(|l|))
