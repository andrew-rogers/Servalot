# Servalot

## An **inetd** like super-server
Very much like **inetd** Servalot handles TCP sockets and communicates with a
service using the stdio. This means that the service does not have to handle
the socket connections directly. The service process can be written in any
language that can handle the stdio.

## Access to bluetooth connected RFCOMM devices
In addition to the service process being able to communicate with clients
connected via a TCP socket, the service shall also be able to communicate with
an RFCOMM device via the stdio connection to Servalot.

