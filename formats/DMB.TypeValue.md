# DMB Type/Value Pairs

Type is a BYOND type number (this type numbering is shared with the protocol). Of these, at least the following are valid:

(this is more-or-less collated from extools/dmdism)

- 0 (null, value ignored)
- 6 (string, as StringID)
- 8 (/mob class, as MobTypeID)
- 9 (/atom/movable class, as ClassID)
- 10 (/atom class, as ClassID)
- 11 (/area class, as ClassID)
- 32 (/datum class, as ClassID)
- 40 (/list class, value ignored)
- 42 (float, value should be transmuted from int to float)
- 59 (/client class, as ClassID)
- 63 (/image class, as ClassID)

Regarding classes, it's important to note that the 'type of class' decides the underlying native type that is created.

It's NOT based on inheritance - inheritance is used to decide the BYOND type number, not the other way around.

It's NOT based on flags - these seem to be for the object runtime's sake (i.e. which native procs, such as Move, exist)

This can allow things like the world's "client" class always being instantiated as a /client derivative even if it isn't one,
as the instantiation of the world's "client" class is done knowing the target will always be a BYOND client.

The DM compiler's analysis of this appears to completely stop at the first "native type" it sees.

