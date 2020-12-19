# CYCSUB

The CYCSUB encryption algorithm ("ServerEncrypt"/"ServerDecrypt") operates on buffers of data at a time.

It has a key of some amount of bytes. If the key is zero-length, the encryption algorithm is completely deactivated (is effectively NOP).

Note that this includes the deactivation of the running checksum component as seen below.

It has two state values:

1. A Uint8 running checksum of unencrypted data. (`checksum`) This is a checksum in the literal 'add' sense, and wraps.
2. An index within the key (`keyIndex`) - this starts at 0.

For each byte in the buffer, encryption adds `checksum + key[keyIndex]` to the data byte.

The key index is then incremented, wrapping to the beginning of the key if it would go past the end.

The checksum is updated after each byte. Again, it's a checksum of the unencrypted data.

Since encryption is addition, decryption is just subtraction.

