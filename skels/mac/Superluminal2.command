#!/bin/sh
# Bundles aren't very terminal friendly.
#
# Sure there's the open command, but it's like double-clicking.
#   open -n ./AppName.app
# OSX 10.6 allowed args; still icky:
#   open -n ./AppName.app --args arg1 arg2 arg3
#
# This frontend allows terminal users to pass args, see printed
# messages, pipe, and redirect the way normal commandline apps should.
#
# GUI users who double-click this will get realtime feedback, but
# lack logging when java itself crashes, or the app abruptly exits.
# When logging's important, they can use the bundle.

# Chop off the filename from the full path used to run this executable.
maindir="${0%/*}";

# Run the script inside the bundle, relaying any args passed to this one.
cd "${maindir}";
"${maindir}/Superluminal2.app/Contents/MacOS/runme" "$@";
