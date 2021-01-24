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

var Editor = function(servalot, gui) {
    this.div_controls = gui.getAppControlsDiv();
    this.div_main = gui.getMainDiv();
    this.file_controls = new FileControls(servalot, this.div_controls);
    this.createGUI();
    
    // Register the callbacks
    var that = this;
    this.file_controls.registerLoadText(function(text) {
        that.ta_edit.value = text;
    });
    this.file_controls.registerSaveText(function() {
        return that.ta_edit.value;
    });
};

Editor.prototype.createGUI = function () {
    this.div_main.innerHTML="";
    this.ta_edit = document.createElement("textarea");
    this.div_main.appendChild(this.ta_edit);
};

