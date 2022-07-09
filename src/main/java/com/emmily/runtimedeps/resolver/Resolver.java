package com.emmily.runtimedeps.resolver;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Collection;

public interface Resolver<T, S> {

  String UNRESOLVED = "$_![UNRESOLVED]!_$";

  T resolve(S source) throws IOException, SAXException;

}
