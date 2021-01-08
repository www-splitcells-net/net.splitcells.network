package net.splitcells.gel;

public enum Language {
    ARGUMENTĀCIJA("argumentācija")
    ,PIEŠĶIRT("piešķirt")
    ,CEĻA_PIEEJAS_SIMBOLS(".")
    ,PRASĪBA("prasība")
    ,PIEDĀVĀJUMS("piedāvājums")
    , NOŅEMT("noņemt")
    , PĀRBAUDE("pārbaude")
    , RINDA("rinda")
    , PIEDĀVĀJUMI("piedāvājumi")
    , PRASĪBAS("parsības")
    , VĒSTURE("vēsture")
    , PIEŠĶIRŠANA("piešķiršana")
    , PIEŠĶIRŠANAS("piešķiršanas")
    , REFKLEKSIJAS_DATI("refkleksijasDati")
    , DAIT("dati")
    , VERTĪBA("vertība")
    , ATSLĒGA("atslēga")
    , TIPS("tips")
    , PARSĪBA("parsība")
    , SOLU_TIPS("soluTips")
    , RADĪJUMS("radījums")
    , NOTIKUMS("notikums")
    , NOVĒRTĒJUMS("novērtējums")
    , PROBLĒMA("problēma")
    , VĀRDS("vāŗds")
    ;
    private final String apraksts;

    Language(String apraksts) {
        this.apraksts = apraksts;
    }

    public String apraksts() {
        return apraksts;
    }
}
