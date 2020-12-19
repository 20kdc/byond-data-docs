# RUNSUB

The RUNSUB encryption algorithm operates on buffers of data at a time.

It has two state values:

1. A Uint8 running checksum of unencrypted data. This is a checksum in the literal 'add' sense, and wraps.
2. A Uint32 key.

For each byte, encryption adds `checksum + (key >> (checksum % 32))` to the data byte (the shift zero-extends).

The checksum is updated after each byte. Again, it's a checksum of the unencrypted data.

It writes the checksum byte unencrypted at the end of the message.

Since encryption is addition, decryption is just subtraction.

