The build process for this project is automated by Maven.
  http://maven.apache.org/
  http://docs.codehaus.org/display/MAVENUSER/Getting+Started+with+Maven


To build, run "mvn clean package" in this folder.

To just compile the source code, run "mvn -P compile clean package" in this folder
(compiled code will be located in "modules/core/target")


Explanation of the directory structure:

  "img/"
	Screenshots.

  "modules/"
    This directory contains modules of the project's main POM.
    
	"core/"
	  This directory contains the POM that compiles the program's source code.
	  The code itself is located in "src/" directory in the main repo.

    "{platform}{arch}/"
      These directories contain POMs that assemble the platform-specific jars and
      distribution zip/tar archives.

  "skels/"
    Subdirectories of this directory contain files to include in the
    distributions' archives.
    
    "common/"
      Files to include in all distribution archives.
  
        "auto_update.xml"
          Info about the latest release, downloaded periodically by clients.

    "win/", "linux/" and "mac/"
      System-specific files to include in distribution archives.

    "exe/"
      Materials to create superluminal.exe (not part of Maven).
        - Get Launch4j: http://launch4j.sourceforge.net/index.html
        - Drag "launch4j_*.xml" onto "launch4jc.exe".
        - "superluminal.exe" will appear alongside the xml.
        - Drag superluminal.exe into "skel_win/".
        - Run "mvn clean package".

      The manifest files will be embedded to prevent VirtualStore redirection.
        http://www.codeproject.com/Articles/17968/Making-Your-Application-UAC-Aware



This project depends on the following libraries.
- JDOM 2.x
    http://www.jdom.org/
    (For JavaDocs, look left.)
- log4j2
    http://logging.apache.org/log4j/2.x/
    (For JavaDocs, look left.)
- Standard Widget Toolkit (SWT) GUI library
    http://www.eclipse.org/swt/
    (For JavaDocs, look left.)