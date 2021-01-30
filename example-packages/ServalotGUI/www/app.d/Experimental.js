/**
 *
 * @licstart  The following is the entire license notice for the 
 *  JavaScript code in this page.
 *
 * Copyright (C) 2021  Andrew Rogers
 *
 *
 * The JavaScript code in this page is free software: you can
 * redistribute it and/or modify it under the terms of the GNU
 * General Public License (GNU GPL) as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option)
 * any later version.  The code is distributed WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU GPL for more details.
 *
 * As additional permission under GNU GPL version 3 section 7, you
 * may distribute non-source (e.g., minimized or compacted) forms of
 * that code without the copy of the GNU GPL normally required by
 * section 4, provided you include this license notice and a URL
 * through which recipients can access the Corresponding Source.
 *
 * @licend  The above is the entire license notice
 * for the JavaScript code in this page.
 *
 */

var Experimental = function(servalot, gui) {
    this.servalot = servalot;
    this.div_controls = gui.getAppControlsDiv();
    this.div_main = gui.getMainDiv();

    this.createCmdGUI();

};

Experimental.prototype.createCmdGUI = function() {

    // Create a get button
    var btn_get = document.createElement("button");
    btn_get.innerHTML = "Get";
    this.div_main.appendChild(btn_get);

    // Create a Logs button
    var btn_logs = document.createElement("button");
    btn_logs.innerHTML = "Logs";
    this.div_main.appendChild(btn_logs);

    // Div for shell commands
    var div_run = document.createElement("div");
    this.div_main.appendChild(div_run);

    // Create textarea for command line
    var ta_cmd = document.createElement("textarea");
    ta_cmd.rows=1;
    ta_cmd.style.width="80%";
    div_run.appendChild(ta_cmd);

    // Run button
    var btn_run = document.createElement("button");
    btn_run.innerHTML = "Run";
    div_run.appendChild(btn_run);

    // Create output area
    var ta_output = document.createElement("textarea");
    ta_output.rows=20;
    ta_output.style.width="100%";
    div_run.appendChild(ta_output);

    // For now, get file from fixed location when button is clicked.
    btn_get.onclick=function() {
        var url = 'https://github.com/andrew-rogers/andrew-rogers.github.io/raw/master/Servalot/Packages/HTTPDemo.zip';
        var filename = 'thing.zip';
        that.servalot.command({cmd: "httpget", url: url, filename: filename});
    };

    // Handle click event
    var that = this;
    btn_logs.onclick=function() {
        that.servalot.command({cmd: "readlogs"}, function(obj) {
            var str="";
            for( var i=0; i<obj.logs.length; i++) str = str + obj.logs[i].type + ": " + obj.logs[i].msg + "\n";
            ta_output.value = ta_output.value + str + "\n";
        });
    };

    // Handle click event
    var that = this;
    btn_run.onclick=function() {
        that.servalot.command({cmd: "exec", args: ["sh", "-c", ta_cmd.value]}, function(obj) {
            var str=atob(obj.stdout);
            ta_output.value = ta_output.value + str + "\n";
        });
    };

};
