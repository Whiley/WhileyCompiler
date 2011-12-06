import * from whiley.lang.*

define OpenRecord1 as {int field, ...}
define OpenRecord2 as {int field}

OpenRecord2 getField(OpenRecord1 r):
    return r

    
    
