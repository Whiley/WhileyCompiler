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








    
    
    