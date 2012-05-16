import println from whiley.lang.System

define Sum as ref { 
    [int] items, 
    int result 
}

void Sum::start():
    sum = 0
    for i in this->items:
        sum = sum + i
    this->result = sum    

int Sum::get():
    return this->result

Sum ::create([int] items):
    return new { 
        items: items, 
        result: 0 
    }

void ::main(System.Console sys):
    data = [1,3,5,7,3,198,1,4,6]
    sum = create(data)
    // start the ref asynchronously
    sum!start()
    // get the result synchronously
    r = sum.get()
    sys.out.println("SUM: " + Any.toString(r))
        

