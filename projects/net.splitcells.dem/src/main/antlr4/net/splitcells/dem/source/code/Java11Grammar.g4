grammar Java11Grammar;

@header {
package net.splitcells.dem.source.code.antlr;
}

access
    : '.'
    ;
Name: [a-zA-Z_][a-zA-Z0-9_]*;
source_unit
	: Name
	;
WS
	: [ \t\r\n]+ -> skip
	;