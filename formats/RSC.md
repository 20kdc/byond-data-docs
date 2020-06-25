# RSC (*.rsc) Format

Status on documentation: Complete reading-wise but checksum details not worked out yet for writing. `byond.rsc` considered irrelevant *unless the encryption crosses over into protocol research*.

## Details

For the outer structure, see (RAD)[./RAD.md].

Values remain little-endian as with DMB.

The inner structure identifies entry components and is the actual data.

1. Uint8 typeOrSomething (corresponds to Cache File typeOrSomething in DMB)
2. Uint32 uniqueID (corresponds to Cache File uniqueID in DMB) - *this is probably also a checksum / hash / ??? of the data and/or entry, but details are unknown*
3. Uint32 timestamp (seconds since the Unix epoch, UTC)
4. Uint32 originalTimestamp (this is the modification time *of the imported file* (presumably to determine which files to update). In `byond.rsc`, this is 0.)
5. Uint32 dataLength
6. Zero-terminated string filename
7. Array of dataLength Uint8s data

The data in these entries is *usually* unencrypted unless you're poking `byond.rsc`.

DreamDaemon does not understand encrypted entries.

Regarding the mapping to the DMB file: The order is swapped. This is known and correct.

## Entry Types

```
0x01: MIDI file.
0x02: OGG or WAV file.
0x03: DMI PNG file.
0x06: Plain PNG file.
0x0B: Plain JPG file.

The flag 0x80 can be added to any of these to indicate encryption (details unknown).

```
