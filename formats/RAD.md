# Random Access Data format

This is the format behind the RSC and savefile formats.

The idea appears to be for the kind of arbitrary-access writing needed for maintaining `byond.rsc` or modifying savegames.

It's actually a concatenated list of entries.

Values are little-endian as with DMB.

The outer entry structure is:

1. Uint32 entryLength
2. Uint8 valid (0x01 for valid entries, 0x00 for invalid entries.)
3. Array of entryLength Uint8s entryContent

