package net.splitcells.dem.testing.annotations;

import net.splitcells.dem.lang.annotations.JavaLegacyArtifact;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static net.splitcells.dem.testing.TestTypes.UNIT_TEST;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Tag(UNIT_TEST)
@Test
@JavaLegacyArtifact
public @interface UnitTest {
}
