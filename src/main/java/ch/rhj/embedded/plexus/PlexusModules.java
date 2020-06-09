package ch.rhj.embedded.plexus;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.Module;

@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Inherited
public @interface PlexusModules
{
	public Class<? extends Module>[] value();
}
