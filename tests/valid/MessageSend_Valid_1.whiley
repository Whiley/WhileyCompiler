

type MyObject is &{int field}

method f(MyObject _this, int x) :
    assume x == 1

public export method test() :
    MyObject m = new {field: 1}
    f(m,1)
    
