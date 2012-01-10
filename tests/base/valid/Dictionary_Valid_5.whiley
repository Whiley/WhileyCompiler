import * from whiley.lang.*

define DL as {int=>[real]}

DL update(DL ls):
    ls[0][0] = 1.234
    return ls

void ::main(System sys,[string] args):
    x = {0=>[1.0,2.0,3.0], 1=>[3.4]}
    x = update(x)
    sys.out.println(Any.toString(x))