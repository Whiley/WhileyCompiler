import * from whiley.lang.*

define sr5nat as int

void ::main(System sys,[string] args):
    x = {f:1}
    x.f = 2
    sys.out.println(toString(x))
    
