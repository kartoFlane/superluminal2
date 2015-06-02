=========================================================
The build process for this project is automated by Maven.
  http://maven.apache.org/
  http://docs.codehaus.org/display/MAVENUSER/Getting+Started+with+Maven


To build for all platforms, run "mvn package" in this folder.
Command "mvn clean" deletes "target/" folders lingering from previous invocations
of "mvn package" command.

To build for a specific platform, use one of the profiles: 'win', 'mac' or 'linux'.
To just compile the source code, use 'compile' profile.
(compiled jar will be located in "modules/core/target")
For example, "mvn package -P win"

================================================
This project depends on the following libraries:
- JDOM 2.x
    http://www.jdom.org/
    (For JavaDocs, look left.)
- log4j2
    http://logging.apache.org/log4j/2.x/
    (For JavaDocs, look left.)
- Standard Widget Toolkit (SWT) GUI library
    http://www.eclipse.org/swt/
    (For JavaDocs, look left.)

Also incorporates the following libraries into the source code to make minute adjustments:
- FTLDat (part of Slipstream Mod Manager)
    http://github.com/Vhati/Slipstream-Mod-Manager
- jdeserialize
    http://code.google.com/p/jdeserialize/


====================================================
Explanation of the repository's directory structure:

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

  "src/"
    The source code of the application
    
    "java/"
      Uncompiled Java files which constitute the editor (package explanation below)
    
    "resources/"
      Various resources that are embedded in the compiled .jar file, and used by
      the program.


===============================================================
General explanation of source code packages and their contents:

  "com.kartoflane.ftl.floorgen"
    A self-contained pseudo-library that generates accurate floor images from ship layout data.

  "com.kartoflane.superluminal2"
    Contains the main class.
    
    ".components"
      General purpose classes that are used across the application.
    
      ".enums"
        Enums used in the editor.
      
      ".interfaces"
        Interfaces used in the editor.
      
    ".core"
      Classes that fulfill a central role in the application, like the Cache to
      request resource handles, game object Database, or the Grid for positioning.

    ".events"
      A rudimentary event system that attempts to facilitate loosely coupled
      communication between unrelated Controllers.

    ".ftl"
      Classes representing the in-game objects that are of interest to this editor.

    ".mvc"
      Interfaces for the editor's MVC system.
      
      ".controllers"
        Controller classes. Controllers are the front-end class that the user
        interacts with. They also provide abstraction for the data held by Models.

        ".props"
          Controllers for props. Props are MVC entities that are used to break
          down complex data structures into simpler ones by representing a single
          aspect of their parent controller.
          Example: a weapon mount has a direction property, among others. Instead of
          having the MountController's View draw the arrow image, a PropController
          is created and attached to the MountController to represent the arrow.

      ".models"
        Model classes. Models are tha back-end classes that hold data, and make
        it available via Controller classes.
        In a proper MVC system, there probably should me much more of those...
        In this editor however, they're used exclusively to hold positioning 
        related data, and link the controller to a GameObject from the ".ftl" package,
        which hold all of the game-related data.

      ".views"
        View classes. Views are the visual representation of the data held by Models
        and abstracted by Controllers.

        ".props"
          Views for props.

    ".tools"
      Classes for the tools used in the editor. Originally they were intended to be
      pretty flexible and self-contained -- each tool defining the behaviour of the
      mouse cursor (eg. what happens when the user left-clicks), but in practice most
      of the actual functionality ended up in the UI/Sidebar classes.

    ".ui"
      All classes defining windows or dialogs in the editor, or UI-related widgets.
      ShipContainer and GibPropContainer are not actual UI widgets per se, but
      containers that serve to provide an additional layer of abstraction over the
      entire MVC system and wrap it up nicely in a single entity.
    
      ".sidebar"
        UI widgets that are containers for several other widgets that, when grouped
        together, are used to manipulate the currently selected tool or element.
        These containers are inserted into the sidebar.
        Originally intended to be freely swappable, but ended up strongly coupled with,
        and dependent on, the currently selected tool.
        
        ".data"
          Same as above, but for selectable Controllers that have modifiable data.
          Instead of being inserted into the sidebar directly however, they're
          inserted into the Manipulation Tool's container.

    ".undo"
      Classes representing undoable operations in the editor's undo system.

    ".utils"
      Various utility classes. Stream-level IO, XML doc interpretation, ship loading and
      saving are all handled through here.

  "net.vhati"
    Parts of code from Vhati's SMM that the editor uses.

  "org.unsynchronised"
    Source code from JDeserialize (link above). Basically unchanged, save for the class
    names that now follow the proper Java casing scheme.
