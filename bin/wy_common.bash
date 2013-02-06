# This file is part of the Whiley Development Kit (WDK).
#
# The Whiley Development Kit is free software; you can redistribute 
# it and/or modify it under the terms of the GNU General Public 
# License as published by the Free Software Foundation; either 
# version 3 of the License, or (at your option) any later version.
#
# The Whiley Development Kit is distributed in the hope that it 
# will be useful, but WITHOUT ANY WARRANTY; without even the 
# implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
# PURPOSE.  See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public 
# License along with the Whiley Development Kit. If not, see 
# <http://www.gnu.org/licenses/>
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
case "$tmp" in
*\**)
    echo "wyjc.jar not found" >&2
    exit 2
    ;;
esac
WYRT_JAR=${tmp##* }
WHILEY_BOOTPATH="$WYRT_JAR"

