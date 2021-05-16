grammar Java_11_parser;
/* source_unit is the root rule. */
@header {
    package net.splitcells.dem.source.code.antlr;
}
call_arguments
    : '()'
    | '(' Whitespace? reference Whitespace? call_arguments_next* Whitespace? ')'
    ;
call_arguments_next
    : ',' Whitespace reference
    ;
class_definition
    : javadoc? Whitespace? 'public'? Whitespace? 'final'? Whitespace? 'class'? Whitespace? Name
        Whitespace? '{' Whitespace? class_member* Whitespace? '}'
    ;
class_member
    : class_member_method_definition
    | class_member_value_declaration
    ;
class_member_method_definition
    : javadoc? Whitespace? modifier_visibility? Whitespace? 'static'? Whitespace? type_declaration Whitespace?
        Name Whitespace? call_arguments Whitespace? '{' Whitespace? statement* Whitespace? '}'
    ;
class_member_value_declaration
    : javadoc? Whitespace? 'private'? Whitespace? 'static'? Whitespace? 'final'? Whitespace?
        type_declaration? Whitespace? Name Whitespace? '=' Whitespace? statement?
    ;
expression
    : 'new' Whitespace? type_declaration call_arguments
    | Name Whitespace? call_arguments
    | variable_declaration
    ;
import_declaration
    : import_static_declaration
    | import_type_declaration
    ;
import_static_declaration
    : 'import' Whitespace 'static' Whitespace type_path ';' Whitespace*
    ;
import_type_declaration
    : 'import' Whitespace type_path ';' Whitespace*
    ;
javadoc
    : '/**' javadoc_content* '*/' Whitespace*
    ;
javadoc_content
    : Javadoc_content_character
    ;
fragment Javadoc_content_character
    : [\n\r\t @*{}/a-zA-Z]
    | Whitespace
    ;
modifier_visibility
    : 'public'
    | 'private'
    ;
Name
    : [a-zA-Z0-9_] [a-zA-Z0-9_]*
    ;
package_declaration
    : 'package' Whitespace package_name ';' Whitespace*
    ;
package_name
    : Name
    | package_name '.' Name
    ;
reference
    : expression
    | Name
    | Name Whitespace? '->' Whitespace? reference
    | Name Whitespace? '->' Whitespace? '{' Whitespace? statement* Whitespace? '}'
    ;
statement
    : 'return'? Whitespace expression ';'
    ;
source_unit
    : package_declaration import_declaration* Whitespace? class_definition EOF
    ;
type_declaration
    : Name type_argument?
    ;
type_argument
    : '<' Whitespace? type_argument_content? Whitespace? '>'
    ;
type_argument_content
    : type_argument Whitespace? type_argument_content_next?
    | Name Whitespace? type_argument_content_next?
    ;
type_argument_content_next
    : ',' Whitespace? type_argument Whitespace? type_argument_content_next?
    | ',' Whitespace? Name Whitespace? type_argument_content_next?
    ;
type_path
    : Name
    | type_path '.' Name
    ;
variable_declaration
    : type_declaration Whitespace Name
    ;
Whitespace
    : [ \t\n\r]+
    ;