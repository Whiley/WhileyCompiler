#
# Copyright (c) 2011, David J. Pearce (djp@ecs.vuw.ac.nz)
# All rights reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
#    * Redistributions of source code must retain the above copyright
#      notice, this list of conditions and the following disclaimer.
#    * Redistributions in binary form must reproduce the above copyright
#      notice, this list of conditions and the following disclaimer in the
#      documentation and/or other materials provided with the distribution.
#    * Neither the name of the <organization> nor the
#      names of its contributors may be used to endorse or promote products
#      derived from this software without specific prior written permission.
#
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
# ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
# WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED. IN NO EVENT SHALL DAVID J. PEARCE BE LIABLE FOR ANY
# DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
# (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
# LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
# (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
# SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
#
# Copyright 2010, David James Pearce.
# modified 2012,	Art Protin <protin2art@gmail.com>

# check for running under cywin
cygwin=false
case "`uname`" in
  CYGWIN*) cygwin=true ;;
esac

######################
# CONSTRUCT CLASSPATH
######################

if $cygwin; then
    # under cygwin the classpath separator must be ";"
    LIBDIR=`cygpath -m "$LIBDIR"`
    PATHSEP=";"
else
    # under UNIX the classpath separator must be ":"
    PATHSEP=":"
fi

WHILEY_CLASSPATH=$CLASSPATH

# as there may more than one version of the jar files
# it is important to first use the * expansion, and then
# use the last one via the "##* " expansion.

for lib in $LIBS
do
    tmp=$(echo $LIBDIR/${lib}-v*.jar)
    JAR=${tmp##* }
    case "$JAR" in
    *\**)
        echo "Library '$lib' not found"
        exit 2
        ;;
    esac
    WHILEY_CLASSPATH="$JAR$PATHSEP$WHILEY_CLASSPATH"
done

######################
# CONSTRUCT BOOTPATH
######################

tmp=$(echo $LIBDIR/wyrt-v*.jar)
WYRT_JAR=${tmp##* }
WHILEY_BOOTPATH="$WYRT_JAR"

