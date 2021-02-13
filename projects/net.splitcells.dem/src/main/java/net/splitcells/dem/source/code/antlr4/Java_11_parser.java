// Generated from Java_11_parser.g4 by ANTLR 4.9.1

    package net.splitcells.dem.source.code.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class Java_11_parser extends Parser {
	static { RuntimeMetaData.checkVersion("4.9.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		Statement_terminator=1, Whitespace=2, Keyword_package=3, Object_accessor=4, 
		Name=5;
	public static final int
		RULE_source_unit = 0, RULE_package_declaration = 1, RULE_package_name = 2;
	private static String[] makeRuleNames() {
		return new String[] {
			"source_unit", "package_declaration", "package_name"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "' '", "'package'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Statement_terminator", "Whitespace", "Keyword_package", "Object_accessor", 
			"Name"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Java_11_parser.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public Java_11_parser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class Source_unitContext extends ParserRuleContext {
		public Package_declarationContext package_declaration() {
			return getRuleContext(Package_declarationContext.class,0);
		}
		public TerminalNode EOF() { return getToken(Java_11_parser.EOF, 0); }
		public List<TerminalNode> Whitespace() { return getTokens(Java_11_parser.Whitespace); }
		public TerminalNode Whitespace(int i) {
			return getToken(Java_11_parser.Whitespace, i);
		}
		public Source_unitContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_source_unit; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).enterSource_unit(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).exitSource_unit(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Java_11_parserVisitor ) return ((Java_11_parserVisitor<? extends T>)visitor).visitSource_unit(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Source_unitContext source_unit() throws RecognitionException {
		Source_unitContext _localctx = new Source_unitContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_source_unit);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(6);
			package_declaration();
			setState(10);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Whitespace) {
				{
				{
				setState(7);
				match(Whitespace);
				}
				}
				setState(12);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(13);
			match(EOF);
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

	public static class Package_declarationContext extends ParserRuleContext {
		public TerminalNode Keyword_package() { return getToken(Java_11_parser.Keyword_package, 0); }
		public TerminalNode Whitespace() { return getToken(Java_11_parser.Whitespace, 0); }
		public Package_nameContext package_name() {
			return getRuleContext(Package_nameContext.class,0);
		}
		public TerminalNode Statement_terminator() { return getToken(Java_11_parser.Statement_terminator, 0); }
		public Package_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_package_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).enterPackage_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).exitPackage_declaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Java_11_parserVisitor ) return ((Java_11_parserVisitor<? extends T>)visitor).visitPackage_declaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Package_declarationContext package_declaration() throws RecognitionException {
		Package_declarationContext _localctx = new Package_declarationContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_package_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(15);
			match(Keyword_package);
			setState(16);
			match(Whitespace);
			setState(17);
			package_name(0);
			setState(18);
			match(Statement_terminator);
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

	public static class Package_nameContext extends ParserRuleContext {
		public TerminalNode Name() { return getToken(Java_11_parser.Name, 0); }
		public Package_nameContext package_name() {
			return getRuleContext(Package_nameContext.class,0);
		}
		public TerminalNode Object_accessor() { return getToken(Java_11_parser.Object_accessor, 0); }
		public Package_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_package_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).enterPackage_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).exitPackage_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Java_11_parserVisitor ) return ((Java_11_parserVisitor<? extends T>)visitor).visitPackage_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Package_nameContext package_name() throws RecognitionException {
		return package_name(0);
	}

	private Package_nameContext package_name(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Package_nameContext _localctx = new Package_nameContext(_ctx, _parentState);
		Package_nameContext _prevctx = _localctx;
		int _startState = 4;
		enterRecursionRule(_localctx, 4, RULE_package_name, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(21);
			match(Name);
			}
			_ctx.stop = _input.LT(-1);
			setState(28);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Package_nameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_package_name);
					setState(23);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(24);
					match(Object_accessor);
					setState(25);
					match(Name);
					}
					} 
				}
				setState(30);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 2:
			return package_name_sempred((Package_nameContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean package_name_sempred(Package_nameContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\7\"\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\3\2\3\2\7\2\13\n\2\f\2\16\2\16\13\2\3\2\3\2\3\3\3\3\3\3\3\3"+
		"\3\3\3\4\3\4\3\4\3\4\3\4\3\4\7\4\35\n\4\f\4\16\4 \13\4\3\4\2\3\6\5\2\4"+
		"\6\2\2\2 \2\b\3\2\2\2\4\21\3\2\2\2\6\26\3\2\2\2\b\f\5\4\3\2\t\13\7\4\2"+
		"\2\n\t\3\2\2\2\13\16\3\2\2\2\f\n\3\2\2\2\f\r\3\2\2\2\r\17\3\2\2\2\16\f"+
		"\3\2\2\2\17\20\7\2\2\3\20\3\3\2\2\2\21\22\7\5\2\2\22\23\7\4\2\2\23\24"+
		"\5\6\4\2\24\25\7\3\2\2\25\5\3\2\2\2\26\27\b\4\1\2\27\30\7\7\2\2\30\36"+
		"\3\2\2\2\31\32\f\3\2\2\32\33\7\6\2\2\33\35\7\7\2\2\34\31\3\2\2\2\35 \3"+
		"\2\2\2\36\34\3\2\2\2\36\37\3\2\2\2\37\7\3\2\2\2 \36\3\2\2\2\4\f\36";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}