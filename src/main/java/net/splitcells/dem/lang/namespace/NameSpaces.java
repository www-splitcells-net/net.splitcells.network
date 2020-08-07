package net.splitcells.dem.lang.namespace;

import static net.splitcells.dem.lang.namespace.NameSpace.nameSpace;
import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class NameSpaces {
    public static final NameSpace FODS_OFFICE = nameSpace("office",
            "urn:oasis:names:tc:opendocument:xmlns:office:1.0");
    public static final NameSpace FODS_TABLE = nameSpace("table",
            "urn:oasis:names:tc:opendocument:xmlns:table:1.0");
    public static final NameSpace FODS_TEXT = nameSpace("text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0");
    public static final NameSpace TEXT = nameSpace("nt", "http://splitcells.net/text.xsd");
    public static final NameSpace STRING = nameSpace("str", "http://splitcells.net/string.xsd");
    public static final NameSpace NAME_SPACE = nameSpace("ns", "http://splitcells.net/namespace.xsd");
    public static final NameSpace DEN = nameSpace("d", "http://splitcells.net/den.xsd");
    public static final NameSpace SEW = nameSpace("s", "http://splitcells.net/sew.xsd");
    public static final String LINK = "link";
    public static final String URL = "url";
    public static final NameSpace NATURAL = nameSpace("n", "http://splitcells.net/natural.xsd");
    public static final String VAL = "val";
    public static final String NAME = "name";
    public static final NameSpace XLINK = nameSpace("xl", "http://www.w3.org/1999/xlink");
    public static final String HREF = "href";

    private NameSpaces() {
        throw constructorIllegal();
    }
}
