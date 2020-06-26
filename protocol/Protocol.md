# INCOMPLETE

## Endianness And Formats

The BYOND protocol uses big-endian for types and lengths. Outside of this, uncertain but probably LE everywhere.

Because of this, endianness has to be explicitly stated in docs when anything is being transmitted.

Strings are null-terminated, not length-prefixed.

## Framing

The BYOND protocol is split into frames.

Each frame has the following form:

For CtoS frames that aren't the handshake {

1. Uint16BE sequence (Only present on CtoS frames that aren't the handshake.)

}

1. Uint16BE type
2. Uint16BE length
3. Array of length Uint8s data

Each frame *must* be sent as a single write call. (It has to do with the PSH flag.)

The sequence number is initialized in the client's handshake, and is advanced with the following method:

1. Multiply the current sequence number by 0x43d4 - the result must be 32-bit.
2. Add this result to itself divided by 0xFFF1 and multiplied by 15, again all 32-bit.
3. This result, cast to Uint16, is the new sequence number except for one last rule:
3. If the sequence number is 0 (keeping in mind it's Uint16): Set it to 1.

Additionally, there's a Uint32 piece of state not encoded here: The encryption key. If it is not 0, encryption is active.

The encryption algorithm has been named [RUNSUB](../algorithms/RUNSUB.md) for now, and applies to the data.

It's woven into the handshake procedure so that neither a modified client nor a modified server can force a zero encryption key, only both.

## Packets (CtoS)

### 0x0000: Quit

This is sent to indicate quitting.

It seems to be a single null byte, which could indicate that it's actually a string (uncertain).

### 0x0001: Handshake

This is sent at the start of any connection.

It always seems to have 18 bytes of content in modern versions.

1. Uint32LE byondVersion
2. Uint32LE minVersion
3. Uint32LE encryptionKeyModified
4. Uint16LE firstSequenceNumber (this is the first sequence number the client will use)
5. Uint32LE byondMinorVersion (note: this isn't present in v354, but I don't know which version it was added to)

The exact details of the encryption key modification are `encryptionKeyModified = encryptionKey - ((minVersion * 0x10000) + byondVersion)`.

The server sends back it's own handshake - 0x0001 - in response.

### 0x0002: Invoke Verb

Further details unknown, just know it involves some sorta client-local verb index system (see StoC 0x0011)

My tests look like:

`02 00 00 00 02 00 00 00 00 00` (invokes verb index 0)
`02 00 01 00 02 00 00 00 00 00` (invokes verb index 1)
`02 00 02 00 02 00 00 00 00 00` (invokes verb index 2)

### 0x001a: PHS 1

Further details unknown.

The server sends back 0x003b in response.

## Packets (StoC)

### 0x0001: Handshake

This is sent after the server receives the client's handshake.

It is already encrypted (which indicates the key must be present in the communications up to/including it).

It has 60 bytes of content.

1. Uint32LE byondVersion
2. Uint32LE minVersion
3. Uint8 isPermanentPort (1 if so, 0 otherwise)
4. Uint8 dmbFlagsHasEx
5. Uint8 unk (introduced in v433)
6. Padding: Read Uint32LE, add 0x71bd632f then AND 0x04008000 - if not 0, repeat. For example, 0x06beb95e terminates, 0x1ed688b0 doesn't.
7. Uint32LE addToEncryptionKey
8. Padding: Read Uint32LE, add 0x17db36e3 then AND 0x00402000 - if not 0, repeat. For example, 0x69b7216b terminates.

addToEncryptionKey has to be added to the encryption key.

The client sends back 0x001a in response.

### 0x000e: Cache List

Further details unknown.

WebClient expects 6 bytes but regular clients don't need any. Almost certainly a different format.

This is the point where the server starts sending packets I can immediately verify it repeatedly sends.

I don't intend to document everything right now, just get the webclient working.

The communications (which are sometimes bi-direction) end with the server sending 0x0018 and then a batch of 0x0011.

### 0x0011: Register Verb Index

This registers a verb index.

It's important to note that these verbs are immediately usable.

1. Uint16LE index
2. String name
3. Unknown stuff (00 00 ff 00 04 00 for my test verbs but could be of any length)

### 0x0018: PHS 5

Further details unknown. Empty in my tests.

By this point the client is in-game, and gets sent verb indexes.

### 0x0026: Describe Atom Appearance

1. 4 bytes (??? subject to change ???)
2. String text
3. 6 bytes
4. The text character, but not sure how it's represented

Further details unknown.

This appears to be describing the appearance of an atom.

### 0x0027: Incoming Message

This is sent from server to client.

1. String text
2. WebClient *suggests* that there is optional stuff here the existence of which is based on if there's room or not.

### 0x003b: PHS 2

Further details unknown.

This is followed by 0x00ef.

### 0x0072: Server Information

Further details unknown.

This contains a lot of stuff that looks like server information.

### 0x00ef: PHS 3

Further details unknown.

This is followed by 0x000e.

