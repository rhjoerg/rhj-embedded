package ch.rhj.embedded.maven;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import ch.rhj.embedded.plexus.WithPlexus;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
@WithPlexus
@ExtendWith(MavenExtension.class)
public @interface WithMaven
{
}
