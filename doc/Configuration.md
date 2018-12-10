# Servalot Configuration

Servalot configuration involves:

* Installation of extension packages including services
* Configuration of services such as TCP port to listen on and what handler process to use to serve a client connection to the TCP port.

JavaScript Object Notation (JSON) is used to pass configuration commands and associated arguments to the configuration manager. JSON is also used to pass any response messages back to the GUI.
The use of JSON allows the use of the [Android WebView](https://developer.android.com/reference/android/webkit/WebView) or a web browser with little differentiation required.

## HTML GUI and Servalot back-end interaction

![alternative text](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/andrew-rogers/Servalot/master/doc/html_gui_cmd_sd.puml&v=1)

