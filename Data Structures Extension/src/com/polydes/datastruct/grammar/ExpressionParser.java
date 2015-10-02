package com.polydes.datastruct.grammar;

import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import com.polydes.datastruct.grammar.ConditionBaseListener;
import com.polydes.datastruct.grammar.ConditionLexer;
import com.polydes.datastruct.grammar.ConditionParser;
import com.polydes.datastruct.grammar.ConditionParser.ExprAddContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprAndContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprDivideContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprEqualContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprFieldContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprGeContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprGtContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprInvocationContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprLeContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprLtContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprModContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprMultiplyContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprNegateContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprNotContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprNotEqualContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprOrContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprPrimaryContext;
import com.polydes.datastruct.grammar.ConditionParser.ExprSubContext;
import com.polydes.datastruct.grammar.ConditionParser.ExpressionContext;
import com.polydes.datastruct.grammar.ConditionParser.LiteralBoolContext;
import com.polydes.datastruct.grammar.ConditionParser.LiteralFloatContext;
import com.polydes.datastruct.grammar.ConditionParser.LiteralIntContext;
import com.polydes.datastruct.grammar.ConditionParser.LiteralNullContext;
import com.polydes.datastruct.grammar.ConditionParser.LiteralStringContext;
import com.polydes.datastruct.grammar.ConditionParser.PrimExprContext;
import com.polydes.datastruct.grammar.ConditionParser.PrimIdentContext;
import com.polydes.datastruct.grammar.ConditionParser.PrimLitContext;
import com.polydes.datastruct.grammar.ConditionParser.PrimThisContext;

public class ExpressionParser
{
	private static ExpressionParser instance = null;
	
	private ParseTreeWalker walker;
	private SimpleTreeBuilder builder;
	
	/**
	 * Builds an AST that represents a Java expression.
	 * 
	 * @param expression A Java expression
	 * @return           A {@code Node} representing the root of an AST, or {@code null} if the input wasn't a valid Java expression
	 */
	public static SyntaxNode buildTree(String expression)
	{
		if(instance == null)
			instance = new ExpressionParser();
		
		return instance.internalBuildTree(expression);
	}
	
	public static void dispose()
	{
		instance.walker = null;
		instance.builder = null;
		instance = null;
	}
	
	private ExpressionParser()
	{
		walker = new ParseTreeWalker(); // create standard walker
		builder = new SimpleTreeBuilder();
	}
	
	/**
	 * returns null if the input wasn't a valid Java expression
	 */
	private SyntaxNode internalBuildTree(String expression)
	{
		ANTLRInputStream stream = new ANTLRInputStream(expression);
		
		ConditionLexer lexer = new ConditionLexer(stream);
		lexer.removeErrorListeners();
		lexer.addErrorListener(ThrowingErrorListener.INSTANCE);
		
		TokenStream tokenStream = new CommonTokenStream(lexer);
		
		ConditionParser parser = new ConditionParser(tokenStream);
		parser.removeErrorListeners();
		parser.addErrorListener(ThrowingErrorListener.INSTANCE);
		ParserRuleContext tree;
		
		try
		{
			tree = parser.expression(); // parse
		}
		catch(ParseCancellationException ex)
		{
			System.out.println(ex.getMessage());
			return null;
		}
		
		walker.walk(builder, tree); // initiate walk of tree with listener
		return builder.getResult();	
	}
	
	class SimpleTreeBuilder extends ConditionBaseListener
	{
		Stack<SyntaxNode> stack = new Stack<SyntaxNode>();
		
		/**
		 * returns null if the input wasn't a valid Java expression
		 */
		public SyntaxNode getResult()
		{
			if(stack.isEmpty())
				return null;
			
			SyntaxNode o = stack.pop();
			stack.clear();
			return o;
		}
		
		@Override
		public void exitExprAdd(ExprAddContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.ADD, l, r));
		}
		
		@Override
		public void exitExprAnd(ExprAndContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.AND, l, r));
		}
		
		@Override
		public void exitExprDivide(ExprDivideContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.DIVIDE, l, r));
		}
		
		@Override
		public void exitExprEqual(ExprEqualContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.EQUAL, l, r));
		}
		
		@Override
		public void exitExprField(ExprFieldContext ctx)
		{
			SyntaxNode o = stack.pop();
			String name = ctx.name.getText();
			
			stack.push(new SyntaxNode(LangElement.FIELD, o, new SyntaxNode(LangElement.STRING, name)));
		}
		
		@Override
		public void exitExprGe(ExprGeContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.GE, l, r));
		}
		
		@Override
		public void exitExprGt(ExprGtContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.GT, l, r));
		}
		
		@Override
		public void exitExprInvocation(ExprInvocationContext ctx)
		{
			List<ExpressionContext> list = ctx.expression();
			String name = ctx.name.getText();
			
			SyntaxNode[] args = new SyntaxNode[list.size() + 1];
			int argIndex = args.length - 1;
			while(argIndex > 1)
				args[argIndex--] = stack.pop();
			args[1] = new SyntaxNode(LangElement.STRING, name);
			args[0] = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.METHOD, args));
		}
		
		@Override
		public void exitExprLe(ExprLeContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.LE, l, r));
		}
		
		@Override
		public void exitExprLt(ExprLtContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.LT, l, r));
		}
		
		@Override
		public void exitExprMod(ExprModContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.MOD, l, r));
		}
		
		@Override
		public void exitExprMultiply(ExprMultiplyContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.MULTIPLY, l, r));
		}
		
		@Override
		public void exitExprNegate(ExprNegateContext ctx)
		{
			SyntaxNode o = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.NEGATE, o));
		}
		
		@Override
		public void exitExprNot(ExprNotContext ctx)
		{
			SyntaxNode o = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.NOT, o));
		}
		
		@Override
		public void exitExprNotEqual(ExprNotEqualContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.NOTEQUAL, l, r));
		}
		
		@Override
		public void exitExprOr(ExprOrContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.OR, l, r));
		}
		
		@Override
		public void exitExprSub(ExprSubContext ctx)
		{
			SyntaxNode r = stack.pop();
			SyntaxNode l = stack.pop();
			
			stack.push(new SyntaxNode(LangElement.SUB, l, r));
		}
		
		@Override
		public void exitLiteralBool(LiteralBoolContext ctx)
		{
			stack.push(new SyntaxNode(LangElement.BOOL, Boolean.parseBoolean(ctx.getText())));
		}
		
		@Override
		public void exitLiteralFloat(LiteralFloatContext ctx)
		{
			stack.push(new SyntaxNode(LangElement.FLOAT, Float.parseFloat(ctx.getText())));
		}
		
		@Override
		public void exitLiteralInt(LiteralIntContext ctx)
		{
			stack.push(new SyntaxNode(LangElement.INT, Integer.parseInt(ctx.getText())));
		}
		
		@Override
		public void exitLiteralNull(LiteralNullContext ctx)
		{
			stack.push(new SyntaxNode(LangElement.NULL));
		}
		
		@Override
		public void exitLiteralString(LiteralStringContext ctx)
		{
			String s = ctx.getText();
			if(s.startsWith("\"") && s.endsWith("\""))
				s = s.substring(1, s.length() - 1);
			stack.push(new SyntaxNode(LangElement.STRING, s));
		}
		
		@Override
		public void exitPrimExpr(PrimExprContext ctx)
		{
			//Do nothing, expression is already on the stack
		}
		
		@Override
		public void exitExprPrimary(ExprPrimaryContext ctx)
		{
			//Do nothing, value of primary is already on the stack
		}
		
		@Override
		public void exitPrimIdent(PrimIdentContext ctx)
		{
			stack.push(new SyntaxNode(LangElement.REFERENCE, ctx.name.getText()));
		}
		
		@Override
		public void exitPrimLit(PrimLitContext ctx)
		{
			//Do nothing, value already on stack
		}
		
		@Override
		public void exitPrimThis(PrimThisContext ctx)
		{
			stack.push(new SyntaxNode(LangElement.REFERENCE, "this"));
		}
	}
}
