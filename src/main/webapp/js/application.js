var NoteModel = Backbone.Model.extend({
    initialize: function() {
        //alert("Welcome to this world");
    },
    urlRoot: '/notes',
    defaults: {
        id: null,
        dateCreated: new Date(),
        message: ''
    }
});

var NotesCollection = Backbone.Collection.extend({
    model: NoteModel,
    url: '/notes'
});

var FileModel = Backbone.Model.extend({
    initialize: function() {
        //alert("Welcome to this world");
    },
    urlRoot: '/files',
    defaults: {
        id: null,
        name: '',
        size: '',
        type: '',
        path: ''
    }
});

var FileCollection = Backbone.Collection.extend({
    model: FileModel,
    url: '/files'
});

var NoteView = Backbone.View.extend({
    tagName: 'li',
    events: {
    },
    initialize: function() {
        this.listenTo(this.model, 'change', this.render, this);
    },
    render: function() {
        var dateString = this.model.get('dateCreated');
        var date = new Date(dateString);
        this.$el.html(date.toString());
        return this;
    }
});

var NotesView = Backbone.View.extend({
    tagName: 'ul',
    className: 'nav nav-list lists-nav',
    events: {
    },
    initialize: function() {
        this.collection = new NotesCollection();
        this.collection.fetch({reset: true});
        this.collection.on('reset', this.render, this);
        this.collection.on('add', this.addOne, this );
    },
    render: function() {
        this.collection.each(this.addOne, this);
        return this;
    },
    addOne: function(note) {
        var noteView = new NoteView({ model: note });
        this.$el.append(noteView.render().el);
    }
});

var FileView = Backbone.View.extend({
    tagName: 'tr',
    events: {
    },
    initialize: function() {
        this.listenTo(this.model, 'change', this.render, this);
    },
    render: function() {
        var mame = $('<td/>').html(this.model.get('name'));
        var size = $('<td/>').html(this.model.get('size'));
        var type = $('<td/>').html(this.model.get('type'));
        var path = $('<td/>').html(this.model.get('path'));
        this.$el.append(mame);
        this.$el.append(size);
        this.$el.append(type);
        this.$el.append(path);
        return this;
    }
});

var FilesView = Backbone.View.extend({
    tagName: 'table',
    className: 'nav nav-list lists-nav',
    events: {
    },
    initialize: function() {
        this.collection = new FileCollection();
        this.collection.fetch({reset: true});
        this.collection.on('reset', this.render, this);
        this.collection.on('add', this.addOne, this );
    },
    render: function() {
        var row = $('<tr/>');
        var mame = $('<th/>').html('Name');
        var size = $('<th/>').html('Size');
        var type = $('<th/>').html('Mime-Type');
        var path = $('<th/>').html('Path');
        row.append(mame);
        row.append(size);
        row.append(type);
        row.append(path);
        this.$el.html(row);
        this.collection.each(this.addOne, this);
        return this;
    },
    addOne: function(file) {
        var fileViw = new FileView({ model: file });
        this.$el.append(fileViw.render().el);
    }
});

var UploadFormView = Backbone.View.extend({
    tagName: 'div',
    className: '',
//    events: {
//        'submit form' : 'uploadFile'
//    },
    render: function() {
        var form = $('<form action="/files" method="post" enctype="multipart/form-data" />');
        var file = $('<p>File: <input type="file" name="file" size="45" /></p>');
        var submit = $('<input type="submit" value="Upload">');
        form.append(file);
        form.append(submit);
        this.$el.append(form);
        console.log(this.$el);
        return this;
    }
});

// The main view of the application
var MainView = Backbone.View.extend({
    el: 'body',
    events: {
    },
    initialize: function() {
        //this.collection.on('add', this.render, this);
    },
    render: function() {
        var root = this.$el;
        var contentDiv = $('<div id="content"></div>');
        root.empty();
        root.html($('<h1>Example</h1>'));
        root.append(contentDiv);
        return this;
    }
});

var view = new MainView();
view.render();

var AppRouter = Backbone.Router.extend({
    routes: {
        '': 'index',
        'files': 'files'
    },
    index: function(){
        console.log('Routed to index.');
        var view = new NotesView();
        $('#content').html(view.render().el);
    },
    files: function(){
        console.log('Routed to files.');
        var filesView = new FilesView();
        var uploadView = new UploadFormView();
        var content = $('#content');
        //content.clear();
        content.append($(uploadView.render().el));
        content.append($(filesView.render().el));
    }
});
// Initiate the router
var appRouter = new AppRouter;

//appRouter.on('route:defaultRoute', function(actions) {
//    alert(actions);
//});

Backbone.history.start();

