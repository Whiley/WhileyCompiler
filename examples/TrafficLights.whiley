import println from whiley.lang.System

// the British interpretation of traffic lights!
define TrafficLights as {
    bool red,
    bool amber,
    bool green    
} where (!red && !amber && green) ||
        (!red && amber && !green) || // ignoring flashing
        (red && !amber && !green) ||
        (red && amber && !green)

TrafficLights TrafficLights():
    return {
        red: true,
        amber: false,
        green: false
    }

TrafficLights change(TrafficLights ls):
    if ls.green:
        // -> !red && !amber && green
        return { red: false, amber: true, green: false }
    else if ls.red:
        // -> red && ~amber && !green
        if ls.amber:
            // -> red && amber && !green
            return { red: false, amber: false, green: true }
        else:
            return { red: true, amber: true, green: false }
    else:
        // -> !red && amber && !green
        return { red: true, amber: false, green: false }

string toString(TrafficLights ls):
    if ls.red:
        r = "RED "
    else:
        r = "    "
    if ls.amber:
        r = r + "AMBER "
    else:
        r = r + "       "
    if ls.green:
        r = r + "GREEN "
    else:
        r = r + "      "
    return r
    
public void ::main(System.Console console):
    lights = TrafficLights()
    console.out.println(toString(lights))
    lights = change(lights)
    console.out.println(toString(lights))
    lights = change(lights)
    console.out.println(toString(lights))
    lights = change(lights)
    console.out.println(toString(lights))
    lights = change(lights)
    console.out.println(toString(lights))
