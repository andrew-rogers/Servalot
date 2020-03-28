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

var RfcommControls = function(query) {
    this.query = query;
    this.div = document.createElement("div");
    //this.createControls();
};

RfcommControls.prototype.show = function() {
    this.createControls();
}

RfcommControls.prototype.createControls = function(obj) {
    let that=this;
    if( obj === undefined ) {
        this.query({cmd: "get bluetooth devices"}, function(obj) { that.createControls(obj); });
        return;
    }
    if(obj.devs) {
        this.div.innerHTML="";
        var devs=obj.devs;
        for( var i=0; i<devs.length; i++) {
            // TODO check if already assigned to a TCP port and get existing details
            let values={name: "My gadget", bind: "0.0.0.0", port: "8085", bt_name: devs[i].name, address: devs[i].address};         
            var btn = new ConfigButton(values,
                function (values) {
                    let str = "<b>" + values.name + "</b> " + values.bind + " " + values.port + "<br><b>" + values.bt_name + "</b> " + values.address;
                    return str;
                },
                function (values) {
                    let str = "<input type='text' size='35' name='name' value='" + values.name + "'><br>"
                            + "<input type='text' size='15' name='bind' value='" + values.bind + "'>"
                            + "<input type='text' size='5' name='port' value='" + values.port + "'>";
                    return str;
                },
                function (btn) {
                    let cmd=btn.update();
                    cmd["cmd"]="add service";
                    cmd["type"]="rfcomm";
                    that.query(cmd);
                }
            );
            this.div.appendChild(btn.div);
        }
    }
};

