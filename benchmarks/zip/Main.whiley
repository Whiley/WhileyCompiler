import whiley.io.*

void System::main([string] args):
    file = this->openFile(args[0])
    contents = file->read()
    zf = zipFile(contents)
    if zf ~= ZipError:
        print "error: " + zf.msg
    else:
        print "found: " + str(|zf.entries|)
