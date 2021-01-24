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

var AppNavigator = function(div, servalot) {
    this.app_dir = "www/app.d";
    this.div = div;
    this.servalot = servalot;
    this.div_servalot=document.createElement("div");
    this.div_app=document.createElement("div");
    this.div_small=document.createElement("div");
    
    this.div.appendChild(this.div_servalot);
    this.div.appendChild(this.div_app);
    this.div.appendChild(this.div_small);
    
    this.div_apps=document.createElement("div");
    this.div_servalot.appendChild(this.div_apps);
    
    this.div_app.innerHTML="Expanded!";
    this.div_small.innerHTML="&lt;&lt;";
    var that = this;
    this.div_small.onclick = function() { that.expand(); };
    this.shrink();
};

AppNavigator.prototype.getAppControlsDiv = function() {
    return this.div_app;
};

AppNavigator.prototype.shrink = function() {
    this.div_servalot.style.display = "none";
    this.div_app.style.display = "none";
    this.div_small.style.display = "block";
};

AppNavigator.prototype.expand = function() {
    this.div_servalot.style.display = "block";
    this.div_app.style.display = "block";
    this.div_small.style.display = "none";
    this.getApps();
};

AppNavigator.prototype.openApp = function(div) {
    loadApp(div.app);
};

AppNavigator.prototype.cbApps = function(apps) {
    var apps = atob(apps.stdout).split('\n');
    this.div_apps.innerHTML="";
    var that = this;
    for( var i=0; i<apps.length; i++) {
        var div = document.createElement("div");
        var app = apps[i];
        div.innerHTML = app.slice(0,-3);
        div.app = app.slice(0,-3);
        div.onclick = function() { that.openApp(this); };
        this.div_apps.appendChild(div);
    }
};

AppNavigator.prototype.getApps = function() {
    var that = this;
    this.servalot.command({cmd: "exec", args: ["sh", "-c", "ls -1 www/app.d"]}, function(apps) {that.cbApps(apps);} );
};

