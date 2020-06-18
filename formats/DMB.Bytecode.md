# DMB Bytecode Format

Sorry, I don't actually have any documentation on this yet.

However, MCHSL & SpaceManiac's project, extools, is known to contain a disassembler for this.

## Possible Extools Errata

1. CALLNR (2A) is really more like another form of GETVAR.
2. C8 opcode; appears to have 1 arg
3. 18 opcode; appears to be similar to "<<" ; uses 6 stack slots, *might* return something
4. Access modifier 0xFFCD (65485) is actually "usr"
5. `SRC_PROC_SPEC` access modifier has the important bits commented out, breaking everything
6. SUBVAR right-hand-side parsing is completely wrong - the SUBVAR is just two accesses concatenated.

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

