# Changelog

## 1.6.5
### Changed
- The original mod file is now downloaded automatically on first start (needs ArchaicLib 1.42.0+): sources and mirrors are listed in `config/archaiclib/originals/` and can be edited there. If the download fails, a banner on the main menu links to the manual download.

## 1.6.4
### Changed
- Performance: uses ArchaicLib 1.40.0 FastRandom instead of java.util.Random, plus the 1.40.0 cached block-state / allocation-free legacy world calls. Requires ArchaicLib 1.40.0+.

## 1.6.3
- **No original assets are bundled any more.** Textures are read at runtime from your own copy of the original Villager Mincer in `.minecraft/archaic_originals/` (via ArchaicLib).
- License changed to HE-RSAL 1.1; source published on GitHub.

## 1.6.2
First release of the 1.12.2 port (Cleanroom Loader). Based on Villager Mincer for 1.6.2.
