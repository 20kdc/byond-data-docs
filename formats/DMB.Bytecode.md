# DMB Bytecode Format

Sorry, I don't actually have any documentation on this yet.

However, MCHSL & SpaceManiac's project, extools, is known to contain a disassembler for this.

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

