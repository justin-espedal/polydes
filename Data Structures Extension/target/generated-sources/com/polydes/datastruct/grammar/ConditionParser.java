// Generated from Condition.g4 by ANTLR 4.2.2
package com.polydes.datastruct.grammar;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class ConditionParser extends Parser {
	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		THIS=1, IntegerLiteral=2, FloatingPointLiteral=3, BooleanLiteral=4, StringLiteral=5, 
		NullLiteral=6, LPAREN=7, RPAREN=8, COMMA=9, DOT=10, GT=11, LT=12, BANG=13, 
		EQUAL=14, LE=15, GE=16, NOTEQUAL=17, AND=18, OR=19, ADD=20, SUB=21, MUL=22, 
		DIV=23, MOD=24, Identifier=25, WS=26;
	public static final String[] tokenNames = {
		"<INVALID>", "'this'", "IntegerLiteral", "FloatingPointLiteral", "BooleanLiteral", 
		"StringLiteral", "'null'", "'('", "')'", "','", "'.'", "'>'", "'<'", "'!'", 
		"'=='", "'<='", "'>='", "'!='", "'&&'", "'||'", "'+'", "'-'", "'*'", "'/'", 
		"'%'", "Identifier", "WS"
	};
	public static final int
		RULE_expression = 0, RULE_primary = 1, RULE_literal = 2;
	public static final String[] ruleNames = {
		"expression", "primary", "literal"
	};

	@Override
	public String getGrammarFileName() { return "Condition.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public ConditionParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class ExprLeContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprLeContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprLe(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprLe(this);
		}
	}
	public static class ExprGeContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprGeContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprGe(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprGe(this);
		}
	}
	public static class ExprPrimaryContext extends ExpressionContext {
		public PrimaryContext primary() {
			return getRuleContext(PrimaryContext.class,0);
		}
		public ExprPrimaryContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprPrimary(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprPrimary(this);
		}
	}
	public static class ExprModContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprModContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprMod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprMod(this);
		}
	}
	public static class ExprNotContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExprNotContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprNot(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprNot(this);
		}
	}
	public static class ExprMultiplyContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprMultiplyContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprMultiply(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprMultiply(this);
		}
	}
	public static class ExprSubContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprSubContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprSub(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprSub(this);
		}
	}
	public static class ExprAddContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprAddContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprAdd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprAdd(this);
		}
	}
	public static class ExprEqualContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprEqualContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprEqual(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprEqual(this);
		}
	}
	public static class ExprInvocationContext extends ExpressionContext {
		public Token name;
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public TerminalNode Identifier() { return getToken(ConditionParser.Identifier, 0); }
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprInvocationContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprInvocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprInvocation(this);
		}
	}
	public static class ExprDivideContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprDivideContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprDivide(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprDivide(this);
		}
	}
	public static class ExprLtContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprLtContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprLt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprLt(this);
		}
	}
	public static class ExprNegateContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExprNegateContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprNegate(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprNegate(this);
		}
	}
	public static class ExprGtContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprGtContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprGt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprGt(this);
		}
	}
	public static class ExprFieldContext extends ExpressionContext {
		public Token name;
		public TerminalNode Identifier() { return getToken(ConditionParser.Identifier, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExprFieldContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprField(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprField(this);
		}
	}
	public static class ExprOrContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprOrContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprOr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprOr(this);
		}
	}
	public static class ExprAndContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprAndContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprAnd(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprAnd(this);
		}
	}
	public static class ExprNotEqualContext extends ExpressionContext {
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExprNotEqualContext(ExpressionContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterExprNotEqual(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitExprNotEqual(this);
		}
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 0;
		enterRecursionRule(_localctx, 0, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(12);
			switch (_input.LA(1)) {
			case SUB:
				{
				_localctx = new ExprNegateContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				{
				setState(7); match(SUB);
				}
				setState(8); expression(15);
				}
				break;
			case BANG:
				{
				_localctx = new ExprNotContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				{
				setState(9); match(BANG);
				}
				setState(10); expression(14);
				}
				break;
			case THIS:
			case IntegerLiteral:
			case FloatingPointLiteral:
			case BooleanLiteral:
			case StringLiteral:
			case NullLiteral:
			case LPAREN:
			case Identifier:
				{
				_localctx = new ExprPrimaryContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(11); primary();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(73);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					setState(71);
					switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
					case 1:
						{
						_localctx = new ExprMultiplyContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(14);
						if (!(precpred(_ctx, 13))) throw new FailedPredicateException(this, "precpred(_ctx, 13)");
						setState(15); match(MUL);
						setState(16); expression(14);
						}
						break;

					case 2:
						{
						_localctx = new ExprDivideContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(17);
						if (!(precpred(_ctx, 12))) throw new FailedPredicateException(this, "precpred(_ctx, 12)");
						setState(18); match(DIV);
						setState(19); expression(13);
						}
						break;

					case 3:
						{
						_localctx = new ExprModContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(20);
						if (!(precpred(_ctx, 11))) throw new FailedPredicateException(this, "precpred(_ctx, 11)");
						setState(21); match(MOD);
						setState(22); expression(12);
						}
						break;

					case 4:
						{
						_localctx = new ExprAddContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(23);
						if (!(precpred(_ctx, 10))) throw new FailedPredicateException(this, "precpred(_ctx, 10)");
						setState(24); match(ADD);
						setState(25); expression(11);
						}
						break;

					case 5:
						{
						_localctx = new ExprSubContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(26);
						if (!(precpred(_ctx, 9))) throw new FailedPredicateException(this, "precpred(_ctx, 9)");
						setState(27); match(SUB);
						setState(28); expression(10);
						}
						break;

					case 6:
						{
						_localctx = new ExprLeContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(29);
						if (!(precpred(_ctx, 8))) throw new FailedPredicateException(this, "precpred(_ctx, 8)");
						setState(30); match(LE);
						setState(31); expression(9);
						}
						break;

					case 7:
						{
						_localctx = new ExprGeContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(32);
						if (!(precpred(_ctx, 7))) throw new FailedPredicateException(this, "precpred(_ctx, 7)");
						setState(33); match(GE);
						setState(34); expression(8);
						}
						break;

					case 8:
						{
						_localctx = new ExprGtContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(35);
						if (!(precpred(_ctx, 6))) throw new FailedPredicateException(this, "precpred(_ctx, 6)");
						setState(36); match(GT);
						setState(37); expression(7);
						}
						break;

					case 9:
						{
						_localctx = new ExprLtContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(38);
						if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
						setState(39); match(LT);
						setState(40); expression(6);
						}
						break;

					case 10:
						{
						_localctx = new ExprEqualContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(41);
						if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
						setState(42); match(EQUAL);
						setState(43); expression(5);
						}
						break;

					case 11:
						{
						_localctx = new ExprNotEqualContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(44);
						if (!(precpred(_ctx, 3))) throw new FailedPredicateException(this, "precpred(_ctx, 3)");
						setState(45); match(NOTEQUAL);
						setState(46); expression(4);
						}
						break;

					case 12:
						{
						_localctx = new ExprAndContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(47);
						if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
						setState(48); match(AND);
						setState(49); expression(3);
						}
						break;

					case 13:
						{
						_localctx = new ExprOrContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(50);
						if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
						setState(51); match(OR);
						setState(52); expression(2);
						}
						break;

					case 14:
						{
						_localctx = new ExprInvocationContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(53);
						if (!(precpred(_ctx, 17))) throw new FailedPredicateException(this, "precpred(_ctx, 17)");
						setState(54); match(DOT);
						setState(55); ((ExprInvocationContext)_localctx).name = match(Identifier);
						setState(56); match(LPAREN);
						setState(65);
						_la = _input.LA(1);
						if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << THIS) | (1L << IntegerLiteral) | (1L << FloatingPointLiteral) | (1L << BooleanLiteral) | (1L << StringLiteral) | (1L << NullLiteral) | (1L << LPAREN) | (1L << BANG) | (1L << SUB) | (1L << Identifier))) != 0)) {
							{
							setState(57); expression(0);
							setState(62);
							_errHandler.sync(this);
							_la = _input.LA(1);
							while (_la==COMMA) {
								{
								{
								setState(58); match(COMMA);
								setState(59); expression(0);
								}
								}
								setState(64);
								_errHandler.sync(this);
								_la = _input.LA(1);
							}
							}
						}

						setState(67); match(RPAREN);
						}
						break;

					case 15:
						{
						_localctx = new ExprFieldContext(new ExpressionContext(_parentctx, _parentState));
						pushNewRecursionContext(_localctx, _startState, RULE_expression);
						setState(68);
						if (!(precpred(_ctx, 16))) throw new FailedPredicateException(this, "precpred(_ctx, 16)");
						setState(69); match(DOT);
						setState(70); ((ExprFieldContext)_localctx).name = match(Identifier);
						}
						break;
					}
					} 
				}
				setState(75);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class PrimaryContext extends ParserRuleContext {
		public PrimaryContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_primary; }
	 
		public PrimaryContext() { }
		public void copyFrom(PrimaryContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class PrimThisContext extends PrimaryContext {
		public PrimThisContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterPrimThis(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitPrimThis(this);
		}
	}
	public static class PrimLitContext extends PrimaryContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public PrimLitContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterPrimLit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitPrimLit(this);
		}
	}
	public static class PrimIdentContext extends PrimaryContext {
		public Token name;
		public TerminalNode Identifier() { return getToken(ConditionParser.Identifier, 0); }
		public PrimIdentContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterPrimIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitPrimIdent(this);
		}
	}
	public static class PrimExprContext extends PrimaryContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public PrimExprContext(PrimaryContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterPrimExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitPrimExpr(this);
		}
	}

	public final PrimaryContext primary() throws RecognitionException {
		PrimaryContext _localctx = new PrimaryContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_primary);
		try {
			setState(83);
			switch (_input.LA(1)) {
			case LPAREN:
				_localctx = new PrimExprContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(76); match(LPAREN);
				setState(77); expression(0);
				setState(78); match(RPAREN);
				}
				break;
			case THIS:
				_localctx = new PrimThisContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(80); match(THIS);
				}
				break;
			case IntegerLiteral:
			case FloatingPointLiteral:
			case BooleanLiteral:
			case StringLiteral:
			case NullLiteral:
				_localctx = new PrimLitContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(81); literal();
				}
				break;
			case Identifier:
				_localctx = new PrimIdentContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(82); ((PrimIdentContext)_localctx).name = match(Identifier);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	 
		public LiteralContext() { }
		public void copyFrom(LiteralContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class LiteralBoolContext extends LiteralContext {
		public TerminalNode BooleanLiteral() { return getToken(ConditionParser.BooleanLiteral, 0); }
		public LiteralBoolContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterLiteralBool(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitLiteralBool(this);
		}
	}
	public static class LiteralFloatContext extends LiteralContext {
		public TerminalNode FloatingPointLiteral() { return getToken(ConditionParser.FloatingPointLiteral, 0); }
		public LiteralFloatContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterLiteralFloat(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitLiteralFloat(this);
		}
	}
	public static class LiteralNullContext extends LiteralContext {
		public TerminalNode NullLiteral() { return getToken(ConditionParser.NullLiteral, 0); }
		public LiteralNullContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterLiteralNull(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitLiteralNull(this);
		}
	}
	public static class LiteralStringContext extends LiteralContext {
		public TerminalNode StringLiteral() { return getToken(ConditionParser.StringLiteral, 0); }
		public LiteralStringContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterLiteralString(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitLiteralString(this);
		}
	}
	public static class LiteralIntContext extends LiteralContext {
		public TerminalNode IntegerLiteral() { return getToken(ConditionParser.IntegerLiteral, 0); }
		public LiteralIntContext(LiteralContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).enterLiteralInt(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof ConditionListener ) ((ConditionListener)listener).exitLiteralInt(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_literal);
		try {
			setState(90);
			switch (_input.LA(1)) {
			case IntegerLiteral:
				_localctx = new LiteralIntContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(85); match(IntegerLiteral);
				}
				break;
			case FloatingPointLiteral:
				_localctx = new LiteralFloatContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(86); match(FloatingPointLiteral);
				}
				break;
			case StringLiteral:
				_localctx = new LiteralStringContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(87); match(StringLiteral);
				}
				break;
			case BooleanLiteral:
				_localctx = new LiteralBoolContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(88); match(BooleanLiteral);
				}
				break;
			case NullLiteral:
				_localctx = new LiteralNullContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(89); match(NullLiteral);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 0: return expression_sempred((ExpressionContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0: return precpred(_ctx, 13);

		case 1: return precpred(_ctx, 12);

		case 2: return precpred(_ctx, 11);

		case 3: return precpred(_ctx, 10);

		case 4: return precpred(_ctx, 9);

		case 5: return precpred(_ctx, 8);

		case 6: return precpred(_ctx, 7);

		case 7: return precpred(_ctx, 6);

		case 8: return precpred(_ctx, 5);

		case 9: return precpred(_ctx, 4);

		case 10: return precpred(_ctx, 3);

		case 11: return precpred(_ctx, 2);

		case 12: return precpred(_ctx, 1);

		case 13: return precpred(_ctx, 17);

		case 14: return precpred(_ctx, 16);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\34_\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\3\2\3\2\3\2\3\2\5\2\17\n\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2"+
		"\3\2\3\2\3\2\3\2\3\2\7\2?\n\2\f\2\16\2B\13\2\5\2D\n\2\3\2\3\2\3\2\3\2"+
		"\7\2J\n\2\f\2\16\2M\13\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3V\n\3\3\4\3\4"+
		"\3\4\3\4\3\4\5\4]\n\4\3\4\2\3\2\5\2\4\6\2\2u\2\16\3\2\2\2\4U\3\2\2\2\6"+
		"\\\3\2\2\2\b\t\b\2\1\2\t\n\7\27\2\2\n\17\5\2\2\21\13\f\7\17\2\2\f\17\5"+
		"\2\2\20\r\17\5\4\3\2\16\b\3\2\2\2\16\13\3\2\2\2\16\r\3\2\2\2\17K\3\2\2"+
		"\2\20\21\f\17\2\2\21\22\7\30\2\2\22J\5\2\2\20\23\24\f\16\2\2\24\25\7\31"+
		"\2\2\25J\5\2\2\17\26\27\f\r\2\2\27\30\7\32\2\2\30J\5\2\2\16\31\32\f\f"+
		"\2\2\32\33\7\26\2\2\33J\5\2\2\r\34\35\f\13\2\2\35\36\7\27\2\2\36J\5\2"+
		"\2\f\37 \f\n\2\2 !\7\21\2\2!J\5\2\2\13\"#\f\t\2\2#$\7\22\2\2$J\5\2\2\n"+
		"%&\f\b\2\2&\'\7\r\2\2\'J\5\2\2\t()\f\7\2\2)*\7\16\2\2*J\5\2\2\b+,\f\6"+
		"\2\2,-\7\20\2\2-J\5\2\2\7./\f\5\2\2/\60\7\23\2\2\60J\5\2\2\6\61\62\f\4"+
		"\2\2\62\63\7\24\2\2\63J\5\2\2\5\64\65\f\3\2\2\65\66\7\25\2\2\66J\5\2\2"+
		"\4\678\f\23\2\289\7\f\2\29:\7\33\2\2:C\7\t\2\2;@\5\2\2\2<=\7\13\2\2=?"+
		"\5\2\2\2><\3\2\2\2?B\3\2\2\2@>\3\2\2\2@A\3\2\2\2AD\3\2\2\2B@\3\2\2\2C"+
		";\3\2\2\2CD\3\2\2\2DE\3\2\2\2EJ\7\n\2\2FG\f\22\2\2GH\7\f\2\2HJ\7\33\2"+
		"\2I\20\3\2\2\2I\23\3\2\2\2I\26\3\2\2\2I\31\3\2\2\2I\34\3\2\2\2I\37\3\2"+
		"\2\2I\"\3\2\2\2I%\3\2\2\2I(\3\2\2\2I+\3\2\2\2I.\3\2\2\2I\61\3\2\2\2I\64"+
		"\3\2\2\2I\67\3\2\2\2IF\3\2\2\2JM\3\2\2\2KI\3\2\2\2KL\3\2\2\2L\3\3\2\2"+
		"\2MK\3\2\2\2NO\7\t\2\2OP\5\2\2\2PQ\7\n\2\2QV\3\2\2\2RV\7\3\2\2SV\5\6\4"+
		"\2TV\7\33\2\2UN\3\2\2\2UR\3\2\2\2US\3\2\2\2UT\3\2\2\2V\5\3\2\2\2W]\7\4"+
		"\2\2X]\7\5\2\2Y]\7\7\2\2Z]\7\6\2\2[]\7\b\2\2\\W\3\2\2\2\\X\3\2\2\2\\Y"+
		"\3\2\2\2\\Z\3\2\2\2\\[\3\2\2\2]\7\3\2\2\2\t\16@CIKU\\";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}