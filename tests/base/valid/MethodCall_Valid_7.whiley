define Sum as process { 
    [int] items, 
    int result 
}

void Sum::start():
    sum = 0
    for i in items:
        sum = sum + i
    this.result = sum    

int Sum::get():
    return result

Sum ::create([int] items):
    return spawn { 
        items: items, 
        result: 0 
    }

void System::main([string] args):
    data = [1,3,5,7,3,198,1,4,6]
    sum = create(items)
    // start the process asynchronously
    sum!start()
    // get the result synchronously
    r = sum.get()
    out.println("SUM: " + str(r))
        

