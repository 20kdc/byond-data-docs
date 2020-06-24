# NOT REALLY A DESCRIPTION, but may be useful working notes

The BYOND webclient is an HTTP server on the same port as the general BYOND server.

## Problems at present:

`Connection broken by server (error code 9)`

## Paths

The following paths are known to exist (that is, others may exist):

### /query

Returns JSON of the form:

```
{"url": "http://localhost:53927/play", "version": "513.1526"}
```

### /play

This is the main webclient page.

### /res/*

This is the content of the `web` BYOND distribution directory.

## WebSocket & Protocol Notes

The server is also a WebSocket server (path `/`).

The protocol has a different framing and a different handshake.

In particular, framing-wise, the prefix is a big-endian UInt16 for the type,
and that's all. No encryption, seemingly.

The server then sends the initial handshake packet.

