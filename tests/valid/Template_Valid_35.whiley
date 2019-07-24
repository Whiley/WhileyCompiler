type List<T> is null | { List<T> next, T data }

function isListInt(List<int>|List<bool> l) -> (bool r):
    if l is List<int>:
        return true
    else:
        return false

public export method test():
    List<int> li1 = null
    List<int> li2 = { next: li1, data: 1 }
    List<int> li3 = { next: li2, data: 2 }
    List<bool> lb1 = null    
    List<bool> lb2 = { next: lb1, data: false }
    List<bool> lb3 = { next: lb2, data: true }
    // test negative cases
    assume !isListInt(lb1)
    assume !isListInt(lb2)
    assume !isListInt(lb3)
    // test positive cases
    assume isListInt(li1)
    assume isListInt(li2)
    assume isListInt(li3)
