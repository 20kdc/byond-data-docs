# CYCSUB (WARNING: DOCUMENTS AND IMPLEMENTATIONS HAVE NOT BEEN PROPERLY TESTED)

The CYCSUB encryption algorithm ("ServerEncrypt"/"ServerDecrypt") operates on buffers of data at a time.

It has a key of some amount of bytes. If the key is zero-length, the encryption algorithm is completely deactivated (is effectively NOP).

Note that this includes the deactivation of the running checksum component as seen below.

It has two state values:

1. A Uint8 running checksum of unencrypted data. (`checksum`) This is a checksum in the literal 'add' sense, and wraps.
2. An index within the key (`keyIndex`) - this starts at 0.

It iterates *in reverse* through bytes in the buffer. To be clear, the instructions presented are given assuming that the buffer index (and no other indices) are going in reverse, from end to beginning.

For each byte in the buffer, encryption adds `checksum + key[keyIndex]` to the data byte.

The key index is then incremented, wrapping to the beginning of the key if it would go past the end.

The checksum is updated after each byte. Again, it's a checksum of the unencrypted data.

Since encryption is addition, decryption is just subtraction.

## Notes on actual usage

### Unknown
This is a two-layered, encryption first being performed with something called the "domain name" (stored in the DMB file as a string - unknown index, but then scrambled), and then being performed with the key `3c 7c 33 5e 0a 71 0d 5b`.

