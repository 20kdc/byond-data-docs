# INCOMPLETE

## Endianness

The BYOND protocol uses big-endian for types and lengths. Outside of this, uncertain.

Because of this, endianness has to be explicitly stated.

## Framing

The BYOND protocol is framed in a "type/length/value" form.

Specifically, two Uint32BE values represent the type and length in that order.

However, after the handshake packet, CtoS packets gain an additional "sequence number" Uint32BE value before the type and length.

StoC packet encryption appears to be different on a packet-to-packet basis to some extent.

Specifically, there's layering to the XORs.

There's a base packet XOR (UNKNOWN), and then incoming message packets (0x27) have text encrypted using (XOR Jump 64)[./XORJUMP64.md] (KEY AS OF YET UNKNOWN).

Other packets have too many runs of identical bytes for this to be universal.

I'm not sure about CtoS packets.

## Packets (CtoS)

### 0x0000: Quit

This is sent to indicate quitting.

It seems to be a single null byte, which could indicate that it's actually a string (uncertain).

### 0x0001: Handshake

This is sent at the start of any connection.

It always seems to have 18 bytes of content.

1. Uint32BE byondVersion
2. Uint32BE unk
3. Uint8 unk (The XOR key for the first 2 bytes of an StoC packet is this + 1. Always.)
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

S 0 1
R b0 b2 ae ae 98 18 82 82 83 39 95 9e 53 e7 f6 4e 85 be (...)
T 00 02 00 00       00 00
K b0 b0 ae ae ?? ?? 82 82 ?? ?? ?? ?? ?? ?? ?? ?? ?? ??

T: theory
K: XOR key
```

