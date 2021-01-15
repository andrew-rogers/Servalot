# Servalot Communications

Servalot communications has two basic formats:

* Streaming, for data pass-through communications.
* JSON messages for commands, responses and chat type messages.

## Streaming

Streaming supports binary processes, TCP sockets, serial ports and Bluetooth RFCOMM ports. A streaming connection is formed between two streaming type nodes. Data transferred over a streaming connection does not require framing.

## Messaging

Messaging supports framed or datagram type communication such as a WebSocket, the WebView and command processing. JSON is used to represent these messages which include a source and destination messaging node id.

## Stream Framing

It is possible to communicate with messaging nodes from stream nodes in one of two ways:

* Framing can be provided by the peer using JSON embedded in the stream. The JSON message must be prefixed with "SERVALOT" and suffixed with a newline, "\n".
* Servalot frames the data into arbitrary sized chunks, encodes in into ASCII hex, and creates a message

### Peer Framing

The peer frames the messages and uses the "SERVALOT" prefix and newline suffix. This allows the peer to specify the destination messaging node. The peer must be Servalot messaging node compatible and thus peer framing may not be suitable for existing peers. If the peer is part of a streaming connection, data that is not encapsulated is passed to the streaming connection otherwise it is discarded.

### Servalot Framing

This stream framing option allows the peer to be messaging node and framing agnostic. Servalot creates the message internally and posts it to the last messaging node to have communicated with the stream node. The ASCI-hex encoding allows binary data to be communicated to/from the stream node using JSON encoded messages. An example that uses this type of framing is where a microcontroller connected to a HC-05 Bluetooth module is required to communicate with a GUI running in the WebView or browser. The microcontroller firmware does not have to be modified to support JSON or framing.

## Intercepting a Streaming Connection

If a messaging node attempts to communicate with a streaming node any existing streaming connection is broken and both streaming nodes become Servalot framed. The messaging node has now intercepted the connection and has the responsibility of forwarding data between the two streaming nodes.
