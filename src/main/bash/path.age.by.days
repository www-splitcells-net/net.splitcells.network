#!/usr/bin/env bash
# SOURCE https://unix.stackexchange.com/questions/102691/get-age-of-given-file/102698#102698
# According to https://meta.stackexchange.com/questions/271080/the-mit-license-clarity-on-using-code-on-stack-overflow-and-stack-exchange
# and the fact that the source was created on Nov 27 '13 at 0:12
# the following code seems to be licensed under MIT.

filename=$1
echo $((($(date +%s) - $(date +%s -r "$filename")) / 86400))
