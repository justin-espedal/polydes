// Generated from Condition.g4 by ANTLR 4.2.2
package com.polydes.datastruct.grammar;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link ConditionParser}.
 */
public interface ConditionListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprLe}.
	 * @param ctx the parse tree
	 */
	void enterExprLe(@NotNull ConditionParser.ExprLeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprLe}.
	 * @param ctx the parse tree
	 */
	void exitExprLe(@NotNull ConditionParser.ExprLeContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprGe}.
	 * @param ctx the parse tree
	 */
	void enterExprGe(@NotNull ConditionParser.ExprGeContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprGe}.
	 * @param ctx the parse tree
	 */
	void exitExprGe(@NotNull ConditionParser.ExprGeContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprMod}.
	 * @param ctx the parse tree
	 */
	void enterExprMod(@NotNull ConditionParser.ExprModContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprMod}.
	 * @param ctx the parse tree
	 */
	void exitExprMod(@NotNull ConditionParser.ExprModContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#PrimThis}.
	 * @param ctx the parse tree
	 */
	void enterPrimThis(@NotNull ConditionParser.PrimThisContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#PrimThis}.
	 * @param ctx the parse tree
	 */
	void exitPrimThis(@NotNull ConditionParser.PrimThisContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprMultiply}.
	 * @param ctx the parse tree
	 */
	void enterExprMultiply(@NotNull ConditionParser.ExprMultiplyContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprMultiply}.
	 * @param ctx the parse tree
	 */
	void exitExprMultiply(@NotNull ConditionParser.ExprMultiplyContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#PrimIdent}.
	 * @param ctx the parse tree
	 */
	void enterPrimIdent(@NotNull ConditionParser.PrimIdentContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#PrimIdent}.
	 * @param ctx the parse tree
	 */
	void exitPrimIdent(@NotNull ConditionParser.PrimIdentContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprEqual}.
	 * @param ctx the parse tree
	 */
	void enterExprEqual(@NotNull ConditionParser.ExprEqualContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprEqual}.
	 * @param ctx the parse tree
	 */
	void exitExprEqual(@NotNull ConditionParser.ExprEqualContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprLt}.
	 * @param ctx the parse tree
	 */
	void enterExprLt(@NotNull ConditionParser.ExprLtContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprLt}.
	 * @param ctx the parse tree
	 */
	void exitExprLt(@NotNull ConditionParser.ExprLtContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprNegate}.
	 * @param ctx the parse tree
	 */
	void enterExprNegate(@NotNull ConditionParser.ExprNegateContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprNegate}.
	 * @param ctx the parse tree
	 */
	void exitExprNegate(@NotNull ConditionParser.ExprNegateContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprField}.
	 * @param ctx the parse tree
	 */
	void enterExprField(@NotNull ConditionParser.ExprFieldContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprField}.
	 * @param ctx the parse tree
	 */
	void exitExprField(@NotNull ConditionParser.ExprFieldContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprPrimary}.
	 * @param ctx the parse tree
	 */
	void enterExprPrimary(@NotNull ConditionParser.ExprPrimaryContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprPrimary}.
	 * @param ctx the parse tree
	 */
	void exitExprPrimary(@NotNull ConditionParser.ExprPrimaryContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprNot}.
	 * @param ctx the parse tree
	 */
	void enterExprNot(@NotNull ConditionParser.ExprNotContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprNot}.
	 * @param ctx the parse tree
	 */
	void exitExprNot(@NotNull ConditionParser.ExprNotContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#LiteralBool}.
	 * @param ctx the parse tree
	 */
	void enterLiteralBool(@NotNull ConditionParser.LiteralBoolContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#LiteralBool}.
	 * @param ctx the parse tree
	 */
	void exitLiteralBool(@NotNull ConditionParser.LiteralBoolContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#LiteralNull}.
	 * @param ctx the parse tree
	 */
	void enterLiteralNull(@NotNull ConditionParser.LiteralNullContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#LiteralNull}.
	 * @param ctx the parse tree
	 */
	void exitLiteralNull(@NotNull ConditionParser.LiteralNullContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprSub}.
	 * @param ctx the parse tree
	 */
	void enterExprSub(@NotNull ConditionParser.ExprSubContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprSub}.
	 * @param ctx the parse tree
	 */
	void exitExprSub(@NotNull ConditionParser.ExprSubContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#LiteralInt}.
	 * @param ctx the parse tree
	 */
	void enterLiteralInt(@NotNull ConditionParser.LiteralIntContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#LiteralInt}.
	 * @param ctx the parse tree
	 */
	void exitLiteralInt(@NotNull ConditionParser.LiteralIntContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprAdd}.
	 * @param ctx the parse tree
	 */
	void enterExprAdd(@NotNull ConditionParser.ExprAddContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprAdd}.
	 * @param ctx the parse tree
	 */
	void exitExprAdd(@NotNull ConditionParser.ExprAddContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprDivide}.
	 * @param ctx the parse tree
	 */
	void enterExprDivide(@NotNull ConditionParser.ExprDivideContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprDivide}.
	 * @param ctx the parse tree
	 */
	void exitExprDivide(@NotNull ConditionParser.ExprDivideContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprInvocation}.
	 * @param ctx the parse tree
	 */
	void enterExprInvocation(@NotNull ConditionParser.ExprInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprInvocation}.
	 * @param ctx the parse tree
	 */
	void exitExprInvocation(@NotNull ConditionParser.ExprInvocationContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprGt}.
	 * @param ctx the parse tree
	 */
	void enterExprGt(@NotNull ConditionParser.ExprGtContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprGt}.
	 * @param ctx the parse tree
	 */
	void exitExprGt(@NotNull ConditionParser.ExprGtContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#PrimLit}.
	 * @param ctx the parse tree
	 */
	void enterPrimLit(@NotNull ConditionParser.PrimLitContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#PrimLit}.
	 * @param ctx the parse tree
	 */
	void exitPrimLit(@NotNull ConditionParser.PrimLitContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#LiteralFloat}.
	 * @param ctx the parse tree
	 */
	void enterLiteralFloat(@NotNull ConditionParser.LiteralFloatContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#LiteralFloat}.
	 * @param ctx the parse tree
	 */
	void exitLiteralFloat(@NotNull ConditionParser.LiteralFloatContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#LiteralString}.
	 * @param ctx the parse tree
	 */
	void enterLiteralString(@NotNull ConditionParser.LiteralStringContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#LiteralString}.
	 * @param ctx the parse tree
	 */
	void exitLiteralString(@NotNull ConditionParser.LiteralStringContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprOr}.
	 * @param ctx the parse tree
	 */
	void enterExprOr(@NotNull ConditionParser.ExprOrContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprOr}.
	 * @param ctx the parse tree
	 */
	void exitExprOr(@NotNull ConditionParser.ExprOrContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprAnd}.
	 * @param ctx the parse tree
	 */
	void enterExprAnd(@NotNull ConditionParser.ExprAndContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprAnd}.
	 * @param ctx the parse tree
	 */
	void exitExprAnd(@NotNull ConditionParser.ExprAndContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#ExprNotEqual}.
	 * @param ctx the parse tree
	 */
	void enterExprNotEqual(@NotNull ConditionParser.ExprNotEqualContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#ExprNotEqual}.
	 * @param ctx the parse tree
	 */
	void exitExprNotEqual(@NotNull ConditionParser.ExprNotEqualContext ctx);

	/**
	 * Enter a parse tree produced by {@link ConditionParser#PrimExpr}.
	 * @param ctx the parse tree
	 */
	void enterPrimExpr(@NotNull ConditionParser.PrimExprContext ctx);
	/**
	 * Exit a parse tree produced by {@link ConditionParser#PrimExpr}.
	 * @param ctx the parse tree
	 */
	void exitPrimExpr(@NotNull ConditionParser.PrimExprContext ctx);
}