# MOSTLY COMPLETE

## Outer Structure

A savefile is a concatenated set of savefile entries.

Values are little-endian as with DMB.

Each entry has the structure:

1. Uint32 entryLength
2. Uint8 unk (I've only seen the value 0x01 for this)
3. Array of entryLength Uint8s entryContent

This is the same outer structure as with (RSC)[./RSC.md] - and it may be the same.

## Entry Content

The outer structure allows skipping through entries easily.

The inner structure identifies entry components and is the actual data.

There seem to be multiple different sub-types of entry content.

In particular, the first entry contains some sort of header information.

The second entry is for the default key, even if the savefile doesn't have one.

```
entry 1: 00 02 00 00 27 01 00 00 00 00 00 00 00 00 00 00
        |?generator?|?mincompat?| ?Unknown? | ?Unknown? |

entry 2: 00 00 00 00 ff ff ff ff 00 00 00 00 00
        | index     | parent    |kL|dataLength |
```

Otherwise, data entries follow this general format:

1. Uint32 index (starts at 0)
2. Uint32 parent (-1, unless the entry has a parent, in which case it is the parent entry's index)
3. Uint8 keyLength
4. Array of keyLength Uint8s - (XORJUMP9)[../algorithms/XORJUMP9.md] encrypted, key 0x53
5. Uint32 dataLength
6. Array of dataLength Uint8s - (XORJUMP9)[../algorithms/XORJUMP9.md] encrypted, key 0x3A

## Entry Value Formats

Savefile entry values are lists - the amount of values is implied by the total size of the blob, as split into these values.

Value contents will be shown in decrypted form.

They use a type/value system, but it's a different one to the one used elsewhere.

An important note is that it's very possible for empty entries to show up.

They're used for things such as parenting.

```
float:
 04 ** ** ** **
 04 00 00 00 40 = 2.0

string:
 01 lL lH (bytes...)
 01 04 00 41 41 41 41 = "AAAA"

class reference:
 03 lL lH (bytes...)
 03 0B 00 2F 6D 6F 62 2F 63 61 6E 61 72 79 = /mob/canary

object reference:
 0B lL lH (bytes...)
 0B 04 00 2F 2F 2E 30 = object("//.0") (this refers to a key of ".0" - see objects)

```

### Objects

Objects don't really have their own type. They get a blank entry with sub-entries containing the details.

The `type` sub-entry is a class reference.
Other sub-entries are equivalent to their corresponding fields.
Only changed entries are saved.

### Relation to the textual format

The relation between the binary format and the text format is incredibly literal.

Given the following in the textual format:
```
. = object("//.0")
.0
	type = /mob/canary
	fluffiness_text = "This canary is extremely fluffy."
```

The savefile that it was generated from contained:
```
header entry
 unk: 01
 raw: 00 02 00 00 27 01 00 00 00 00 00 00 00 00 00 00

data entry
 unk: 01
 raw: 00 00 00 00 ff ff ff ff 00 07 00 00 00 31 47 4c 7a 71 49 40
  key: 
  dat: 0b 04 00 2f 2f 2e 30
  dac: .  .  .  /  /  .  0  

data entry
 unk: 01
 raw: 01 00 00 00 00 00 00 00 02 7d 6c 00 00 00 00
  key: .0
  dat:
  dac: 

data entry
 unk: 01
 raw: 02 00 00 00 01 00 00 00 04 27 25 15 0b 0e 00 00 00 39 48 4c 7a 33 08 12 56 e1 ea fa fc d4 d6
  key: type
  dat: 03 0b 00 2f 6d 6f 62 2f 63 61 6e 61 72 79
  dac: .  .  .  /  m  o  b  /  c  a  n  a  r  y  

data entry
 unk: 01
 raw: 03 00 00 00 01 00 00 00 0f 35 30 10 08 11 e9 e7 f7 e8 d7 f2 c2 da b0 a5 23 00 00 00 3b 63 4c 01 36 0e 03 59 e1 ea fa fc d4 d6 98 a8 b9 f3 b9 9d 9a 85 65 64 77 77 5d 0d 50 53 3d 37 3c 1a 42
  key: fluffiness_text
  dat: 01 20 00 54 68 69 73 20 63 61 6e 61 72 79 20 69 73 20 65 78 74 72 65 6d 65 6c 79 20 66 6c 75 66 66 79 2e
  dac: .     .  T  h  i  s     c  a  n  a  r  y     i  s     e  x  t  r  e  m  e  l  y     f  l  u  f  f  y  .  
```

