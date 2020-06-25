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

It always seems to have 18 bytes of content.

1. Uint32BE byondVersion
2. Uint32BE unk
3. Uint8 unk (The XOR key for the first 2 byte of an StoC packet seems to be this + 1.)
4. 5 unknown bytes
5. Uint32BE byondMinorVersion

## Packets (StoC)

### 0x0001: Handshake

This is sent after the server receives the client's handshake.

It is already encrypted (which indicates the key must be present in the communications up to/including it).

It has 60 bytes of content.

1. Uint32BE byondVersion (BUT ENCRYPTED)
2. 56 unknown bytes

### 0x0027: Incoming Message

This is sent from server to client.

## Decryption workpad

```
between client 513 (01 02) and server 512 (00 02)

C 0 1
R 01 02 00 00 14 01 00 00 af bc be 12 da 91 f6 05 00 00

NOTE that this byte       ^
 is the key for the first packet - 1
 current working theory is that the packet type is somehow involved too

S 0 1
R b0 b2 ae ae 98 18 82 82 83 39 95 9e 53 e7 f6 4e 85 be (...)
T 00 02 00 00       00 00
K b0 b0 ae ae ?? ?? 82 82 ?? ?? ?? ?? ?? ?? ?? ?? ?? ??

T: theory
K: key

theory: content-based : subtract data from key??? add, not XOR?
 content-based would explain the oddities in changes, so almost certainly true
 but I'm having trouble developing a consistent theory

there's too many single-byte (00) packets... coincidence, ignored data, oddities???
note that *empty* packets don't seem to be a thing

-- notes on cycle --

note: the LACK of cycles with same input values on 'A'
but also note: that despite different inputs, the 256 repetition "settles" with the final 3 bytes

```

