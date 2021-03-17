type IntRecord is {int f}
type BoolRecord is {bool f}
type IntArrayRecord is {int[] f}
type T_Record<T> is {T f}

type S_Record<S> is {S f}

function f<T>({T f} rec) -> (T r)
ensures r == rec.f:
    //
    return rec.f

public export method test():
    //
    IntRecord ir = {f:1234}
    BoolRecord br = {f:true}
    IntArrayRecord iar = {f: [0,1,2]}
    //
    assert f(ir) == 1234
    assert f(br) == true
    assert f(iar) == [0,1,2]
    //
    T_Record<int> tri = {f:2345}
    T_Record<bool> trb = {f:false}
    T_Record<int[]> tria = {f:[4,5,6]}
    //
    assert f(tri) == 2345
    assert f(trb) == false
    assert f(tria) == [4,5,6]
    //
    S_Record<int> sri = {f:8910}
    S_Record<bool> srb = {f:true}
    S_Record<int[]> sria = {f:[8,9,10]}
    //
    assert f(sri) == 8910
    assert f(srb) == true
    assert f(sria) == [8,9,10]
    