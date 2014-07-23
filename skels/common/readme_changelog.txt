Changelog

2.0.2 beta:
- Menu buttons' hotkey text is now updated when you modify hotkeys
- Hotkeys can now be unbound
- Added .shp file loading
- Added gibs' angular velocity modification
- Added raw value modification to gibs
- Fixed a somewhat rare bug with system visibility
- Added mount-gib linking
- Reworked hotkey system
- Added search functionality to weapon, drone & augment selection dialogs (Ctrl+F hotkey)
- Added hotkeys to Mod Management: Confirm (Enter), Load (Ctrl+L), Remove (Delete)
- Added undo/redo. Currently undoable operations:
  * Creation & deletion
  * Move (by mouse)
  * Room resize
  * System (un)assignment
  * Door linking

2.0.1 beta:
- Fixed a minor code screw-up that prevented shield, floor and thumbnail images from being saved.
- Fixed some stations being visible when they should not be
- Fixed the corrupt image "bug" -- the editor now detects when you've installed mods with SMM
  while the editor was running, and now automatically reloads the database
- Added keybind modification to the Settings dialog
- Rooms, weapon mounts and gibs can now be reordered via the Ship Overview window -- simply drag them.

2.0.0 beta9:
- Fixed a bug that would prevent weapon/drone slots from saving correctly for enemy ships
- Ship save destination is now on a per-ship basis, instead of being application-wide
- File and folder selection dialogs should now remember their own paths. Some of the file
  selection dialogs are grouped together, eg. all interior images' dialogs use the same path.
- Toggling hangar image for enemy ships now displays the enemy window instead
- Disabled the horizontal offset slider for enemy ships, since it doesn't affect them
- Fixed enemy offset loading & improved enemy optimal offset calculation
- Ship Loader now also remembers previous selection
- Enemy ship images now get saved to 'ships_glow' instead of 'ships_noglow' -- still not sure
  if only one is enough for enemy ship hulls to show up correctly
- Removed unnecessary method calls, reducing ManipulationTool dragging lag by around 33%
- Fixed a bug with database reloading that would crash the editor in any mods were loaded
- Corrected ship saving to only export interior and glow images when the system using them
  is actually assigned
- Fixed a bug with the loading dialog that would cause the editor to crash if two loading
  dialogs were displayed at the same time
- Hiding an object now also deselects it
- Fixed config dialog's contents not wrapping when the window was resized, improved scrolling
- Added artillery loading & saving (no modification yet)
- Added new config option to reset door links when the door is moved
- Reworked Gib Tool to be a part of Images Tool
- Fixed a bug that would cause gib ordering to not be preserved
- Fixed angular velocity not being loaded
- Fixed gib saving
- Added gib modification (WOOO!)
- Added gib image saving
- Fixed shield & interior images (?) being exported for enemy ships
- Slightly reworked & improved the Overview Window, added gibs, and a visibility toggle button

2.0.0 beta8:
- The update dialog now displays a brief list of changes that have been made since the version
  you're using
- Fixed a bug with ship loading that would not link mounts to gibs correctly, causing problems
  when you tried to save the ship
- Weapon/drone/augment selection dialogs now will also scroll to show the last selected item
- Added the ability to change enemy ships' boarding AI, now only gets exported for enemy ships
- Fixed a bug that would not allow to confirm "No Drone List" in drone selection dialog
- Fixed systems not disappearing when hiding rooms
- Done some preliminary work on gibs - can be viewed and moved around with Gib Tool (you have to hide
  all the other elements first - keys 1 through 8 by default). Gibs are not exported yet.

2.0.0 beta7:
- Some of the previously unloadable ships can now be loaded (missing 'max' attribute on
  <crewCount> tag)
- Fixed a bug that would cause systems to be exported evem when the room they've beem
  assigned to has been deleted
- Stations are no longer saved for enemy ships, since they apparently don't affect them anymore
- Station Tool is now disabled for enemy ships
- Some of enemy ships' systems' level caps have been raised to 10
- Reworked the way the editor handles systems to allow multiple artilleries
- Improvied resize detection on Linux environments -- grid should fit the window most of the time now
- Fixed issues with keybinds on Linux -- temporary workaround

2.0.0 beta6a:
- Added verification to image browsers to make sure that the selected file actually exists
- Improved error handling in ship-saving code
- Fixed a minor bug with crew UI that would cause a crash
- Added tooltips to rooms' sidebar
- Changed tooltips to stay until dismissed by the user
- Fixed drone parts and missile amount changes not being applied to the ship
- Fixed 'File > Reload Database' function

2.0.0 beta5:
- Added crew modification
- Weapon/drone/augment dialogs now remember previous selection
- Some more tooltips
- Several minor tweaks, behind-the-scenes changes
- Added a popup for when the user downloaded a wrong version of the editor for their system
- Decoupled room and system drawing logic, systems are drawn on separate layer above rooms

2.0.0 beta4a:
- Fixed a minor oversight that would cause the editor to crash when editing enemy ships

2.0.0 beta4:
- Added Calculate Optimal Offset option - calculates both thick and fine offsets
- Added fine offset modification (HORIZONTAL and VERTICAL properties)
- Added ability to show the hangar image as background, which accurately shows where
  the ship will be positioned in-game.
- Added more tooltips
- Added mouse shortcuts to Weapon Mounting Tool
- Added enemy shield resizing

2.0.0 beta3:
- Fixed weapon mounts with direction NONE being incorrectly saved ('none' instead of 'no')
- Fixed a bug that caused the grid not to be resized properly when the editor was started
- Fixed a minor screw-up that caused the additional weapon/drone slots to be unusable
- Fixed Images Tool's hotkey not working
- Added a crappy image for Images Tool
- Some spinners (ie. numerical fields with up/down arrows) were showing up incorrectly on
  Linux, attempted to fix that by giving them a fixed width
- Added "Show File" button to Image Viewer, which will show the file in the OS' filesystem,
  disabled if it's not applicable for the currently viewed image.
- Wired min and max sector spinners to update the ship's min/max sector values (they were
  having no effect previously)
- Min/max sector tags now get saved before the <systemList> tag
- Editor now also saves the <boardingAI>sabotage</boardingAI> tag
- Fixed DatabaseCore in Mod Management being draggable and thus removeable on some platforms
- Added the option to reload the entire database
- Weapon mounts can now display any weapon as if it were equipped at the mount, without
  actually changing the loadout
- Added info icons to several elements of the UI, that display a short tip when hovered over,
  which describes the setting
- Added ship offset modification

2.0.0 beta2:
- Fixed a crash when resizing the sidebar to occupy the entire window
- Fixed a bug when saving a ship after a new weapon mount has been placed
- Fixed a bug causing new rooms to always have ID -1
- Hopefully fixed a bug where right-clicking on a room to assign system causes a crash
- Fixed a crash when clicking interior images' "Clear" button
- Default images are now loaded when a system is assigned for the first time
- Also removed ability to assign interior images to systems in enemy ships
- Images tab moved from Properties to its own separate "tool"
- Weapon/drone slots now allow up to 8 slots, but show a warning the first time the
  user assigns more than 4 slots

2.0.0 beta1:
- Test release
