// Generated from Java_11_parser.g4 by ANTLR 4.9.1

    package net.splitcells.dem.source.code.antlr;

import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link Java_11_parser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface Java_11_parserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link Java_11_parser#source_unit}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSource_unit(Java_11_parser.Source_unitContext ctx);
	/**
	 * Visit a parse tree produced by {@link Java_11_parser#import_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_declaration(Java_11_parser.Import_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link Java_11_parser#import_static_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_static_declaration(Java_11_parser.Import_static_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link Java_11_parser#import_type_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitImport_type_declaration(Java_11_parser.Import_type_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link Java_11_parser#package_declaration}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_declaration(Java_11_parser.Package_declarationContext ctx);
	/**
	 * Visit a parse tree produced by {@link Java_11_parser#type_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitType_name(Java_11_parser.Type_nameContext ctx);
	/**
	 * Visit a parse tree produced by {@link Java_11_parser#package_name}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPackage_name(Java_11_parser.Package_nameContext ctx);
}