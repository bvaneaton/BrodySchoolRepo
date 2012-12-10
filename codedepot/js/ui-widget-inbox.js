//Inbox Feature
$.widget("ui.compose_message", {
	options: {
		loadingDiv: "<div class='loading'></div>",
		loadingObj:""
	},
	_create: function() {
	},
	_init: function() {
		var self = this;
		self._setLoadingBox();
		self.element.slideDown();
		$.ajax({
			url: "/codedepot/users/view_compose_box",
			success: function(data){
				self.element.html(data);
				self._removeLoadingBox();
				self.element.slideDown();
				self.element.prepend("<a href='javascript:void(0)' class='close-compose'>[X]</a>");
				self.element.find(".close-compose").click(function() {
					self.destroy();
				});
				self.element.find("form").setupFormAjaxRequest();
			}
		});
	},
	_destroy: function() {
		this.element.remove();
	},
	_setLoadingBox: function () {
		if (this.options.loadingObj == ""){
			this.element.html(this.options.loadingDiv);
			this.options.loadingObj = this.element.children(".loading");
		}
	},
	_removeLoadingBox: function () {
		if (this.options.loadingObj != ""){
			this.options.loadingObj.remove();
		}
	}
});
$.widget("ui.inbox", {
	options:{
		loadingDiv: "<div class='loading'></div>",
		loadingObj:"",
		isCreated:false,
		isOpen:false,
		composeElement:"",
		messageOpenedId:null
	},
	_create: function (){
		var self = this;
		self._setLoadingBox();
		self.element.slideDown();
		self._updateInbox();
		this.options.isCreated = true;
	},
	_init: function() {
		if(this.options.isOpen){
			this.element.slideUp();
			this.destroy();
		}else {
			this.element.slideDown();
			this.options.isOpen = true;
		}
	},
	_openMessage: function (trElement) {
		if(this.options.messageOpenedId != trElement.attr("id")){
			trElement.find("div.message-field").slideDown();
			this.options.messageOpenedId = trElement.attr("id");
		} else {
			this.options.messageOpenedId = null;
		}
	},
	_closeAllMessages: function () {
		this.element.find("div.message-field").slideUp();
	},
	_updateInbox: function () {
		var self = this;
		$.ajax({
			url: "/codedepot/users/view_inbox",
			success: function(data){
				self.element.html(data);
				self._removeLoadingBox();
				self.element.slideDown();
				self.element.find("table.message-table tr td.td-message").click(function(){
					self._closeAllMessages();
					self._openMessage($(this));
				});
				self.element.find(".message-delete-link").click(function() {
					var response = window.confirm("Are you sure you want to delete?");
					if (response) {
						self._deleteMessage($(this).attr("id"));
					}
				});
				self.element.find(".compose-link").click(function(){
					if(self.options.composeElement != ""){
						try {
							self.options.composeElement.compose_message("destroy");
						}catch(err){
							self.options.composeElement = "";
						}
					}
					$(this).after("<div class='ui-compose-widget ui-widget'></div>");
					self.options.composeElement = $(this).next(".ui-compose-widget");
					self.options.composeElement.compose_message();
				});
			}
		});
	},
	_setLoadingBox: function () {
		if (this.options.loadingObj == ""){
			this.element.html(this.options.loadingDiv);
			this.options.loadingObj = this.element.children(".loading");
		}
	},
	_removeLoadingBox: function () {
		if (this.options.loadingObj != ""){
			this.options.loadingObj.remove();
			this.options.loadingObj = "";
		}
	},
	_deleteMessage: function(id) {
		var self = this;
		self._setLoadingBox();
		$.ajax({
			url: "/codedepot/users/delete_message/"+id,
			success: function(data){
				self._updateInbox();
			}
		});
	}
});