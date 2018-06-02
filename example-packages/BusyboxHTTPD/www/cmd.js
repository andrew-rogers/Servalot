/**
 *
 * @licstart  The following is the entire license notice for the 
 *  JavaScript code in this page.
 *
 * Copyright (C) 2018  Andrew Rogers
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

function query_sh(script,stdin,callback)
{
  var b64=btoa(stdin);
  var blob = new Blob([script,"\n\n",b64], { type: "text/plain" });
  var xmlhttp = new XMLHttpRequest(); // new HttpRequest instance
  xmlhttp.onreadystatechange = function() {
    if (xmlhttp.readyState == XMLHttpRequest.DONE) {
      var rt=xmlhttp.responseText;
      var i=rt.indexOf("\n");
      var err=rt.substring(0,i);
      rt=rt.substring(i+1);
      i=rt.indexOf("\n");
      var h2=rt.substring(0,i);
      rt=rt.substring(i+1);
      if(callback)callback(err,atob(rt));
    }
  };
  xmlhttp.open("POST", "/cgi-bin/cmd.sh", true);
  xmlhttp.send(blob);
}

