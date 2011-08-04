import whiley.io.*

// ========================================================
// Benchmark
// ========================================================

define MAX_BUFFER_SIZE as 5

define Link as process { [int] items, null|Link next }
	 
int Link::get():
    item = items[0]
    this.items = items[1..]
    return item
	 
void Link::push(int item):
    tmp = this.next
    if tmp == null || |items| < MAX_BUFFER_SIZE:        
        this.items = items + [item]
    else:
        tmp!push(item)

bool Link::isEmpty():
    return |items| == 0

void Link::flush():
    // use of tmp here is less than ideal ...
    tmp = this.next
    if tmp != null:
        for d in items:
            tmp!push(d)
        tmp.flush()    

(Link,Link) System::create(int n):
    end = spawn { items: [], next: null }
    start = end
    for i in 0..n:
        start = spawn { items: [], next: start }
    return start,end
    
void System::main([string] args):
    // create a ring of size 10
    start,end = this.create(10)
    for i in 0..100:
        start!push(i)
    start.flush()
    while !end.isEmpty():
        out.println(str(end.get()))    
