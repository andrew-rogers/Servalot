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

var Query = function() {
    this.pending = {};
    if (wvi !== undefined) {
        that = this;
        wvi.response = function(json) { if(log)log("R:"+json); that.response(JSON.parse(json)); };
    }
};

Query.prototype.query = function(obj, callback) {

    if( callback === undefined ) callback = function(){};

    // Find smallest number not already pending and use for source port
    let src = 0;
    while( this.pending[src] !== undefined ) src=src+1;

    // Store the callback
    this.pending[src] = callback;

    // Create a transport control packet
    tcp = {src: ""+src, dst: "0", data: obj}; // Use dst=0 for Servalot command server

    wvi.command(JSON.stringify(tcp));
};

Query.prototype.response = function(tcp) {
    let dst=tcp.dst;
    if( dst !== undefined ) {

        // Check for the FIN flag
        var tcf = tcp.tcf;
        if( tcf !== undefined && tcf.includes("f") ) {
            delete this.pending[dst];
        }
        else {
            var cb=this.pending[dst];
            if( cb !== undefined ) cb(tcp.data);
        }
    }
};

