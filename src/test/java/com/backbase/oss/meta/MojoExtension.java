// package com.backbase.oss.meta;
//
// import java.lang.reflect.Constructor;
// import org.codehaus.plexus.DefaultPlexusContainer;
// import org.codehaus.plexus.PlexusContainer;
// import org.codehaus.plexus.PlexusContainerException;
// import org.junit.jupiter.api.extension.ExtensionContext;
// import org.junit.jupiter.api.extension.TestInstanceFactory;
// import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
// import org.junit.jupiter.api.extension.TestInstantiationException;
//
// public class MojoExtension implements TestInstanceFactory {
//
//
// @Override
// public Object createTestInstance(TestInstanceFactoryContext fc, ExtensionContext xc)
// throws TestInstantiationException {
//
// try {
// final Class<?> tc = fc.getTestClass();
// final Constructor<?> ct = tc.getConstructor();
//
// ct.setAccessible(true);
//
// final Object test = ct.newInstance();
//
// container().addComponent(test, tc.getName());
//
// return test;
// } catch (final Exception e) {
// throw new TestInstantiationException("", e);
// }
// }
//
// private PlexusContainer container() throws PlexusContainerException {
// return new DefaultPlexusContainer();
// }
//
// }
