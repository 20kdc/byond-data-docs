# DMI files (*.dmi)

Note: This documentation is likely less complete. I'm generally focusing on stuff that hasn't already been documented.
This DMI information is included as an attempt at completeness and should hopefully be enough to get something working.

DMI files are PNG files.
They contain nothing in them that is technically non-standard.

However, they contain a zTXt chunk: http://libpng.org/pub/png/spec/1.2/PNG-Chunks.html#C.zTXt

This has the keyword "Description"; ExifTool is able to detect this, see: https://exiftool.org/TagNames/PNG.html#TextualData

So it's not absolutely necessary to have a specialized editor in order to perform editing of DMI files.

If you're in a particular hurry and willing to use external commands, `exiftool -j images.dmi` will output the DMI in the `Description` field of the first (`[0]`) object in the array (which is the root of the output).

## DMI Metadata Format

So presumably you've extracted the description string and you want a definitive idea of format.
It's newline-delimited (basically a text file).

It starts with a comment: `# BEGIN DMI`.

It then has a block of the form:

```
version = 4.0
	width = 32
	height = 32
```

The tabs likely mean something as with the rest of DM, not tested and best not to assume.

Each state is of the form:

```
state = "somestate"
	dirs = 1
	frames = 1
```

The images are allocated in a "left to right, then top to bottom" standard grid pattern.

This list of images is split by states, and then those states are split by frames, and then those frames are split by directions.

For a 4-direction state, directions are in the order south/north/east/west.

Finally, it ends with `# END DMI`.

