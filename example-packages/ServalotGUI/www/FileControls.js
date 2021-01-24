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

var FileControls = function(servalot, div) {
    this.servalot = servalot;
    this.div = div;
    this.cdLoadText = null;
    this.cbSaveText = null;
    this.createGUI();
    this.showControls();
    this.file_selector = new FileSelector(this.div_list, listfiles);
};

FileControls.prototype.createGUI = function(callback) {

    this.div.innerHTML="";

    // Create two divs: one for the controls and one for the file list
    this.div_controls=document.createElement("div");
    this.div.appendChild(this.div_controls);
    this.div_list=document.createElement("div");
    this.div.appendChild(this.div_list);

    // Create load button
    var btn_load = document.createElement("button");
    btn_load.innerHTML = "Load...";
    this.div_controls.appendChild(btn_load);
    
    // Create filename editor
    var span_filename = document.createElement("span");
    this.div_controls.appendChild(span_filename);
    this.input_filename = document.createElement("input");
    this.input_filename.setAttribute("type","text");
    span_filename.appendChild(this.input_filename);
    
    // Create save button
    var btn_save = document.createElement("button");
    btn_save.innerHTML = "Save";
    this.div_controls.appendChild(btn_save);
    
    // Setup event handlers
    var that = this;
    btn_load.onclick = function() { that.showFileSelector(); };
    btn_save.onclick = function() { that.saveFile(); };
};

FileControls.prototype.showControls = function () {
    this.div_list.style.display = "none";
    this.div_controls.style.display = "block";
            
};

FileControls.prototype.showFileSelector = function () {
    var that = this;
    this.div_list.style.display = "block";
    this.div_controls.style.display = "none";
    this.file_selector.show(function(path) {
        that.input_filename.value = path;
        that.servalot.command({cmd: "exec", args: ["sh", "-c", "cat "+path]}, function(obj) {
            var text=atob(obj.stdout);
            that.showControls();
            if( that.cbLoadText ) that.cbLoadText(text)
        });
    });
};

FileControls.prototype.saveFile = function () {
    if( this.cbSaveText ) {
        var file = this.input_filename.value;
        var text = this.cbSaveText();
        this.servalot.command({cmd: "exec", args: ["sh", "-c", "cat > "+file], stdin: btoa(text)}, function(){
            // TODO: Handle file saved acknowledgement
        });
    }
};

FileControls.prototype.registerLoadText = function(callback) {
    this.cbLoadText = callback;
};

FileControls.prototype.registerSaveText = function(callback) {
    this.cbSaveText = callback;
};

FileControls.prototype.getFileName = function() {
    return that.input_filename.value;
};



