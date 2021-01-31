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
}