package whiley.lang

define Error as { string msg }

public Error Error(string msg):
    return {msg: msg}

public define SyntaxError as { 
    string msg,  // message
    int start,   // start index
    int end      // last index
}

public SyntaxError SyntaxError(string msg, int start, int end):
    return {
        msg: msg,
        start: start,
        end: end
    }
