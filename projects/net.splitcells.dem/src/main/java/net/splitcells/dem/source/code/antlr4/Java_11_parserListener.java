// Generated from Java_11_parser.g4 by ANTLR 4.9.1

    package net.splitcells.dem.source.code.antlr;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link Java_11_parser}.
 */
public interface Java_11_parserListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link Java_11_parser#source_unit}.
	 * @param ctx the parse tree
	 */
	void enterSource_unit(Java_11_parser.Source_unitContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java_11_parser#source_unit}.
	 * @param ctx the parse tree
	 */
	void exitSource_unit(Java_11_parser.Source_unitContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java_11_parser#import_declaration}.
	 * @param ctx the parse tree
	 */
	void enterImport_declaration(Java_11_parser.Import_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java_11_parser#import_declaration}.
	 * @param ctx the parse tree
	 */
	void exitImport_declaration(Java_11_parser.Import_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java_11_parser#package_declaration}.
	 * @param ctx the parse tree
	 */
	void enterPackage_declaration(Java_11_parser.Package_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java_11_parser#package_declaration}.
	 * @param ctx the parse tree
	 */
	void exitPackage_declaration(Java_11_parser.Package_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java_11_parser#type_name}.
	 * @param ctx the parse tree
	 */
	void enterType_name(Java_11_parser.Type_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java_11_parser#type_name}.
	 * @param ctx the parse tree
	 */
	void exitType_name(Java_11_parser.Type_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link Java_11_parser#package_name}.
	 * @param ctx the parse tree
	 */
	void enterPackage_name(Java_11_parser.Package_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link Java_11_parser#package_name}.
	 * @param ctx the parse tree
	 */
	void exitPackage_name(Java_11_parser.Package_nameContext ctx);
}