import println from whiley.lang.System

define Days as [
    "Monday",
    "Tuesday",
    "Wednesday",    
    "Thursday",
    "Friday",
    "Saturday",
    "Sunday"
]

define item as int where 0 <= $ && $ < 7

item inc(item i):
    return (i + 1) % 7

void ::print(System.Console console, item day, int count):
    if count > 0:
        console.out.println(Days[day])
        print(console,inc(day),count-1)

public void ::main(System.Console console):
    print(console,0,15)









    
    
    