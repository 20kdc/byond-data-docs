# INCOMPLETE BUT STRUCTURALLY COMPLETE UP TO V512

Addendum to this disclaimer - I haven't yet found indications of anything going particularly *wrong* when using this information on V514.

But that may simply be due to a lack of feature use (i.e. RHS version differences).

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

When a specific set of executor options are *not* being forced, it's (likely?) possible to write arbitrary comment lines.
For perfect read logic, read lines at the start of the file until you find one without "#", then backtrack a character.
Record all of this and consider it a blob of data that you have absolutely no reason to care about whatsoever (because that's what it is).

It follows (or starts) with a line of the form `world bin v512\n` (at time of writing).

A value which we'll call the "base pointer/key" (which is presumably used so that file-position-based encryption doesn't royally fail for shebang interpreter changes) is set to the file position of the start of this line (the 'w').

This is a 32-bit value that gets downcast as necessary.

This version is the GEN Version.

If it's below 222, information is not available on these versions at this time.
There's also some problem in this document or the current implementation of it regarding versions below 230.

Known versions always follow with one of two line types:

1. A compatibility line of the form `min compatibility v512 468\n` (at time of writing).

2. A compatibility line of the form `min compatibility v222\n` (not checked).

In the first case, the LHS version and RHS version are available.

In the second case, the number is both the LHS version and the RHS version.

Integer types in BYOND data are little-endian, to align with the x86 processor.

A UInt32 follows. This is a set of game flags.

Bit 31 (highest): Another UInt32 follows. (Unsure on meaning.)
Bit 30: LARGE OBJECT IDs

There's also a bunch of unknown stuff. (Seems to default to 832, but this isn't necessary)

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

1. StringID name (this is the actual name, the value of 'type')
2. Nullable ClassID parentType (this is the value of 'parent_type')
3. Nullable StringID objName (this is the value of 'name'. IFL)
4. Nullable StringID description (this is the value of 'description'. IFL)
5. Nullable CacheFileID icon (the 'icon' property)
6. Nullable StringID iconState (the 'icon_state' property)
7. Uint8 direction (the 'direction' property - 2 is a good default)

For GEN Versions >= 307, additionally {

1. Uint8 dmInterface (See: The Annotated Standard Class Hierarchy. IFL).
2. If it's 0x0F (*not* 0xFF!), then a Uint32 follows to replace it.

If the format version is insufficient, this defaults to 1.
To preserve the original file, it's important to preserve the 'long-ness' status separately.
Also note that the default-to-1 behavior is generally a good idea even outside
format version shenanigans.

}

1. StringID text (the 'text' property, might be nullable)

For RHS Versions >= 494, additionally {

1. ObjectID unk
2. Uint16 unk (note: my pet theory is, this involves either step or icon size)
3. Uint16 unk (same)

}

For RHS Versions >= 508, additionally {

1. Uint16 unk
2. Uint16 unk

}

1. Nullable StringID suffix (the 'suffix' property, IFL)
2. (For GEN Versions >= 306, a Uint32, otherwise a Uint8) flags (check The Annotated Standard Class Hierarchy. IFL)
3. Nullable ListID verbList (ID of a list of ProcIDs: verbs)
4. Nullable ListID procList (ID of a list of ProcIDs: procs)
5. Nullable ProcID initializer (initializes an awful lot of stuff. IFL)
6. Nullable ListID initializedVarsList (same format as the Overriding Vars List, so see that subsection. IFL)
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

## Sub-Block 2 (The Mob Type Table)

Like Sub-Block 1, this starts with an ObjectID entry count.

Each entry attaches mob metadata to a `/mob`-derived class.

For each entry:

1. ClassID clazz
2. Nullable ObjectID key (as in `key = "PlayerName"`)
3. Uint8 sightFlags (IFL)
 According to Lego, if this doesn't have 0x80, see\_in\_dark is 2, see\_invisible is based on flag 0x02, and sight\_flags is really 0.
 For reproducibility purposes it's useful to keep these as separate entities.

If sightFlags is >= 0x80:

1. Uint32 sightFlagsEx (IFL)
2. Uint8 seeInDark (IFL)
3. Uint8 seeInvisible (IFL)

## Sub-Block 3 (The String Table)

The string table contains all DMStrings.

As is the pattern so far, there is some amount of entries represented as an ObjectID.

An ongoing 32-bit hash [NQCRC](../algorithms/NQCRC.md) is maintained throughout the string table, initialized to -1.

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

Finally, the string is written, encrypted with [XORJUMP](../algorithms/XORJUMP9.md).

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

1. Nullable StringID path (Full path)
  NOTE: No, this isn't used for inheritance. See below.

}

1. Nullable StringID name (This is the display name for verbs, but *it is also used for inheritance*.)
2. Nullable StringID desc (The description of the verb - thanks to Willox for finding this)
3. Nullable StringID verbCategory (NOTE: Verbs with a null category don't show up in the menu.)
4. Uint8 verbSrcParam (usually 0xFF - thanks to Willox for finding this)
5. Uint8 verbSrcKind (usually 0 - thanks to Willox for finding this, see below)
6. Uint8 flags (See below)
7. If flags has the high bit set, Uint32 unk, Uint8 unk.
8. ListID code (See [Bytecode](./DMB.Bytecode.md) for further information)
9. ListID of VarID locals (note that this isn't nullable)
10. ListID args (note that this isn't nullable) (it's also complicated - VarIDs are involved, but also other stuff)

```
verbSrcKind meanings (thanks to Willox for these!):
0x01: in view
0x02: in oview
0x03: usr.loc
0x05: in range
0x08: in usr
0x20: = usr

flags:

Thanks to Willox for these:
 0x01: hidden
 0x02: src_equal
 0x04: wait_for
 0x20: no_category
 0x40: yes_category

0x80: Has extended flags
```

## Sub-Block 6 (The Var Table)

Like Sub-Block 1, this starts with an ObjectID entry count.

For each entry {

1. Uint8 type
2. Uint32 value
3. StringID name

For further information on the type/value part, see [TypeValue](./DMB.TypeValue.md).

}

If the GEN Version *and* the LHS Version are both >= 512, this has a footer {

1. Nullable ListID-as-UInt32 unk

}

## Sub-Block 7

Like Sub-Block 1, this starts with an ObjectID entry count.

Each entry is a ProcID.

## Sub-Block 8 (The Instance Table)

Like Sub-Block 1, this starts with an ObjectID entry count.

For each entry:

1. Uint8 type
2. Uint32 value
3. Nullable ProcID initializer (used to set per-instance properties)

For further information on the type/value part, see [TypeValue](./DMB.TypeValue.md).
But to be particular, this represents a 

A turf has the values (10, (some class ID), 0xFFFF).

It is important to note that as far as I am aware, most properties of an initializer proc don't matter.

Check [Bytecode](./DMB.Bytecode.md) for more information.

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
4. Nullable ListID of ProcID procs (this contains procs attached to /world)
5. Nullable ProcID globalVariableInitializer (this contains global variable initializers)
6. Nullable StringID domain (this is `world.domain`, a mysterious, seemingly undocumented property - thanks to Willox for finding this)
7. StringID name

If GEN Version < 368 (YES, UNDER) {

1. ObjectID unk

}

1. Uint32 tickTimeMillis (this is `world.tick_lag`, multiplied by 100 - essentially, the time per tick measured in integer milliseconds.)
2. ClassID client

If GEN Version >= 308 {

1. ClassID image

}

1. Uint8 clientLazyEye (this is `client.lazy_eye`, used for camera control - thanks to Willox for finding this)
2. Uint8 clientDir (this is `client.dir` - defaults to 1 (NORTH). - thanks to Willox for finding this)

If GEN Version >= 415 {

1. Uint16 clientControlFreak (this is `client.control_freak`, used to restrict skins and macros - thanks to Willox for finding this)

}

1. Uint8 unk (again)

If GEN Version >= 230 {

1. Nullable StringID clientScript (this is `client.script` *specifically* as a string - thanks to Willox for finding this)

}

If GEN Version >= 507 {

1. Uint16 clientScriptCacheFileCount
2. Array of clientScriptCacheFileCount CacheFileIDs clientScriptCacheFiles (this is every `client.script` file - thanks to Willox for finding this)

}

If GEN Version < 507 (yes, under) {

1. Nullable ObjectID unkSpecial

}

If GEN Version >= 232 {

1. Uint16 unk (Seems to default to 2827, but isn't necessary)

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
2. Uint32 hubNumber (This is where `world.hub` goes when a number is provided - thanks to Willox for finding this)
3. Uint32 gameVersion (`world.version`, not to be confused with the BYOND version - thanks to Willox for finding this)

}

If GEN Version >= 272 {

1. Uint16 cacheLifespan (Best set to 30. `world.cache_lifespan`, in days - thanks to Willox for finding this)
2. Nullable StringID clientCommandText (`client.command_text` - thanks to Willox for finding this)
3. Nullable StringID clientCommandPrompt (`client.command_prompt` (undocumented?) - thanks to Willox for finding this)

}

If GEN Version >= 276 {

1. Nullable StringID hub (this is the `user.game` hub name, i.e. `world.hub`)

}

If GEN Version >= 305 {

1. Nullable StringID channel (`world.channel` ; might not ACTUALLY be nullable ; undocumented but DreamMaker understands it ; seems to be the string "default"?)

}

If GEN Version >= 360 {

1. Nullable CacheFileID skin (DMI and friends - thanks to Willox for finding this)

}

If LHS Version >= 455 {

1. Uint16 iconSizeX (default 32)
2. Uint16 iconSizeY (default 32)
3. Uint16 mapFormat (default 32768 - thanks to Willox for finding this)

}

## Sub-Block 11 (The Cache Files Table)

Like Sub-Block 1, this starts with an ObjectID entry count.

For each entry:

1. Uint32 uniqueID
2. Uint8 typeOrSomething

These correspond to entries in the relevant RSC files.

(The order is swapped. This is known and correct.)

## The Requirements For A DMB File To Be Loadable (Eden)

### Minimum

Honestly, I'm not even sure what the minimum is.
Check the included source at this point, and go down from there.

### Important Things Of Note

BYOND *may* expect strings to have their DM-compiler-generated "word IDs".

That is, strings being compared by-ID to constants without the runtime verifying that they are in fact the correct constants.

### The Annotated Standard Class Hierarchy

It's important to note that some classes are actually BYOND built-in types.

Additionally, some class code is part of BYOND and is always generated for every file.

```
class flags:
 CF_MOB  = 0x0002
 CF_ATOM = 0x0004 Seems to control access to atom procs in some way.
 CF_AREA = 0x0008

'DMIF' is dmInterface, seen as _dm_interface in stddef.dm (see https://github.com/tgstation/FastDMM/blob/master/src/main/resources/stddef.dm )
 (This is a bitfield, but actually attempting to match the flags as described there doesn't always work - atom/movable are always off.)

'TP' is the type when it shows up in the Instance Table.

                        DMIF   FLAG   TP PARENT
/client             = 0x0001 0x0000      0
/datum              = 0x0001 0x0000      0
/sound              = 0x0221 0x0000      /datum
/icon               = 0x0301 0x0000      /datum
/matrix             = 0x0401 0x0000      /datum
/regex              = 0x2001 0x0000      /datum
/dm_filter          = 0x0001 0x0000      /datum
/image              = 0x0041 0x0004 0x3F /datum
/mutable_appearance = 0x4041 0x0004 0x3F /image
/database           = 0x1001 0x0000      /datum
/database/query     = 0x1001 0x0000      /database
/atom               = 0x0001 0x0004 0x0A /datum
/turf               = 0x0001 0x0004 0x0A /atom
/area               = 0x0001 0x000C 0x0B /atom
/atom/movable       = 0x0001 0x0004 0x09 /atom
/obj                = 0x0001 0x0004 0x09 /atom/movable
/mob                = 0x0001 0x0006 0x08 /atom/movable
```

There's some additional notes worth keeping in mind:

1. You may notice /world isn't here. That's because /world isn't a class.
2. Turfs and atoms are one and the same.
3. Objs and movable atoms are also one and the same.

