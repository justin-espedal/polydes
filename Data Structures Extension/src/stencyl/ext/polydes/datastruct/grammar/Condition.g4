grammar Condition;

expression
    :   primary                                     #ExprPrimary
    |   expression '.' name=Identifier
        '(' (expression (',' expression)*)? ')'     #ExprInvocation
    |   expression '.' name=Identifier              #ExprField
    |   ('-') expression                            #ExprNegate
    |   ('!') expression                            #ExprNot
    |   expression '*'  expression                  #ExprMultiply
    |   expression '/'  expression                  #ExprDivide
    |   expression '%'  expression                  #ExprMod
    |   expression '+'  expression                  #ExprAdd
    |   expression '-'  expression                  #ExprSub
    |   expression '<=' expression                  #ExprLe
    |   expression '>=' expression                  #ExprGe
    |   expression '>'  expression                  #ExprGt
    |   expression '<'  expression                  #ExprLt
    |   expression '==' expression                  #ExprEqual
    |   expression '!=' expression                  #ExprNotEqual
    |   expression '&&' expression                  #ExprAnd
    |   expression '||' expression                  #ExprOr
    ;

primary
    :   '(' expression ')' #PrimExpr
    |   'this'             #PrimThis
    |   literal            #PrimLit
    |   name=Identifier    #PrimIdent
    ;

literal
    :   IntegerLiteral       #LiteralInt
    |   FloatingPointLiteral #LiteralFloat
    |   StringLiteral        #LiteralString
    |   BooleanLiteral       #LiteralBool
    |   NullLiteral          #LiteralNull
    ;
	
THIS : 'this';
	
IntegerLiteral
    :   '0'
    |   NonZeroDigit (Digits? | Underscores Digits)
    ;

fragment
Digits
    :   Digit (DigitOrUnderscore* Digit)?
    ;

fragment
Digit
    :   '0'
    |   NonZeroDigit
    ;

fragment
NonZeroDigit
    :   [1-9]
    ;

fragment
DigitOrUnderscore
    :   Digit
    |   '_'
    ;

fragment
Underscores
    :   '_'+
    ;

FloatingPointLiteral
    :   Digits '.' Digits?
    |   '.' Digits
    |   Digits
    |   Digits
    ;

BooleanLiteral
    :   'true'
    |   'false'
    ;

StringLiteral
    :   '"' StringCharacters? '"'
    ;
	
fragment
StringCharacters
    :   StringCharacter+
    ;
	
fragment
StringCharacter
    :   ~["\\]
    ;
	
NullLiteral     : 'null';

LPAREN          : '(';
RPAREN          : ')';
COMMA           : ',';
DOT             : '.';

GT              : '>';
LT              : '<';
BANG            : '!';
EQUAL           : '==';
LE              : '<=';
GE              : '>=';
NOTEQUAL        : '!=';
AND             : '&&';
OR              : '||';
ADD             : '+';
SUB             : '-';
MUL             : '*';
DIV             : '/';
MOD             : '%';

Identifier
    :   JavaLetter JavaLetterOrDigit*
    ;

fragment
JavaLetter
    :   [a-zA-Z$_] // these are the "java letters" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierStart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

fragment
JavaLetterOrDigit
    :   [a-zA-Z0-9$_] // these are the "java letters or digits" below 0xFF
    |   // covers all characters above 0xFF which are not a surrogate
        ~[\u0000-\u00FF\uD800-\uDBFF]
        {Character.isJavaIdentifierPart(_input.LA(-1))}?
    |   // covers UTF-16 surrogate pairs encodings for U+10000 to U+10FFFF
        [\uD800-\uDBFF] [\uDC00-\uDFFF]
        {Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)))}?
    ;

WS  :  [ \t\r\n\u000C]+ -> skip
    ;