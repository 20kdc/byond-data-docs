# MOSTLY COMPLETE

## Outer Structure

An RSC file is actually a concatenated list of RSC entries.

It has occasional encryption, but only in files you aren't supposed to poke at.

Values are little-endian as with DMB.

The outer entry structure is:

1. Uint32 entryLength
2. Uint8 unk (I've only seen the value 0x01 for this)
3. Array of entryLength Uint8s entryContent

This is the same outer structure as with (SAV)[./SAV.md] - and it may be the same.

## Entry Content

The outer structure allows skipping through entries easily.

The inner structure identifies entry components and is the actual data.

1. Uint8 typeOrSomething (corresponds to Cache File typeOrSomething in DMB)
2. Uint32 uniqueID (corresponds to Cache File uniqueID in DMB)
3. Array of 8 Uint8s unk
4. Uint32 dataLength
5. Zero-terminated string filename
6. Array of dataLength Uint8s data

Regarding the mapping to the DMB file: The order is swapped. This is known and correct.

