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
		Statement_terminator=1, Whitespace=2, Keyword_package=3, Keyword_import=4, 
		Object_accessor=5, Name=6;
	public static final int
		RULE_source_unit = 0, RULE_import_declaration = 1, RULE_package_declaration = 2, 
		RULE_type_name = 3, RULE_package_name = 4;
	private static String[] makeRuleNames() {
		return new String[] {
			"source_unit", "import_declaration", "package_declaration", "type_name", 
			"package_name"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "' '", "'package'", "'import'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Statement_terminator", "Whitespace", "Keyword_package", "Keyword_import", 
			"Object_accessor", "Name"
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
		public List<Import_declarationContext> import_declaration() {
			return getRuleContexts(Import_declarationContext.class);
		}
		public Import_declarationContext import_declaration(int i) {
			return getRuleContext(Import_declarationContext.class,i);
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
			setState(10);
			package_declaration();
			setState(14);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Whitespace || _la==Keyword_import) {
				{
				{
				setState(11);
				import_declaration();
				}
				}
				setState(16);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(17);
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

	public static class Import_declarationContext extends ParserRuleContext {
		public TerminalNode Keyword_import() { return getToken(Java_11_parser.Keyword_import, 0); }
		public List<TerminalNode> Whitespace() { return getTokens(Java_11_parser.Whitespace); }
		public TerminalNode Whitespace(int i) {
			return getToken(Java_11_parser.Whitespace, i);
		}
		public Type_nameContext type_name() {
			return getRuleContext(Type_nameContext.class,0);
		}
		public TerminalNode Statement_terminator() { return getToken(Java_11_parser.Statement_terminator, 0); }
		public Import_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_import_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).enterImport_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).exitImport_declaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Java_11_parserVisitor ) return ((Java_11_parserVisitor<? extends T>)visitor).visitImport_declaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Import_declarationContext import_declaration() throws RecognitionException {
		Import_declarationContext _localctx = new Import_declarationContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_import_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(22);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Whitespace) {
				{
				{
				setState(19);
				match(Whitespace);
				}
				}
				setState(24);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(25);
			match(Keyword_import);
			setState(26);
			match(Whitespace);
			setState(27);
			type_name(0);
			setState(28);
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

	public static class Package_declarationContext extends ParserRuleContext {
		public TerminalNode Keyword_package() { return getToken(Java_11_parser.Keyword_package, 0); }
		public List<TerminalNode> Whitespace() { return getTokens(Java_11_parser.Whitespace); }
		public TerminalNode Whitespace(int i) {
			return getToken(Java_11_parser.Whitespace, i);
		}
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
		enterRule(_localctx, 4, RULE_package_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(33);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Whitespace) {
				{
				{
				setState(30);
				match(Whitespace);
				}
				}
				setState(35);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(36);
			match(Keyword_package);
			setState(37);
			match(Whitespace);
			setState(38);
			package_name(0);
			setState(39);
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

	public static class Type_nameContext extends ParserRuleContext {
		public TerminalNode Name() { return getToken(Java_11_parser.Name, 0); }
		public Type_nameContext type_name() {
			return getRuleContext(Type_nameContext.class,0);
		}
		public TerminalNode Object_accessor() { return getToken(Java_11_parser.Object_accessor, 0); }
		public Type_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).enterType_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).exitType_name(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Java_11_parserVisitor ) return ((Java_11_parserVisitor<? extends T>)visitor).visitType_name(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Type_nameContext type_name() throws RecognitionException {
		return type_name(0);
	}

	private Type_nameContext type_name(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		Type_nameContext _localctx = new Type_nameContext(_ctx, _parentState);
		Type_nameContext _prevctx = _localctx;
		int _startState = 6;
		enterRecursionRule(_localctx, 6, RULE_type_name, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(42);
			match(Name);
			}
			_ctx.stop = _input.LT(-1);
			setState(49);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Type_nameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_type_name);
					setState(44);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(45);
					match(Object_accessor);
					setState(46);
					match(Name);
					}
					} 
				}
				setState(51);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
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
		int _startState = 8;
		enterRecursionRule(_localctx, 8, RULE_package_name, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(53);
			match(Name);
			}
			_ctx.stop = _input.LT(-1);
			setState(60);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Package_nameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_package_name);
					setState(55);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(56);
					match(Object_accessor);
					setState(57);
					match(Name);
					}
					} 
				}
				setState(62);
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

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 3:
			return type_name_sempred((Type_nameContext)_localctx, predIndex);
		case 4:
			return package_name_sempred((Package_nameContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean type_name_sempred(Type_nameContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 1);
		}
		return true;
	}
	private boolean package_name_sempred(Package_nameContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 1);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\bB\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\3\2\3\2\7\2\17\n\2\f\2\16\2\22\13\2\3\2\3\2"+
		"\3\3\7\3\27\n\3\f\3\16\3\32\13\3\3\3\3\3\3\3\3\3\3\3\3\4\7\4\"\n\4\f\4"+
		"\16\4%\13\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\7\5\62\n\5\f\5"+
		"\16\5\65\13\5\3\6\3\6\3\6\3\6\3\6\3\6\7\6=\n\6\f\6\16\6@\13\6\3\6\2\4"+
		"\b\n\7\2\4\6\b\n\2\2\2A\2\f\3\2\2\2\4\30\3\2\2\2\6#\3\2\2\2\b+\3\2\2\2"+
		"\n\66\3\2\2\2\f\20\5\6\4\2\r\17\5\4\3\2\16\r\3\2\2\2\17\22\3\2\2\2\20"+
		"\16\3\2\2\2\20\21\3\2\2\2\21\23\3\2\2\2\22\20\3\2\2\2\23\24\7\2\2\3\24"+
		"\3\3\2\2\2\25\27\7\4\2\2\26\25\3\2\2\2\27\32\3\2\2\2\30\26\3\2\2\2\30"+
		"\31\3\2\2\2\31\33\3\2\2\2\32\30\3\2\2\2\33\34\7\6\2\2\34\35\7\4\2\2\35"+
		"\36\5\b\5\2\36\37\7\3\2\2\37\5\3\2\2\2 \"\7\4\2\2! \3\2\2\2\"%\3\2\2\2"+
		"#!\3\2\2\2#$\3\2\2\2$&\3\2\2\2%#\3\2\2\2&\'\7\5\2\2\'(\7\4\2\2()\5\n\6"+
		"\2)*\7\3\2\2*\7\3\2\2\2+,\b\5\1\2,-\7\b\2\2-\63\3\2\2\2./\f\3\2\2/\60"+
		"\7\7\2\2\60\62\7\b\2\2\61.\3\2\2\2\62\65\3\2\2\2\63\61\3\2\2\2\63\64\3"+
		"\2\2\2\64\t\3\2\2\2\65\63\3\2\2\2\66\67\b\6\1\2\678\7\b\2\28>\3\2\2\2"+
		"9:\f\3\2\2:;\7\7\2\2;=\7\b\2\2<9\3\2\2\2=@\3\2\2\2><\3\2\2\2>?\3\2\2\2"+
		"?\13\3\2\2\2@>\3\2\2\2\7\20\30#\63>";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}