# NOT REALLY A DESCRIPTION, but may be useful working notes

The BYOND webclient is an HTTP server on the same port as the general BYOND server.

## The Current State Of The Official Software

These are the transcribed StoC packets of a connection, with v513.1526:

1. Handshake packet (0x0001) (513, 498)
2. Winset packet (0x00e5), no controls, DMS files 'alert', 'any', 'bar', 'browser', 'button', 'child', 'color', 'dpad' -- (at this point I stopped, the most important note here is that defaultSkin was moved to the end)
3. Cache list packet (0x000e), 1 entry, 0x26b, cache ID is in fact the actual cache ID, weird string thing is empty.
4. Empty IDK packet (0x001a) - this is presumably the fancy auth thing that goes bonkers
5. Output packet (0x0027) - `Connection broken by server (error code 9)<br/>\n`

I feel it important to note that after the 0x001a, dealing with the stupid websocket mask nonsense shows the client's response: `00 1a 2d 00`.

Clearly this is not satisfactory for our server overlord and we should bow down to it and blah blah blah.

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

The server then sends the initial handshake message.

## StoC

### 0x0001: Handshake (WS)

1. Uint32LE byondVersion
2. Uint32LE unkButProbablyMinVersion

### 0x00e5: Winset (WS)

1. Uint16LE controls
2. for each entry in controls, a complex structure blah blah blah

then, while there is room at the end of the message:

1. Uint16LE strLen - if 0xFFFF, then a Uint32LE comes after to replace it
2. (that many string bytes)

having any of these at all indicates the packet sets up the "main skin" of the webclient
(i.e. the root of the weird HTML UI classes thing it has)
each of these is such a class, see current state of software for further notes

