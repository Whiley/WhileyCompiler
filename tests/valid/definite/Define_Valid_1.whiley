define (int x,int y) as point
define [int] as listint
define {int} as setint

void System::main([string] args):
     point p
     setint si
     listint li
     int x
     
     si = {1,2,3}
     li = [1,2,3]
     
     p = (x:1,y:2)
     x = p.x
     
     print str(x)
     print str(|si|)
     print str(li[0])
