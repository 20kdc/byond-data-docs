# DMB Bytecode Format

Sorry, I don't actually have any documentation on this yet.

However, MCHSL & SpaceManiac's project, extools, is known to contain a disassembler for this.

## PUSHVAL

PUSHVAL's argument is the usual (TypeValue)[./DMB.TypeValue.md] pair.

However, due to the 16-bit nature of lists (normally), there's a slight modification to the encoding.

For floats (type 42), and SPECIFICALLY floats, they have 2 value entries rather than 1.
These are laid out large word first (big-endian), even though the individual list byte pairs remain little-endian.
So the actual on-disk byte order, with 0 being MSB, is 1032.

The way you will actually receive it after your (assumed) list reader abstraction, it'll just be the high word followed by the low word.

I don't know what the form is when large object IDs are enabled, but assume it's unchanged.

## Possible Extools Errata

1. CALLNR (0x2A) is really more like another form of GETVAR.
2. 0xC8 opcode; appears to have 1 arg
3. 0x18 opcode; appears to be similar to "<<" ; uses 6 stack slots, *might* return something
4. Access modifier 0xFFCD (65485) is actually "usr"
5. `SRC_PROC_SPEC` access modifier has the important bits commented out, breaking everything
6. SUBVAR right-hand-side parsing is completely wrong - the SUBVAR is just two accesses concatenated.
7. 0xDB opcode; appears to be `text2ascii`, no args
8. 0xDC opcode; appears to be `ascii2text`, no args
9. 0x29 type id; appears to be `type path for modified type`. Thanks to the Red Book for cluing me in on the existence of these. My name for this type is `INSTANCE_TYPEPATH`. Takes an instance table ID
10. 0x3F type id; `IMAGE_TYPEPATH`, takes a class ID like the others

The following code goes through a few different calls and accesses:

```
/datum
	var/datum/a
	proc/universally_trouble()
		usr.a.a.a.a.a.a.a.a.universally_trouble()
		universally_trouble()
		a.universally_trouble()
		universally_carrot()

proc/universally_carrot()

```

## Instance Initializer Procs

Instance initializer procs are theoretically not special.

However, they have relatively special syntax, being part of DMM files.

As such, they seem to only contain the following opcodes (as named by extools dmdism):

PUSHI (80, (unsigned int))

PUSHVAL (96, (type), (val), (only present if type = 42: val ex.))

SETVAR src.? (52, 65500, 65486, (StringID))

END (0)

These are always in the form PUSHVAL or PUSHI followed by SETVAR, with the list terminated with END.

(I haven't yet seen any others, but that doesn't necessarily mean they don't exist.)

