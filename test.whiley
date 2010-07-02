define state as (string input, int pos) where pos >= 0 && pos <= |input|

define expr as (int num) | (int op, expr lhs, expr rhs) | (string err)

state parseWhiteSpace(state st):
    if(st.pos < |st.input| && isWhiteSpace(st.input[st.pos])):
        return parseWhiteSpace((input:st.input,pos:st.pos+1))
    else:
        return st

bool isWhiteSpace(char c):
    return c == ' ' || c == '\t' || c == '\n'    

(expr e, state st) parseTerm(state st):
    st = parseWhiteSpace(st)
    if st.pos < |st.input|:
        if isLetter(st.input[st.pos]):
            return parseIdentifier(st)
    return (e:(err:"expecting number or variable"),st:st)

(expr e, state st) parseIdentifier(state st):
    return (e:(err:"Got here"),st:st)

