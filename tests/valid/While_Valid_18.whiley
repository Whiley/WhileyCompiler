import whiley.lang.*

type nat is (int x) where x >= 0

function match([byte] data, nat offset, nat end) -> int:
    nat pos = end
    int len = 0
    while (offset < pos) && ((pos < |data|) && (data[offset] == data[pos])) where (pos >= 0) && (offset >= 0):
        offset = offset + 1
        pos = pos + 1
        len = len + 1
    return len

method main(System.Console sys) -> void:
    [byte] xs = [
        00000000b,
        00000101b,
        00000000b,
        00000110b,
        00000000b,
        00000101b
    ]
    int x = match(xs, 0, |xs| - 2)
    sys.out.println(x)
