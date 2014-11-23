package stencyl.ext.polydes.datastruct.grammar;

import java.util.List;
import java.util.Stack;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import stencyl.ext.polydes.datastruct.grammar.ConditionBaseListener;
import stencyl.ext.polydes.datastruct.grammar.ConditionLexer;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprAddContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprAndContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprDivideContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprEqualContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprFieldContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprGeContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprGtContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprInvocationContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprLeContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprLtContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprModContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprMultiplyContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprNegateContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprNotContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprNotEqualContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprOrContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprPrimaryContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExprSubContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.ExpressionContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.LiteralBoolContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.LiteralFloatContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.LiteralIntContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.LiteralNullContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.LiteralStringContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.PrimExprContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.PrimIdentContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.PrimLitContext;
import stencyl.ext.polydes.datastruct.grammar.ConditionParser.PrimThisContext;

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
	public static Node buildTree(String expression)
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
	private Node internalBuildTree(String expression)
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
		Stack<Node> stack = new Stack<Node>();
		
		/**
		 * returns null if the input wasn't a valid Java expression
		 */
		public Node getResult()
		{
			if(stack.isEmpty())
				return null;
			
			Node o = stack.pop();
			stack.clear();
			return o;
		}
		
		@Override
		public void exitExprAdd(ExprAddContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.ADD, l, r));
		}
		
		@Override
		public void exitExprAnd(ExprAndContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.AND, l, r));
		}
		
		@Override
		public void exitExprDivide(ExprDivideContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.DIVIDE, l, r));
		}
		
		@Override
		public void exitExprEqual(ExprEqualContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.EQUAL, l, r));
		}
		
		@Override
		public void exitExprField(ExprFieldContext ctx)
		{
			Node o = stack.pop();
			String name = ctx.name.getText();
			
			stack.push(new Node(LangElement.FIELD, o, new Node(LangElement.STRING, name)));
		}
		
		@Override
		public void exitExprGe(ExprGeContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.GE, l, r));
		}
		
		@Override
		public void exitExprGt(ExprGtContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.GT, l, r));
		}
		
		@Override
		public void exitExprInvocation(ExprInvocationContext ctx)
		{
			List<ExpressionContext> list = ctx.expression();
			String name = ctx.name.getText();
			
			Node[] args = new Node[list.size() + 1];
			int argIndex = args.length - 1;
			while(argIndex > 1)
				args[argIndex--] = stack.pop();
			args[1] = new Node(LangElement.STRING, name);
			args[0] = stack.pop();
			
			stack.push(new Node(LangElement.METHOD, args));
		}
		
		@Override
		public void exitExprLe(ExprLeContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.LE, l, r));
		}
		
		@Override
		public void exitExprLt(ExprLtContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.LT, l, r));
		}
		
		@Override
		public void exitExprMod(ExprModContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.MOD, l, r));
		}
		
		@Override
		public void exitExprMultiply(ExprMultiplyContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.MULTIPLY, l, r));
		}
		
		@Override
		public void exitExprNegate(ExprNegateContext ctx)
		{
			Node o = stack.pop();
			
			stack.push(new Node(LangElement.NEGATE, o));
		}
		
		@Override
		public void exitExprNot(ExprNotContext ctx)
		{
			Node o = stack.pop();
			
			stack.push(new Node(LangElement.NOT, o));
		}
		
		@Override
		public void exitExprNotEqual(ExprNotEqualContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.NOTEQUAL, l, r));
		}
		
		@Override
		public void exitExprOr(ExprOrContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.OR, l, r));
		}
		
		@Override
		public void exitExprSub(ExprSubContext ctx)
		{
			Node r = stack.pop();
			Node l = stack.pop();
			
			stack.push(new Node(LangElement.SUB, l, r));
		}
		
		@Override
		public void exitLiteralBool(LiteralBoolContext ctx)
		{
			stack.push(new Node(LangElement.BOOL, Boolean.parseBoolean(ctx.getText())));
		}
		
		@Override
		public void exitLiteralFloat(LiteralFloatContext ctx)
		{
			stack.push(new Node(LangElement.FLOAT, Float.parseFloat(ctx.getText())));
		}
		
		@Override
		public void exitLiteralInt(LiteralIntContext ctx)
		{
			stack.push(new Node(LangElement.INT, Integer.parseInt(ctx.getText())));
		}
		
		@Override
		public void exitLiteralNull(LiteralNullContext ctx)
		{
			stack.push(new Node(LangElement.NULL));
		}
		
		@Override
		public void exitLiteralString(LiteralStringContext ctx)
		{
			stack.push(new Node(LangElement.STRING, ctx.getText()));
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
			stack.push(new Node(LangElement.REFERENCE, ctx.name.getText()));
		}
		
		@Override
		public void exitPrimLit(PrimLitContext ctx)
		{
			//Do nothing, value already on stack
		}
		
		@Override
		public void exitPrimThis(PrimThisContext ctx)
		{
			stack.push(new Node(LangElement.REFERENCE, "this"));
		}
	}
}
