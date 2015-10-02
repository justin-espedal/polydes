// Generated from Condition.g4 by ANTLR 4.2.2
package com.polydes.datastruct.grammar;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ConditionLexer extends Lexer {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		THIS=1, IntegerLiteral=2, FloatingPointLiteral=3, BooleanLiteral=4, StringLiteral=5, 
		NullLiteral=6, LPAREN=7, RPAREN=8, COMMA=9, DOT=10, GT=11, LT=12, BANG=13, 
		EQUAL=14, LE=15, GE=16, NOTEQUAL=17, AND=18, OR=19, ADD=20, SUB=21, MUL=22, 
		DIV=23, MOD=24, Identifier=25, WS=26;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"<INVALID>",
		"'this'", "IntegerLiteral", "FloatingPointLiteral", "BooleanLiteral", 
		"StringLiteral", "'null'", "'('", "')'", "','", "'.'", "'>'", "'<'", "'!'", 
		"'=='", "'<='", "'>='", "'!='", "'&&'", "'||'", "'+'", "'-'", "'*'", "'/'", 
		"'%'", "Identifier", "WS"
	};
	public static final String[] ruleNames = {
		"THIS", "IntegerLiteral", "Digits", "Digit", "NonZeroDigit", "DigitOrUnderscore", 
		"Underscores", "FloatingPointLiteral", "BooleanLiteral", "StringLiteral", 
		"StringCharacters", "StringCharacter", "NullLiteral", "LPAREN", "RPAREN", 
		"COMMA", "DOT", "GT", "LT", "BANG", "EQUAL", "LE", "GE", "NOTEQUAL", "AND", 
		"OR", "ADD", "SUB", "MUL", "DIV", "MOD", "Identifier", "JavaLetter", "JavaLetterOrDigit", 
		"WS"
	};


	public ConditionLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Condition.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 32: return JavaLetter_sempred((RuleContext)_localctx, predIndex);

		case 33: return JavaLetterOrDigit_sempred((RuleContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean JavaLetterOrDigit_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 2: return Character.isJavaIdentifierPart(_input.LA(-1));

		case 3: return Character.isJavaIdentifierPart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}
	private boolean JavaLetter_sempred(RuleContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return Character.isJavaIdentifierStart(_input.LA(-1));

		case 1: return Character.isJavaIdentifierStart(Character.toCodePoint((char)_input.LA(-2), (char)_input.LA(-1)));
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\34\u00e3\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\5\3R\n\3\3"+
		"\3\3\3\3\3\5\3W\n\3\5\3Y\n\3\3\4\3\4\7\4]\n\4\f\4\16\4`\13\4\3\4\5\4c"+
		"\n\4\3\5\3\5\5\5g\n\5\3\6\3\6\3\7\3\7\5\7m\n\7\3\b\6\bp\n\b\r\b\16\bq"+
		"\3\t\3\t\3\t\5\tw\n\t\3\t\3\t\3\t\3\t\5\t}\n\t\3\n\3\n\3\n\3\n\3\n\3\n"+
		"\3\n\3\n\3\n\5\n\u0088\n\n\3\13\3\13\5\13\u008c\n\13\3\13\3\13\3\f\6\f"+
		"\u0091\n\f\r\f\16\f\u0092\3\r\3\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3"+
		"\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\26\3\26\3"+
		"\26\3\27\3\27\3\27\3\30\3\30\3\30\3\31\3\31\3\31\3\32\3\32\3\32\3\33\3"+
		"\33\3\33\3\34\3\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\3!\7!\u00c8"+
		"\n!\f!\16!\u00cb\13!\3\"\3\"\3\"\3\"\3\"\3\"\5\"\u00d3\n\"\3#\3#\3#\3"+
		"#\3#\3#\5#\u00db\n#\3$\6$\u00de\n$\r$\16$\u00df\3$\3$\2\2%\3\3\5\4\7\2"+
		"\t\2\13\2\r\2\17\2\21\5\23\6\25\7\27\2\31\2\33\b\35\t\37\n!\13#\f%\r\'"+
		"\16)\17+\20-\21/\22\61\23\63\24\65\25\67\269\27;\30=\31?\32A\33C\2E\2"+
		"G\34\3\2\n\3\2\63;\4\2$$^^\6\2&&C\\aac|\4\2\2\u0101\ud802\udc01\3\2\ud802"+
		"\udc01\3\2\udc02\ue001\7\2&&\62;C\\aac|\5\2\13\f\16\17\"\"\u00ee\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\33\3\2\2\2"+
		"\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2"+
		"\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2"+
		"\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3\2\2"+
		"\2\2A\3\2\2\2\2G\3\2\2\2\3I\3\2\2\2\5X\3\2\2\2\7Z\3\2\2\2\tf\3\2\2\2\13"+
		"h\3\2\2\2\rl\3\2\2\2\17o\3\2\2\2\21|\3\2\2\2\23\u0087\3\2\2\2\25\u0089"+
		"\3\2\2\2\27\u0090\3\2\2\2\31\u0094\3\2\2\2\33\u0096\3\2\2\2\35\u009b\3"+
		"\2\2\2\37\u009d\3\2\2\2!\u009f\3\2\2\2#\u00a1\3\2\2\2%\u00a3\3\2\2\2\'"+
		"\u00a5\3\2\2\2)\u00a7\3\2\2\2+\u00a9\3\2\2\2-\u00ac\3\2\2\2/\u00af\3\2"+
		"\2\2\61\u00b2\3\2\2\2\63\u00b5\3\2\2\2\65\u00b8\3\2\2\2\67\u00bb\3\2\2"+
		"\29\u00bd\3\2\2\2;\u00bf\3\2\2\2=\u00c1\3\2\2\2?\u00c3\3\2\2\2A\u00c5"+
		"\3\2\2\2C\u00d2\3\2\2\2E\u00da\3\2\2\2G\u00dd\3\2\2\2IJ\7v\2\2JK\7j\2"+
		"\2KL\7k\2\2LM\7u\2\2M\4\3\2\2\2NY\7\62\2\2OV\5\13\6\2PR\5\7\4\2QP\3\2"+
		"\2\2QR\3\2\2\2RW\3\2\2\2ST\5\17\b\2TU\5\7\4\2UW\3\2\2\2VQ\3\2\2\2VS\3"+
		"\2\2\2WY\3\2\2\2XN\3\2\2\2XO\3\2\2\2Y\6\3\2\2\2Zb\5\t\5\2[]\5\r\7\2\\"+
		"[\3\2\2\2]`\3\2\2\2^\\\3\2\2\2^_\3\2\2\2_a\3\2\2\2`^\3\2\2\2ac\5\t\5\2"+
		"b^\3\2\2\2bc\3\2\2\2c\b\3\2\2\2dg\7\62\2\2eg\5\13\6\2fd\3\2\2\2fe\3\2"+
		"\2\2g\n\3\2\2\2hi\t\2\2\2i\f\3\2\2\2jm\5\t\5\2km\7a\2\2lj\3\2\2\2lk\3"+
		"\2\2\2m\16\3\2\2\2np\7a\2\2on\3\2\2\2pq\3\2\2\2qo\3\2\2\2qr\3\2\2\2r\20"+
		"\3\2\2\2st\5\7\4\2tv\7\60\2\2uw\5\7\4\2vu\3\2\2\2vw\3\2\2\2w}\3\2\2\2"+
		"xy\7\60\2\2y}\5\7\4\2z}\5\7\4\2{}\5\7\4\2|s\3\2\2\2|x\3\2\2\2|z\3\2\2"+
		"\2|{\3\2\2\2}\22\3\2\2\2~\177\7v\2\2\177\u0080\7t\2\2\u0080\u0081\7w\2"+
		"\2\u0081\u0088\7g\2\2\u0082\u0083\7h\2\2\u0083\u0084\7c\2\2\u0084\u0085"+
		"\7n\2\2\u0085\u0086\7u\2\2\u0086\u0088\7g\2\2\u0087~\3\2\2\2\u0087\u0082"+
		"\3\2\2\2\u0088\24\3\2\2\2\u0089\u008b\7$\2\2\u008a\u008c\5\27\f\2\u008b"+
		"\u008a\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008d\3\2\2\2\u008d\u008e\7$"+
		"\2\2\u008e\26\3\2\2\2\u008f\u0091\5\31\r\2\u0090\u008f\3\2\2\2\u0091\u0092"+
		"\3\2\2\2\u0092\u0090\3\2\2\2\u0092\u0093\3\2\2\2\u0093\30\3\2\2\2\u0094"+
		"\u0095\n\3\2\2\u0095\32\3\2\2\2\u0096\u0097\7p\2\2\u0097\u0098\7w\2\2"+
		"\u0098\u0099\7n\2\2\u0099\u009a\7n\2\2\u009a\34\3\2\2\2\u009b\u009c\7"+
		"*\2\2\u009c\36\3\2\2\2\u009d\u009e\7+\2\2\u009e \3\2\2\2\u009f\u00a0\7"+
		".\2\2\u00a0\"\3\2\2\2\u00a1\u00a2\7\60\2\2\u00a2$\3\2\2\2\u00a3\u00a4"+
		"\7@\2\2\u00a4&\3\2\2\2\u00a5\u00a6\7>\2\2\u00a6(\3\2\2\2\u00a7\u00a8\7"+
		"#\2\2\u00a8*\3\2\2\2\u00a9\u00aa\7?\2\2\u00aa\u00ab\7?\2\2\u00ab,\3\2"+
		"\2\2\u00ac\u00ad\7>\2\2\u00ad\u00ae\7?\2\2\u00ae.\3\2\2\2\u00af\u00b0"+
		"\7@\2\2\u00b0\u00b1\7?\2\2\u00b1\60\3\2\2\2\u00b2\u00b3\7#\2\2\u00b3\u00b4"+
		"\7?\2\2\u00b4\62\3\2\2\2\u00b5\u00b6\7(\2\2\u00b6\u00b7\7(\2\2\u00b7\64"+
		"\3\2\2\2\u00b8\u00b9\7~\2\2\u00b9\u00ba\7~\2\2\u00ba\66\3\2\2\2\u00bb"+
		"\u00bc\7-\2\2\u00bc8\3\2\2\2\u00bd\u00be\7/\2\2\u00be:\3\2\2\2\u00bf\u00c0"+
		"\7,\2\2\u00c0<\3\2\2\2\u00c1\u00c2\7\61\2\2\u00c2>\3\2\2\2\u00c3\u00c4"+
		"\7\'\2\2\u00c4@\3\2\2\2\u00c5\u00c9\5C\"\2\u00c6\u00c8\5E#\2\u00c7\u00c6"+
		"\3\2\2\2\u00c8\u00cb\3\2\2\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca"+
		"B\3\2\2\2\u00cb\u00c9\3\2\2\2\u00cc\u00d3\t\4\2\2\u00cd\u00ce\n\5\2\2"+
		"\u00ce\u00d3\6\"\2\2\u00cf\u00d0\t\6\2\2\u00d0\u00d1\t\7\2\2\u00d1\u00d3"+
		"\6\"\3\2\u00d2\u00cc\3\2\2\2\u00d2\u00cd\3\2\2\2\u00d2\u00cf\3\2\2\2\u00d3"+
		"D\3\2\2\2\u00d4\u00db\t\b\2\2\u00d5\u00d6\n\5\2\2\u00d6\u00db\6#\4\2\u00d7"+
		"\u00d8\t\6\2\2\u00d8\u00d9\t\7\2\2\u00d9\u00db\6#\5\2\u00da\u00d4\3\2"+
		"\2\2\u00da\u00d5\3\2\2\2\u00da\u00d7\3\2\2\2\u00dbF\3\2\2\2\u00dc\u00de"+
		"\t\t\2\2\u00dd\u00dc\3\2\2\2\u00de\u00df\3\2\2\2\u00df\u00dd\3\2\2\2\u00df"+
		"\u00e0\3\2\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e2\b$\2\2\u00e2H\3\2\2\2\24"+
		"\2QVX^bflqv|\u0087\u008b\u0092\u00c9\u00d2\u00da\u00df\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}