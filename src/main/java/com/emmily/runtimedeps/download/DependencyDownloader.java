package com.emmily.runtimedeps.download;

import com.emmily.runtimedeps.dependency.FileType;
import com.emmily.runtimedeps.dependency.MavenDependency;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public interface DependencyDownloader {

  File downloadFile(MavenDependency mavenDependency,
                    FileType fileType) throws IOException;


  Collection<File> downloadFiles(MavenDependency mavenDependency,
                                 FileType... fileTypes) throws IOException;

  Collection<File> downloadAll(MavenDependency mavenDependency) throws IOException, SAXException;

}
