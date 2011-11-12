import * from whiley.lang.*

define intlist as {int}

void ::main(System sys,[string] args):    
     il = {1,2,3}
     sys.out.println(|il|)
     sys.out.println(il[0])
