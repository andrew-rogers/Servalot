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

var ConfigButton = function(values, normal, formFields, callback) {
    this.values = values;
    this.normal = normal;
    this.formFields = formFields;
    this.div = this.createButton(values, callback);
};

ConfigButton.prototype.createButton = function (values, callback) {

    var form = document.createElement("form");
    form.innerHTML=this.formFields(values);

    var div = document.createElement("div");
    div.style="border-style: none; height: 80px;";

    var btn_back = document.createElement("button");
    btn_back.innerHTML="&#x2718;"; // Cross
    btn_back.type="reset";
    form.appendChild(btn_back);

    var btn_ok = document.createElement("button");
    btn_ok.innerHTML="&#x2714;"; // Tick
    btn_ok.type="submit";
    form.appendChild(btn_ok);

    var div_normal = document.createElement("div");
    div_normal.innerHTML=this.normal(values);
    div_normal.style.display="block";

    var div_form = document.createElement("div");
    div_form.appendChild(form);
    div_form.style.display="none";

    div.appendChild(div_normal);
    div.appendChild(div_form);

    var that = this;
    form.addEventListener("submit", function(e) {
        div_normal.style.display="block";
        div_form.style.display="none";
        e.preventDefault(); // Prevent page reload.
        callback(that);
    });

    form.addEventListener("reset", function(e) {
        div_normal.style.display="block";
        div_form.style.display="none";
    });

    // When normal display is clicked show the form
    div_normal.addEventListener("click", function() {
        div_normal.style.display="none";
        div_form.style.display="block";
    });

    this.div_normal = div_normal;
    this.form = form;
    this.btn_ok = btn_ok;
    this.btn_back = btn_back;
    return div;
};

ConfigButton.prototype.update = function (values) {

    // If no values supplied, get old values and merge in form values
    if( typeof values === 'undefined' ) {
        values = this.values;
        Object.assign(values, this.getFormValues());
    }

    this.values = values;
    this.div_normal.innerHTML=this.normal(values);
    this.form.innerHTML=this.formFields(values);
    this.form.appendChild(this.btn_back);
    this.form.appendChild(this.btn_ok);

    return values;
};

ConfigButton.prototype.getFormValue = function (name) {
    return this.form.elements[name].value;
};

ConfigButton.prototype.getFormValues = function () {
    let obj={};

    for (let i=0; i<this.form.elements.length; i++) {
        let el = this.form.elements[i];
        if( el.type == "text" ) {
            obj[el.name]=el.value;
        }
    }
    return obj;
};

