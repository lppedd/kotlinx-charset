lexer grammar UcmLexer;

options {
  caseInsensitive = true;
}

/* mode DEFAULT; */
Comment         : '#' ~[\n\r]*      -> skip;
AttrOpen        : '<'               -> skip, mode(ATTR);
Charmap         : 'CHARMAP'         -> skip, mode(CHARMAP);
Ws              : WS                -> skip;

mode ATTR;
AttrName        : ~[>]+;
AttrClose       : '>' [ \t]*        -> skip, mode(ATTR_VALUE);
ATTR_Ws         : WS                -> skip;

mode ATTR_VALUE;
AttrValue       : ~[\n\r]+          -> mode(DEFAULT_MODE);
ATTR_VALUE_Ws   : WS                -> skip;

mode CHARMAP;
UnicodeOpen     : '<'               -> skip;
Codepoint       : 'U' HEX;
UnicodeClose    : '>'               -> skip;
Byte            : '\\x' HEX;
Type            : '|' [0-9]+;
Plus            : '+'               -> skip;
EndCharmap      : 'END CHARMAP'     -> skip, mode(DEFAULT_MODE);
CHARMAP_Ws      : WS                -> skip;

/*********************************************************
/ FRAGMENTS
/********************************************************/
fragment WS
  : [ \t\r\n]+
  ;

fragment HEX
  : [a-z0-9][a-z0-9]+
  ;
