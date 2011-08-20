import whiley.lang.*:*

define Sum as process { 
    [int] items, 
    int result 
}

void Sum::start():
    sum = 0
    for i in this.items:
        sum = sum + i
    this.result = sum    

int Sum::get():
    return this.result

Sum ::create([int] items):
    return spawn { 
        items: items, 
        result: 0 
    }

int seqSum([int] items):
    r = 0
    for i in items:
        r = r + i
    return r

int ::parSum([int] items):
    sum = create(items)
    // start the process asynchronously
    sum!start()
    // get the result synchronously
    return sum.get()

int ::sum(int::([int]) m, [int] data):
    return m(data)

void System::main([string] args):
    data = [1,3,5,7,3,198,1,4,6]
    s1 = sum(&parSum,data)
    this.out.println("SUM: " + str(s1))
    s2 = sum(&seqSum,data)
    this.out.println("SUM: " + str(s2))
        

