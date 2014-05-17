Changelog

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