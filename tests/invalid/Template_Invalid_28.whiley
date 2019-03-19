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
        vec[vec.length] = item
    //
    vec.length = vec.length + 1        
    //
    return vec
