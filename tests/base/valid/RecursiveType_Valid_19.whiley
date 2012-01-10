

define Link as { [int] items, null|Link next }
	 
Link System::create(int n):
    start = null
    for i in 0..n:
        start = { items: [], next: start }
    return start
