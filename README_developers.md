Compiling & versioning
======================

The build process for this project is automated by [Maven](http://maven.apache.org/).

## Compiling

To build for all platforms, run `mvn clean package` in this folder.
Command `mvn clean` deletes `target/` folders lingering from previous invocations of `mvn package` command.

To build for a specific platform, use one of the profiles: `win`, `mac` or `linux`.
To just compile the source code, use `compile` profile (compiled jar will be located in `modules/core/target/`).

For example, `mvn clean package -P win`


## Versioning

To increment project version in Maven's POMs, run `mvn versions:set` -- you will then be prompted to enter the new version string.

When releasing new versions of the program, remember to:

- Increment version:
	- Update version string in main `Superluminal.java` class (`APP_VERSION`)
	- Update version string for Macs in: `skels/mac/Superluminal2.app/Contents/Info.plist`
	- Run `mvn versions:set` to bump version numbers coherently
	- Update version string in: `skels/common/auto_update.xml/`  
		This change then needs to be pushed to the main repository in order to trigger client-side notification to update.

- Update the changelog:
	- Complete changelog: `skels/common/readme_changelog.txt`
	- Brief rundown of important changes in XML format: `skels/common/auto_update.xml`


## Releasing

Releasing a new version of the program generally has the following workflow:

- Commit and push all changes constituting the current version to the repository
- Increment versions (see section [Versioning](https://github.com/kartoFlane/superluminal2/blob/master/README_developers.md#versioning))
- Run `mvn clean package`
- Upload assembled distribution archives, making them available for download (currently: GitHub repository releases)
- Push version increment commit with the message: `Increment version (insert_version_here)`


Dependencies
============

This project depends on the following libraries:

- [JDOM 2.x](http://www.jdom.org/) - XML handling
- [log4j2](http://logging.apache.org/log4j/2.x/) - logging
- [Standard Widget Toolkit](http://www.eclipse.org/swt/) - GUI

Also incorporates the following libraries into the source code to make minute adjustments:

- [FTLDat (part of Slipstream Mod Manager)](http://github.com/Vhati/Slipstream-Mod-Manager)
- [JDeserialize](https://github.com/frohoff/jdeserialize)


Repository
==========

## Directory structure

- `img/`  
	Screenshots.

- `modules/`  
	This directory contains modules of the project's main POM.

	- `core/`  
		This directory contains the POM that compiles the program's source code. The code itself is located in `src/` directory in the main repo.

		- `win32/`, `win64/`, `linux32/`, `linux64/`, `mac/`  
			These directories contain POMs that assemble the platform-specific jars and distribution zip/tar archives.

	- `skels/`  
		Subdirectories of this directory contain files to include in the distributions' archives.

		- `common/`  
		Files to include in all distribution archives.

			- `auto_update.xml`  
				Info about the latest release, downloaded periodically by clients.

		- `win/`, `linux/`, `mac/`  
			System-specific files to include in distribution archives.

		- `exe/`  
			Materials to create superluminal.exe (not part of Maven).

			- Get [Launch4j](http://launch4j.sourceforge.net/index.html)
			- Drag `launch4j_*.xml` onto `launch4jc.exe`.
			- `superluminal.exe` will appear alongside the xml.
			- Drag `superluminal.exe` into `skel_win/`.
			- Run `mvn clean package`.

			The manifest files will be embedded to prevent VirtualStore redirection.  
			[Article: Making Your Application UAC-Aware](http://www.codeproject.com/Articles/17968/Making-Your-Application-UAC-Aware)

	- `src/`  
		The source code of the application

		- `java/`  
			Uncompiled Java files which constitute the editor (package explanation below)

		- `resources/`  
			Various resources that are embedded in the compiled .jar file, and used by the program.


## Source code packages

- `com.kartoflane.common`  
	Collection of miscellaneous utility classes written for other projects, which are useful in the editor.

- `com.kartoflane.common.selfpatch`  
	Base classes and interfaces for the self-patching functionality.

- `com.kartoflane.ftl.floorgen`  
	Provides a `FloorImageFactory` class that generates accurate floor images from ship layout data.

- `com.kartoflane.ftl.layout`  
	Classes representing FTL ship layout data, used by `floorgen`

- `com.kartoflane.superluminal2`  
	Contains the main class.

	- `.components`  
		General purpose classes that are used across the application.

		- `.enums`  
			Enums used in the editor.

		- `.interfaces`  
			Interfaces used in the editor.

	- `.core`  
		Classes that fulfill a central role in the application, like the Cache to request resource handles or the Grid for positioning.

	- `.db`  
		Classes pertaining to the editor's database system -- storage of loaded GameObjects and management of loaded mods.

	- `.events`  
		A rudimentary event system that attempts to facilitate loosely coupled communication between unrelated Controllers.

	- `.ftl`  
		Classes representing the in-game objects that are of interest to this editor.

	- `.mvc`  
		Interfaces for the editor's MVC system.

		- `.controllers`  
			Controller classes. Controllers are the front-end class that the user interacts with. They also provide abstraction for the data held by `Models`.

			- `.props`  
				Controllers for props. Props are MVC entities that are used to break down complex data structures into simpler ones by representing a single aspect of their parent `Controller`. For example, a weapon mount has a direction property, among others - instead of having the `MountController`'s `View` draw the arrow image, a `PropController` is created and attached to the `MountController` to represent the arrow.

		- `.models`  
			Model classes. Models are tha back-end classes that hold data, and make it available via `Controller` classes. In a proper MVC system, there probably should me much more of those... In this editor however, they're used exclusively to hold positioning related data, and link the controller to a `GameObject` from the `.ftl` package, which hold all of the game-related data.

		- `.views`  
			View classes. `Views` are the visual representation of the data held by `Models` and abstracted by `Controllers`.

			- `.props`  
			Views for props.

	- `.selfpatch`  
		Implementation of the self-patching functionality for the editor.

	- `.tools`  
		Classes for the tools used in the editor. Originally they were intended to be pretty flexible and self-contained -- each tool defining the behaviour of the mouse cursor (eg. what happens when the user left-clicks), but in practice most of the actual functionality ended up in the `ui`/`sidebar` classes.

	- `.ui`  
		All classes defining windows or dialogs in the editor, or UI-related widgets. `ShipContainer` and `GibPropContainer` are not actual UI widgets per se, but containers that serve to provide an additional layer of abstraction over the entire MVC system and wrap it up nicely in a single entity.

		- `.sidebar`  
			UI widgets that are containers for several other widgets that, when grouped together, are used to manipulate the currently selected tool or element. These containers are inserted into the sidebar. Originally intended to be freely swappable, but ended up strongly coupled with, and dependent on, the currently selected tool.

			- `.data`  
				Same as above, but for selectable `Controllers` that have modifiable data. Instead of being inserted into the sidebar directly however, they're inserted into the Manipulation Tool's container.

	- `.undo`  
		Classes representing undoable operations in the editor's undo system.

	- `.utils`  
		Various utility classes. Stream-level IO, XML doc interpretation, ship loading and saving are all handled through here.

- `net.vhati`  
	Parts of code from Vhati's [SMM](http://github.com/Vhati/Slipstream-Mod-Manager) that the editor uses.

- `org.unsynchronised`  
	Source code from JDeserialize (link above). Basically unchanged, save for the class names that now follow the proper Java casing scheme. Used to deserialize legacy ship saving format (.shp).
