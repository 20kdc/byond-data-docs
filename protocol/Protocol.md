# INCOMPLETE

## Endianness

The BYOND protocol uses big-endian for types and lengths. Outside of this, uncertain but probably LE everywhere.

Because of this, endianness has to be explicitly stated in docs when anything is being transmitted.

## Framing

The BYOND protocol is split into frames.

Each frame has the following form:

1. Uint16BE sequence (Only present on CtoS frames that aren't the handshake.)
2. Uint16BE type
3. Uint16BE length
4. Array of length Uint8s data

Each frame *must* be sent as a single write call. (Yes, really.)

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
4. Uint16LE firstSequenceNumber (this is the first sequence number)
5. Uint32LE byondMinorVersion (note: this isn't present in v354, but I don't know which version it was added to)

The exact details of the encryption key modification are `encryptionKeyModified = encryptionKey - ((minVersion * 0x10000) + byondVersion)`.

The server sends back it's handshake packet in response.

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

The client sends back something else in response.

### 0x0027: Incoming Message

This is sent from server to client.
Details unknown.
