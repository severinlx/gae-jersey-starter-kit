// ---------------------------------------------------------------------------------------------------------------------
// Model

var File = function() {
    /**
     * Internal JSON identifier.
     * @private
     * @type {Number}
     */
    this.id = null;
    /**
     * File name.
     * @private
     * @type {String}
     */
    this.name = null;
    /**
     * File path in Google Blob Store
     * @private
     * @type {String}
     */
    this.path = null;
    /**
     * File size in bytes.
     * @private
     * @type {String}
     */
    this.size = null;
    /**
     * File mime type.
     * @private
     * @type {String}
     */
    this.type = null;
};

File.prototype.getId = function() {
    return this.id;
};

File.prototype.getName = function() {
    return this.name;
};

File.prototype.getPath = function() {
    return this.path;
};

File.prototype.getType = function() {
    return this.type;
};

File.prototype.getSize = function() {
    return this.size
};

// ---------------------------------------------------------------------------------------------------------------------
// jQuery Components

  /**
 * Create file upload form.
 * Element must be <form/>
 * @returns {jQuery}
 */
$.fn.fileUploadForm = function() {
    if (!this.is('form')) return this;

    var fileInput = $('<input type="file" name="file" size="45" />');

    var fileFormGroup = $('<div class="form-group"/>');
    fileFormGroup.append('<label for="file">Select a file:</label>');
    fileFormGroup.append(fileInput);

    var form = this;
    form.empty();
    //form.attr('action', '/files');
    //form.attr('method', 'post');
    form.attr('enctype', 'multipart/form-data');
    form.addClass('form-inline panel panel-default');
    form.append(fileFormGroup);

    var completeHandler = function() {
        $('body').fileBody();
    };

    var submitButton = $('<input type="button" value="Upload It" class="btn btn-danger" />');
    form.append(submitButton);

//    $(fileInput).on('change', function() {
//        var file = this.files[0];
//        name = file.name;
//        size = file.size;
//        type = file.type;
//        //Your validation
//    });

    $(submitButton).on('click', function() {
        var formData = new FormData($('form')[0]);
        $.ajax({
            url: '/files',
            type: 'POST',
//            xhr: function() {  // Custom XMLHttpRequest
//                var myXhr = $.ajaxSettings.xhr();
//                if (myXhr.upload){ // Check if upload property exists
//                    myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // For handling the progress of the upload
//                }
//                return myXhr;
//            },
            //Ajax events
            //beforeSend: beforeSendHandler,
            success: completeHandler,
            //error: errorHandler,
            // Form data
            data: formData,
            //Options to tell jQuery not to process data or worry about content-type.
            cache: false,
            contentType: false,
            processData: false
        });
    });

    return form;
};

/**
 * Creates file data to table row.
 * Element must be <th/>
 * @returns {jQuery}
 */
$.fn.fileTableHead = function() {
    if (!this.is('tr')) return this;
    var tr = this;
    tr.append($('<th/>').html('ID'));
    tr.append($('<th/>').html('Name'));
    tr.append($('<th/>').html('Path'));
    tr.append($('<th/>').html('Type'));
    tr.append($('<th/>').html('Size'));
    return tr;
};

/**
 * Creates file data to table row.
 * Element must be <tr/>
 * @param {File} file
 * @returns {jQuery}
 */
$.fn.fileTableData = function(file) {
    if (!this.is('tr')) return this;
    var tr = this;
    tr.attr('id', 'file-' + file.getId());
    tr.append($('<td/>').html(file.getId()));
    tr.append($('<td/>').html(file.getName()));
    tr.append($('<td/>').html(file.getPath()));
    tr.append($('<td/>').html(file.getType()));
    tr.append($('<td/>').html(file.getSize()));
    return tr;
};

/**
 * Creates file table.
 * Element must be <table/>
 * @param {File[]} files
 * @returns {jQuery}
 */
$.fn.fileTable = function(files) {
    if (!this.is('table')) return this;
    var table = this;
    var body = $('<tbody/>');
    body.append($('<tr/>').fileTableHead());
    for (var i = 0; i < files.length; i++) body.append($('<tr/>').fileTableData(files[i]));
    table.addClass('table table-striped');
    table.append(body);
    return table;
};

/**
 * Creates file view.
 * Element must be <div/>
 * @returns {jQuery}
 */
$.fn.fileDiv = function() {
    if (!this.is('div')) return this;
    var div = this;
    var files = [];
    var render = function() {
        div.append($('<h1>Files</h1>'));
        div.append($('<form/>').fileUploadForm());
        div.append($('<table/>').fileTable(files));
    };
    var load = function(data) {
        $.each(data, function(index, item) {
            var file = new File();
            $.extend(file, item);
            files.push(file);
        });
        render();
    };
    $.ajax({
        dataType: "json",
        type: "GET",
        url: '/files',
        success: load
    });
    return div;
};
/**
 * Creates file view.
 * Element must be <body/>
 * @returns {jQuery}
 */
$.fn.fileBody = function() {
    if (!this.is('body')) return this;
    var body = this;
    body.empty();
    body.append($('<div id="files"/>').fileDiv());
    return body;
};

$(document).ready(function() {
    $('body').fileBody();
});

