import println from whiley.lang.System

define nat as int where $ >= 0

int match([byte] data, nat offset, nat end):
    pos = end
    len = 0
    while offset < pos && pos < |data| && data[offset] == data[pos] where pos >= 0 && offset >= 0:
        offset = offset + 1
        pos = pos + 1
        len = len + 1
    return len
   
void ::main(System.Console sys):
    xs = [0b,101b,0b,110b,0b,101b]
    xs = match(xs,0,|xs|-2)
    sys.out.println(Any.toString(xs))








    
    
    