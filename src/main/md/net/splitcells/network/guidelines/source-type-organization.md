# Organization of Source Types
The number of formats/protocols should be minimized.

Avoid creating new own protocols and formats,
if possible,
because these create maintenance burdens.
Prefer create new pseudo protocols by using a restricted version of
existing and established protocols instead.
Restricted version of established protocols often increase portability.
The own article schema for the website based on XML is deprecated.

Minimize the number of protocols inside one files.
In the best case only one format is present per file.
If multiple formats are needed for one file,
try creating multiple files with the
same file name suffix and one format for each such file.