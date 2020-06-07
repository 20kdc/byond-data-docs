# MOSTLY COMPLETE

## Outer Structure

An RSC file is actually a concatenated list of RSC entries.

It has no encryption whatsoever, just verification.

Values are little-endian as with DMB.

The outer entry structure is:

1. Uint32 entryLength
2. Uint8 unk
3. Array of entryLength Uint8s entryContent

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

