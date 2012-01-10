import * from whiley.lang.*

define sr5nat as int where $ > 0

void ::main(System.Console sys,[string] args):
    x = {f:1}
    x.f = 2
    sys.out.println(Any.toString(x))
    
