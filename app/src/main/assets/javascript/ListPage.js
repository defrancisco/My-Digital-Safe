/*
 * My Digital Safe, the secure notepad Android app.
 * Copyright (C) 2018 Security First Designs
 *
 * My Digital Safe is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <a href="www.gnu.org/licenses/">here</a>.
 */
var jsonString;
var wrapper;
var windowFlag = false;
var noNotesCommentDiv;

function renderList() {
    wrapper = document.getElementById("listWrapper");
    wrapper.innerHTML = "";
    jsonString = Android.getData();

    var jsonArray = JSON.parse(jsonString);

    if (jsonArray.length > 0) {
        noNotesCommentDiv.classList.add("hide");
    } else {
        // noNotesCommentDiv.classList.remove("hide");
    }

    for (var i = 0; i < jsonArray.length; i++) {
        (function () {

            var baseHtmlStructure = document.createElement("div");
            baseHtmlStructure.classList.add("listItem");

            var dataDiv = document.createElement("div");
            dataDiv.classList.add("dataDiv");

            var deleteAreaDiv = document.createElement("div");
            deleteAreaDiv.classList.add("deleteArea");

            var idDiv = document.createElement("div");
            idDiv.classList.add("id");
            var fileNameDiv = document.createElement("div");
            fileNameDiv.classList.add("fileName");
            var dateDiv = document.createElement("div");
            dateDiv.classList.add("date");

            var file = jsonArray[i];
            var id = file._id;
            var date = file.access_date;
            var fileName = file.file_name;

            idDiv.innerText = id;
            dateDiv.innerText = date;
            fileNameDiv.innerText = fileName;

            var spacerDiv = document.createElement("div");
            spacerDiv.classList.add("spacer");

            dataDiv.appendChild(idDiv);
            dataDiv.appendChild(fileNameDiv);
            dataDiv.appendChild(dateDiv);
            dataDiv.appendChild(spacerDiv);

            baseHtmlStructure.appendChild(dataDiv);
            baseHtmlStructure.appendChild(deleteAreaDiv);

            dataDiv.addEventListener("click", function () {
                var fileString = JSON.stringify(file);
                Android.listClickOccurred(fileString);
            });

            var borderDiv = document.createElement("div");
            borderDiv.classList.add("borderDiv");


            wrapper.appendChild(baseHtmlStructure);


        }());

    }
}
$(document).ready(function () {
    noNotesCommentDiv = document.getElementById("noNotesComment");

    renderList();

    $("#closeNewNoteWindowButton").on("click", function (event) {
        event.preventDefault();
        if (windowFlag) {
            document.getElementById("primaryWindow").classList.remove("hide");
            document.getElementById("newNoteWindow").classList.add("hide");
        } else {
            document.getElementById("primaryWindow").classList.add("hide");
            document.getElementById("newNoteWindow").classList.remove("hide");
        }
        windowFlag = !windowFlag;
    });
});