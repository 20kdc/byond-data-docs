# DMB Type/Value Pairs

Type is a BYOND type number (this type numbering is shared with the protocol). Of these, at least the following are valid:

- 0 (null, value ignored)
- 6 (string, as StringID)
- 8 (mob type path, as MobTypeID)
- 9 (movable atom type path, as ClassID)
- 10 (atom type path, as ClassID)
- 11 (area type path, as ClassID)
- 42 (float, value should be transmuted from int to float)

