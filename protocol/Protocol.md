# INCOMPLETE

## Endianness

The BYOND protocol uses big-endian for types and lengths. Outside of this, uncertain.

Because of this, endianness has to be explicitly stated.

## Framing

The BYOND protocol is framed in a "type/length/value" form.

Specifically, two Uint32BE values represent the type and length in that order.

However, after the handshake packet, CtoS packets are encrypted.

## Packets (CtoS)

### 0x01: Handshake

This is sent from client to server at the start of any connection.

It always seems to have 18 bytes of content.

The first 4 are suspiciously equivalent to a Uint32BE meaning 'BYOND version',
 and the second 4 also look like a Uint32BE.

The next 6 bytes MIGHT be the XOR key used by the client in future comms in reverse.

Just check that possibility, I'm not done here yet.

It has what appears to be a unique connection ID of some sort.

## Packets (StoC)

### 0x01: Handshake

This is sent from server to client after it receives the client's handshake.


