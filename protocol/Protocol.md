# INCOMPLETE

## Endianness

The BYOND protocol uses big-endian for types and lengths. Outside of this, uncertain.

Because of this, endianness has to be explicitly stated.

## Framing

The BYOND protocol is framed in a "type/length/value" form.

Specifically, two Uint32BE values represent the type and length in that order.

However, after the handshake packet, CtoS packets gain an additional "sequence number" Uint32BE value before the type and length.

StoC packet encryption might be different on a packet-to-packet basis. Not strictly sure.

I'm referring to the encryption algorithm as (RUNSUB)[../algorithms/RUNSUB.md] right now.

I'm not sure about CtoS packets - they probably do the same thing, but don't be sure.

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
4. Uint16LE unk (could be related to the sequence number business, CHECK THIS)
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
3. Uint8 portTruncated
4. Uint8 dmbFlagsHasEx
5. Padding to hide key: read Uint32BE, add 0x71bd632f, AND 0x04008000 - if not 0, repeat
6. Uint32LE addToEncryptionKey
7. Padding to hide key: read Uint32BE, add 0x17db36e3, AND 0x00402000 - if not 0, repeat

addToEncryptionKey has to be added to the encryption key.

The client sends back something else in response.

### 0x0027: Incoming Message

This is sent from server to client.
Details unknown.
