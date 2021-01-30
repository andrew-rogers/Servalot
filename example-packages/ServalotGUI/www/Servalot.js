/**
 *
 * @licstart  The following is the entire license notice for the 
 *  JavaScript code in this page.
 *
 * Copyright (C) 2020  Andrew Rogers
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
 
var Servalot = function() {
    this.pending = {};
    if (typeof wvi !== 'undefined') {
        var that = this;
        wvi.response = function(json) { if(log)log("R:"+json); that.response(JSON.parse(json)); };
    }
    else
    {
        var hostname = window.location.hostname;
        this.ws = new WebSocket("ws://" + hostname + ":8800");
        var that = this;
        this.ws.onmessage = function (e) {
            that.response(JSON.parse(e.data));
        };
    }
};

Servalot.prototype.command = function(obj, callback) {

    if( callback !== undefined ) {
        // Find smallest number not already pending
        let n = 0;
        while( this.pending[n] !== undefined ) n=n+1;

        // Store the callback
        this.pending[n] = callback;
        obj.src = ""+n;
        obj.dst = "0"; // Command server
    }

    if (typeof wvi !== 'undefined') {
        wvi.command(JSON.stringify(obj));
    }
    else
    {
        this.ws.send(JSON.stringify(obj));
    }
};

Servalot.prototype.response = function(obj) {
    let n=obj.dst;
    if( n !== undefined ) {
        let cb=this.pending[n];
        if( cb !== undefined ) cb(obj);
        delete this.pending[n];
    }
};

