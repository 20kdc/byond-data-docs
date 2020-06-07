# INCOMPLETE BUT STRUCTURALLY COMPLETE UP TO V512

## Taxonomy

An "ObjectID" is a generic object ID which there isn't details for yet.

These all use a format dependent on a flag in the file header.

A "ClassID" is a reference into the Class Table.

A "MobTypeID" is a reference into the Mob Types Table.

A "StringID" is a reference into the String Table.

A "ListID" is a reference into the List Table.

A "ProcID" is a reference into the Proc Table.

A "InstanceID" is a reference into the Instance Table.

A "CacheFileID" is a reference into the Cache Files Table.

A lot of these IDs allow 0xFFFF to mean "none". (Even if LARGE OBJECT IDs is set, when it would logically be 0xFFFFFFFF - if making a DMB, have fun with that)

These will be referred to as "nullable" IDs.

There are sometimes references to IDs which aren't actually in ObjectID form.

These are in the form "ClassID-as-Uint32" (i.e. a ClassID in the form of a Uint32)

## The Three Wise Version Numbers

There are 3 version numbers for a DMB file.

There is the GEN Version, the LHS Version, and the RHS Version.

All of these are important to loading a DMB file for reasons I do not understand.

Why it is not a single version number I do not understand.

## Header

The DMB file format, for the grand total of 3 plain ASCII lines it has, uses Unix newlines.

The DMB file format starts with an optional "shebang line" ("#!") for Unix executable interpreter business.

A value which we'll call the "base pointer/key" (which is presumably used so that file-position-based encryption doesn't royally fail for shebang interpreter changes) is set to the file position at this point.

This is a 32-bit value that gets downcast as necessary.

It follows (or starts) with a line of the form `world bin v512\n` (at time of writing).

This version is the GEN Version.

If it's below 222, information is not available on these versions at this time.

Known versions always follow with one of two line types:

1. A compatibility line of the form `min compatibility v512 468\n` (at time of writing).

2. A compatibility line of the form `min compatibility v222\n` (not checked).

In the first case, the LHS version and RHS version are available.

In the second case, the number is both the LHS version and the RHS version.

Integer types in BYOND data are little-endian, to align with the x86 processor.

A UInt32 follows. This is a set of game flags.

Bit 31 (highest): Another UInt32 follows. (Unsure on meaning.)
Bit 30: LARGE OBJECT IDs

It's important to note that ObjectID is Uint16 normally and Uint32 if LARGE OBJECT IDs is set.

For analysis reasons, it's important to note that LARGE OBJECT IDs did not exist until at *least* after v368.

## Sub-Block 0 (The Grid)

Next, three UInt16s follow: Width, Height, Z-Levels.

The arrangement of the grid is as you would expect:

1. The outer loop is the per-Z-Level loop.
2. The next inner loop is the per-row loop.
3. The next next inner loop is per-cell loop.

However, unlike what you would expect, Y coordinates in BYOND go upwards and coordinates start at 1.

The part where coordinates start at 1 is not preserved in the file, but Y coordinates going upwards is.

This data is run-length-compressed, and made up of the following 'groups':

1. Nullable InstanceID turfID
2. Nullable InstanceID areaID
3. Nullable ListID additionalTurfIDs (a list of InstanceIDs)
4. Uint8 (Amount of copies. This is NOT incremented, 0x00 is presumably invalid.)

As far as I can tell, the DMM order is: additionalIDs, turfID, areaID.

## Total Size Of All Strings In The File

1. A Uint32 containing the total size of all strings in the file, including null terminators.

If you get weird "protomem"-related errors, it's this field.

## Sub-Block 1 (Classes)

This is the set of classes.

This starts with an ObjectID, which is the amount of entries.

Each class has:

1. StringID name
2. Nullable ClassID parent
3. Nullable StringID nameLastFragment
4. ObjectID unk
5. Nullable CacheFileID icon (the 'icon' property)
6. Nullable StringID iconState (the 'icon_state' property)
7. Uint8 unk

For GEN Versions >= 307, additionally,
 another Uint8. If it's 0x0F (*not* 0xFF!), then a Uint32 follows to replace it. Unknown.
If the format version is insufficient, this defaults to 1.

1. StringID text (the 'text' property, might be nullable)

For RHS Versions >= 494, additionally {

1. ObjectID unk
2. Uint16 unk
3. Uint16 unk

}

For RHS Versions >= 508, additionally {

1. Uint16 unk
2. Uint16 unk

}

1. ObjectID unk
2. (For GEN Versions >= 306, a Uint32, otherwise a Uint8) unk
3. Nullable ListID verbList (ID of a list of ProcIDs: verbs)
4. Nullable ListID procList (ID of a list of ProcIDs: procs)
5. Array of 2 ObjectIDs unk
6. Nullable ListID definingVarList (see relevant subsection)

For GEN Versions >= 267, additionally {

1. Float32 layer

}

For RHS Versions >= 500, additionally {

1. Uint8 hasFloats, but really a boolean
2. If hasFloats, Array of 6 Float32s

}

For RHS Versions >= 509, additionally {

1. Uint8 hasEvenMoreFloats, really a boolean
2. If hasEvenMoreFloats, Array of 20 Float32s

}

For GEN Versions >= 306, additionally {

1. Nullable ListID overridingVarList (see relevant subsection)

}

### Defining Var List

The Defining Var List is a set of key/value pairs implicitly terminated.

The Defining Var List uses a VarID key (the actual var).

The values are flags.

0x0001 means "global".

0x0002 means "const". This implies "global" and is always seen with "global".

0x0004 means "tmp".

### Overriding Var List

The Overriding Var List is a set of key/value pairs implicitly terminated.

The Overriding Var List uses a StringID key (the name of the var to be overridden).

This is followed with a type value (see The Var Table for further information on the general concepts).

Details on what happens for large-object-IDs ARE NOT CERTAIN.

But specifically for 16-bit object IDs and float values, the value is written in two pieces, 'semi-big-endian'.

So `225, 42, 16896, 0` means `[string 225] = float 32.0`.

### A Minor Warning

It's important to note that some classes are actually BYOND built-in types.

Additionally, some class code is part of BYOND and is always generated for every file.

## Sub-Block 2 (The Mob Type Table)

Like Sub-Block 1, this starts with an ObjectID entry count.

Each entry attaches mob metadata to a `/mob`-derived class.

For each entry:

1. ClassID clazz
2. Nullable ObjectID key (as in `key = "PlayerName"`)
3. Uint8 hasFancyStuff

If hasFancyStuff is >= 0x80:

1. Uint32 unk
2. Uint8 unk
3. Uint8 unk

## Sub-Block 3 (The String Table)

The string table contains all DMStrings.

As is the pattern so far, there is some amount of entries represented as an ObjectID.

An ongoing 32-bit hash (NQCRC)[../algorithms/NQCRC.md] is maintained throughout the string table, initialized to -1.

For each entry {

The entries use encrypted lengths, and encrypted data - this is where the "base pointer/key" is involved.

Lengths are Uint16 XOR'd with the file position of the value minus the "base pointer/key".

The entry length is prefixed over and over with (encrypted) 0xFFFF to reach the required total.

The 0xFFFF prefix adds 0xFFFF to the length total, and then reads another length (which can itself be 0xFFFF).

Otherwise the final length value is added to the total and length reading stops.

With official software, if the string length happens to be exactly equal to 0xFFFF, it doesn't write 0xFFFF 0x0000, it just writes 0xFFFF.

```
#define F "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
#define G F+F
#define H G+G
#define I H+H
#define J I+I
#define K J+J
#define L K+K
#define M L+L
#define N M+M
#define O N+N
#define P O+O

/mob
    text = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"+F+G+H+I+J+K+L+M+N+O+P
```

Finally, the string is written, encrypted with (XORJUMP9)[../algorithms/XORJUMP9.md].

The key is again the file position (at string start) minus the "base pointer/key".

Strings, including empty ones, are hashed - including the terminating null byte, which is *not* actually present.

}

Importantly, after the entries, there is a footer.

For GEN Versions >= 468 {

1. Uint32 hash (This contains the result of the ongoing hash for verification)

}

### String Contents And Their Escaping

DMB has a number of very complex string escaping rules.


## Sub-Block 4 (The List Table)

Like Sub-Block 1, this starts with an ObjectID entry count.

This doesn't really have a set content format; it's a generic list container for other bits to use for dynamically sized lists.

It's guaranteed to contain only lists of ObjectIDs, but that's as far as guarantees go.

For each entry:

1. Uint16 subCount
2. Array of subCount Nullable ObjectIDs subEntries

## Sub-Block 5 (The Proc Table)

Like Sub-Block 1, this starts with an ObjectID entry count.

For each entry:

For GEN Versions >= 224 OR if large object IDs are on {

1. Nullable StringID name (Full path)

}

1. Nullable StringID displayName (the term 'displayName' here is particularly important for verbs)
2. Array of 2 ObjectIDs unk
3. Uint8 unk
4. Uint8 unk
5. Uint8 x
6. If x has the high bit set, Uint32 unk, Uint8 unk.
7. ListID code (See (Bytecode)[./DMB.Bytecode.md] for further information)
8. ListID of VarID locals
9. ListID args (but it's complicated - VarIDs are involved, but also other stuff)

## Sub-Block 6 (The Var Table)

Like Sub-Block 1, this starts with an ObjectID entry count.

For each entry {

1. Uint8 type
2. Uint32 value
3. StringID name

}

Type is a BYOND type number (this type numbering is shared with the protocol). Of these, at least the following are valid:

- 0 (null, value ignored)
- 6 (string, as StringID)
- 42 (float, value should be transmuted from int to float)

If the GEN Version *and* the LHS Version are both >= 512, this has a footer {

1. Nullable ListID-as-UInt32 unk

}

## Sub-Block 7

Like Sub-Block 1, this starts with an ObjectID entry count.

Each entry is a ProcID.

## Sub-Block 8 (The Instance Table)

Like Sub-Block 1, this starts with an ObjectID entry count.

For each entry:

1. Uint8 unk
2. ClassID-as-Uint32 clazz (the actual class)
3. Nullable ProcID initializer (used to set per-instance properties)

A turf has the values (10, (some class ID), 0xFFFF).

It is important to note that as far as I am aware, most properties of an initializer proc don't matter.

Check the DMB.Bytecode.md file for more information.

## Sub-Block 9 (Map Additional Data)

This attaches instances to the map.

Uint32, total objects.

For each object:

1. Uint16 offset

That is the offset in tiles (based on the same universal map index logic as for the map itself) from the last object.
If there is no last object, the start of the map.

2. Nullable InstanceID instance

It is reasonable to assume that null instances are used to pad out long empty regions of space.
(i.e. they don't mean anything)

## Sub-Block 10 (Standard Object IDs)

1. Nullable MobTypeID mob
2. Nullable ClassID turf
3. Nullable ClassID area
4. Array of 3 ObjectIDs unk
5. StringID name

If GEN Version < 368 (YES, UNDER) {

1. ObjectID unk

}

1. Uint32 unk
2. ClassID client

If GEN Version >= 308 {

1. ClassID image

}

1. Uint8 unk
2. Uint8 unk (yes, another)

If GEN Version >= 415 {

1. Uint16 unk

}

1. Uint8 unk (again)

If GEN Version >= 230 {

1. ObjectID unk

}

If GEN Version >= 507 {

1. Uint16 evenMoreCount
2. Array of evenMoreCount ObjectIDs evenMore

}

If GEN Version < 507 (yes, under) {

1. Nullable ObjectID unkSpecial

}

If GEN Version >= 232 {

1. Uint16 unk

}

If GEN Version >= 235 && GEN Version < 368 {

1. Uint16 unk

}

If GEN Version >= 236 && GEN Version < 368 {

1. Uint16 unk

}

If GEN Version >= 341 {

1. Nullable StringID hubPasswordHashed (this is the 'hashed' hub password)

}

If GEN Version >= 266 {

1. Nullable StringID serverName
2. Array of 2 Uint32s unk

}

If GEN Version >= 272 {

1. Uint16 unk
2. Array of 2 ObjectIDs unk

}

If GEN Version >= 276 {

1. Nullable StringID hub (this is the `user.game` hub name)

}

If GEN Version >= 305 {

1. ObjectID unk

}

If GEN Version >= 360 {

1. ObjectID unk

}

If LHS Version >= 455 {

1. Uint16 iconSizeX (default 32)
2. Uint16 iconSizeY (default 32)
3. Uint16 unk (default 32768)

}

## Sub-Block 11 (The Cache Files Table)

Like Sub-Block 1, this starts with an ObjectID entry count.

For each entry:

1. Uint32 uniqueID
2. Uint8 typeOrSomething

These correspond to entries in the relevant RSC files.

(The order is swapped. This is known and correct.)
