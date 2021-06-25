package net.splitcells.dem.lang;

import java.util.function.Function;

import static net.splitcells.dem.utils.ConstructorIllegal.constructorIllegal;

public class Lambdas {
    private Lambdas() {
        throw constructorIllegal();
    }

    public static <P, R> Function<P, R> describedFunction(Function<P, R> arg, String description) {
        return new Function<P, R>() {

            @Override
            public R apply(P p) {
                return arg.apply(p);
            }
            
            @Override
            public String toString() {
                return description;
            }
        };
    }
}
