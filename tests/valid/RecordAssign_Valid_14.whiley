type Buffer is { int[] data, int wp }

function update(Buffer b, int i, int v) -> (Buffer r)
requires i >= 0 && i < |b.data|
ensures b.wp == r.wp:
    //
    b.data[i] = v
    return b

public export method test():
    Buffer b = { data: [0,0,0,0], wp: 0 }
    //
    assume update(b,0,10) == { data: [10,00,00,00], wp: 0 }
    assume update(b,1,10) == { data: [00,10,00,00], wp: 0 }
    assume update(b,2,10) == { data: [00,00,10,00], wp: 0 }
    assume update(b,3,10) == { data: [00,00,00,10], wp: 0 }