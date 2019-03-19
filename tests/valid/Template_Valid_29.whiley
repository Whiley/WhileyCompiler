// test case from std::collection

public type Vector<T> is {
    T[] items,
    int length
} where length <= |items|

function add<T>(Vector<T> vec, T item) -> Vector<T>:
    //
    if vec.length == |vec.items|:
        // vec is full so must resize
        int nlen = (vec.length*2)+1
        // double size of internal array
        T[] nitems = [item; nlen]
        int i = 0
        // copy items
        while i < vec.length:
           nitems[i] = vec.items[i]
           i = i + 1
        //
        vec.items = nitems
    else:
        vec.items[vec.length] = item
    //
    vec.length = vec.length + 1        
    //
    return vec

public export method test():
    // integers first
    Vector<int> vi_1 = { items: [1,2,3], length: 2 }    
    Vector<int> vi_2 = add(vi_1,0)
    Vector<int> vi_3 = add(vi_2,4)    
    assert vi_1 == { items: [1,2,3], length: 2 } 
    assert vi_2 == { items: [1,2,0], length: 3 } 
    assert vi_3 == { items: [1,2,0,4,4,4,4], length: 4 } 
    // booleans second
    Vector<bool> vb_1 = { items: [true,false,true,false], length: 3 }
    Vector<bool> vb_2 = add(vb_1,true)
    Vector<bool> vb_3 = add(vb_2,false)
    assert vb_1 == { items: [true,false,true,false], length: 3 } 
    assert vb_2 == { items: [true,false,true,true], length: 4 }
    assert vb_3 == { items: [true,false,true,true,false,false,false,false,false], length: 5 }
