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
		Keyword_static=5, Object_accessor=6, Name=7;
	public static final int
		RULE_source_unit = 0, RULE_import_declaration = 1, RULE_import_static_declaration = 2, 
		RULE_import_type_declaration = 3, RULE_package_declaration = 4, RULE_type_name = 5, 
		RULE_package_name = 6;
	private static String[] makeRuleNames() {
		return new String[] {
			"source_unit", "import_declaration", "import_static_declaration", "import_type_declaration", 
			"package_declaration", "type_name", "package_name"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "';'", "' '", "'package'", "'import'", "'static'", "'.'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "Statement_terminator", "Whitespace", "Keyword_package", "Keyword_import", 
			"Keyword_static", "Object_accessor", "Name"
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
			setState(14);
			package_declaration();
			setState(18);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Whitespace || _la==Keyword_import) {
				{
				{
				setState(15);
				import_declaration();
				}
				}
				setState(20);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(21);
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
		public Import_static_declarationContext import_static_declaration() {
			return getRuleContext(Import_static_declarationContext.class,0);
		}
		public Import_type_declarationContext import_type_declaration() {
			return getRuleContext(Import_type_declarationContext.class,0);
		}
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
		try {
			setState(25);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(23);
				import_static_declaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(24);
				import_type_declaration();
				}
				break;
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

	public static class Import_static_declarationContext extends ParserRuleContext {
		public TerminalNode Keyword_import() { return getToken(Java_11_parser.Keyword_import, 0); }
		public List<TerminalNode> Whitespace() { return getTokens(Java_11_parser.Whitespace); }
		public TerminalNode Whitespace(int i) {
			return getToken(Java_11_parser.Whitespace, i);
		}
		public TerminalNode Keyword_static() { return getToken(Java_11_parser.Keyword_static, 0); }
		public Type_nameContext type_name() {
			return getRuleContext(Type_nameContext.class,0);
		}
		public TerminalNode Statement_terminator() { return getToken(Java_11_parser.Statement_terminator, 0); }
		public Import_static_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_import_static_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).enterImport_static_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).exitImport_static_declaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Java_11_parserVisitor ) return ((Java_11_parserVisitor<? extends T>)visitor).visitImport_static_declaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Import_static_declarationContext import_static_declaration() throws RecognitionException {
		Import_static_declarationContext _localctx = new Import_static_declarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_import_static_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(30);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Whitespace) {
				{
				{
				setState(27);
				match(Whitespace);
				}
				}
				setState(32);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(33);
			match(Keyword_import);
			setState(34);
			match(Whitespace);
			setState(35);
			match(Keyword_static);
			setState(36);
			match(Whitespace);
			setState(37);
			type_name(0);
			setState(38);
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

	public static class Import_type_declarationContext extends ParserRuleContext {
		public TerminalNode Keyword_import() { return getToken(Java_11_parser.Keyword_import, 0); }
		public List<TerminalNode> Whitespace() { return getTokens(Java_11_parser.Whitespace); }
		public TerminalNode Whitespace(int i) {
			return getToken(Java_11_parser.Whitespace, i);
		}
		public Type_nameContext type_name() {
			return getRuleContext(Type_nameContext.class,0);
		}
		public TerminalNode Statement_terminator() { return getToken(Java_11_parser.Statement_terminator, 0); }
		public Import_type_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_import_type_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).enterImport_type_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof Java_11_parserListener ) ((Java_11_parserListener)listener).exitImport_type_declaration(this);
		}
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof Java_11_parserVisitor ) return ((Java_11_parserVisitor<? extends T>)visitor).visitImport_type_declaration(this);
			else return visitor.visitChildren(this);
		}
	}

	public final Import_type_declarationContext import_type_declaration() throws RecognitionException {
		Import_type_declarationContext _localctx = new Import_type_declarationContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_import_type_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(43);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Whitespace) {
				{
				{
				setState(40);
				match(Whitespace);
				}
				}
				setState(45);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(46);
			match(Keyword_import);
			setState(47);
			match(Whitespace);
			setState(48);
			type_name(0);
			setState(49);
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
		enterRule(_localctx, 8, RULE_package_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(54);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==Whitespace) {
				{
				{
				setState(51);
				match(Whitespace);
				}
				}
				setState(56);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(57);
			match(Keyword_package);
			setState(58);
			match(Whitespace);
			setState(59);
			package_name(0);
			setState(60);
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
		int _startState = 10;
		enterRecursionRule(_localctx, 10, RULE_type_name, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(63);
			match(Name);
			}
			_ctx.stop = _input.LT(-1);
			setState(70);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Type_nameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_type_name);
					setState(65);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(66);
					match(Object_accessor);
					setState(67);
					match(Name);
					}
					} 
				}
				setState(72);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
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
		int _startState = 12;
		enterRecursionRule(_localctx, 12, RULE_package_name, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			{
			setState(74);
			match(Name);
			}
			_ctx.stop = _input.LT(-1);
			setState(81);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new Package_nameContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_package_name);
					setState(76);
					if (!(precpred(_ctx, 1))) throw new FailedPredicateException(this, "precpred(_ctx, 1)");
					setState(77);
					match(Object_accessor);
					setState(78);
					match(Name);
					}
					} 
				}
				setState(83);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
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
		case 5:
			return type_name_sempred((Type_nameContext)_localctx, predIndex);
		case 6:
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
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3\tW\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\3\2\7\2\23\n\2\f\2\16\2"+
		"\26\13\2\3\2\3\2\3\3\3\3\5\3\34\n\3\3\4\7\4\37\n\4\f\4\16\4\"\13\4\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\5\7\5,\n\5\f\5\16\5/\13\5\3\5\3\5\3\5\3\5\3"+
		"\5\3\6\7\6\67\n\6\f\6\16\6:\13\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3"+
		"\7\3\7\7\7G\n\7\f\7\16\7J\13\7\3\b\3\b\3\b\3\b\3\b\3\b\7\bR\n\b\f\b\16"+
		"\bU\13\b\3\b\2\4\f\16\t\2\4\6\b\n\f\16\2\2\2V\2\20\3\2\2\2\4\33\3\2\2"+
		"\2\6 \3\2\2\2\b-\3\2\2\2\n8\3\2\2\2\f@\3\2\2\2\16K\3\2\2\2\20\24\5\n\6"+
		"\2\21\23\5\4\3\2\22\21\3\2\2\2\23\26\3\2\2\2\24\22\3\2\2\2\24\25\3\2\2"+
		"\2\25\27\3\2\2\2\26\24\3\2\2\2\27\30\7\2\2\3\30\3\3\2\2\2\31\34\5\6\4"+
		"\2\32\34\5\b\5\2\33\31\3\2\2\2\33\32\3\2\2\2\34\5\3\2\2\2\35\37\7\4\2"+
		"\2\36\35\3\2\2\2\37\"\3\2\2\2 \36\3\2\2\2 !\3\2\2\2!#\3\2\2\2\" \3\2\2"+
		"\2#$\7\6\2\2$%\7\4\2\2%&\7\7\2\2&\'\7\4\2\2\'(\5\f\7\2()\7\3\2\2)\7\3"+
		"\2\2\2*,\7\4\2\2+*\3\2\2\2,/\3\2\2\2-+\3\2\2\2-.\3\2\2\2.\60\3\2\2\2/"+
		"-\3\2\2\2\60\61\7\6\2\2\61\62\7\4\2\2\62\63\5\f\7\2\63\64\7\3\2\2\64\t"+
		"\3\2\2\2\65\67\7\4\2\2\66\65\3\2\2\2\67:\3\2\2\28\66\3\2\2\289\3\2\2\2"+
		"9;\3\2\2\2:8\3\2\2\2;<\7\5\2\2<=\7\4\2\2=>\5\16\b\2>?\7\3\2\2?\13\3\2"+
		"\2\2@A\b\7\1\2AB\7\t\2\2BH\3\2\2\2CD\f\3\2\2DE\7\b\2\2EG\7\t\2\2FC\3\2"+
		"\2\2GJ\3\2\2\2HF\3\2\2\2HI\3\2\2\2I\r\3\2\2\2JH\3\2\2\2KL\b\b\1\2LM\7"+
		"\t\2\2MS\3\2\2\2NO\f\3\2\2OP\7\b\2\2PR\7\t\2\2QN\3\2\2\2RU\3\2\2\2SQ\3"+
		"\2\2\2ST\3\2\2\2T\17\3\2\2\2US\3\2\2\2\t\24\33 -8HS";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}