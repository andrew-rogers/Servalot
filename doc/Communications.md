# Servalot Communications

Servalot communications has two basic formats:

* Streaming, for data pass-through communications.
* Mailbox JSON messages for commands, responses and chat type messages.

## Streaming

Streaming supports binary processes, TCP sockets, serial ports and Bluetooth RFCOMM ports. A streaming connection is formed between two streaming type nodes. Data transferred over a streaming connection does not require framing.

## Mailbox Messaging

Mailbox messaging supports framed or datagram type communication such as a WebSocket, the WebView and command processing. JSON is used to represent these messages which include a source and destination mailbox id.

## Stream Framing

It is possible to communicate with mailboxes from stream nodes in one of two ways:

* Framing can be provided by the peer using JSON embedded in the stream. The JSON message must be prefixed with "SERVALOT" and suffixed with a newline, "\n".
* Servalot frames the data into arbitrary sized chunks, encodes in into ASCII hex, and creates a message

### Peer Framing

The peer frames the messages and uses the "SERVALOT" prefix and newline suffix. This allows the peer to specify the destination mailbox. The peer must be Servalot Mailbox compatible and thus peer framing may not be suitable for existing peers.

### Servalot Framing

This stream framing option allows the peer to be mailbox and framing agnostic. Servalot creates the message internally and posts it to the last mailbox to have communicated with the stream node. The ASCI-hex encoding allows binary data to be communicated to/from the stream node using JSON encoded messages. An example that uses this type of framing is where a microcontroller connected to a HC-05 Bluetooth module is required to communicate with a GUI running in the WebView or browser. The microcontroller firmware does not have to be modified to support JSON or framing.

## Intercepting a Streaming Connection

If a mailbox attempts to communicate with a streaming node any existing streaming connection is broken and both streaming nodes become Servalot framed. The mailbox has now intercepted the connection and has the responsibility of forwarding data between the two streaming nodes.
