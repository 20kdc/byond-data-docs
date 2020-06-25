# RUNSUB

This is an interesting algorithm... that I haven't fully documented.

It's:

1. content-aware
2. per-byte

it seems to have a running key like XORJUMP9

but it also seems to have some other, unknown state

general decryption operation appears to be something like "subtract key from byte to get decrypted byte",
 with an unknown key modifier applied - sometimes it's consistent with SUB, sometimes it's consistent with XOR, sometimes neither

however, this only covers a slim subset of RUNSUB input

