import println from whiley.lang.System

type nat is int where $ >= 0

function match([byte] data, nat offset, nat end) => int:
    pos = end
    len = 0
    while (offset < pos) && ((pos < |data|) && (data[offset] == data[pos])) where (pos >= 0) && (offset >= 0):
        offset = offset + 1
        pos = pos + 1
        len = len + 1
    return len

method main(System.Console sys) => void:
    xs = [00000000b, 00000101b, 00000000b, 00000110b, 00000000b, 00000101b]
    xs = match(xs, 0, |xs| - 2)
    sys.out.println(Any.toString(xs))
