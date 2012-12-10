// Vote Bar Widget
$.widget("ui.vote_buttons", {
	options:{
		user_rating:null,
		submissionId:"",
		callback:null,
		upButton:null,
		downButton:null,
		loadingDiv: "<div class='loading'></div>",
		loadingObj:""
	},
	_create: function (){
		this.element.html("<div class='vote-up-button button'>Vote Up</div><div class='vote-down-button button'>Vote Down</div>");
		this.options.upButton = this.element.find(".vote-up-button");
		this.options.downButton = this.element.find(".vote-down-button");
	},
	_init: function () {
		var self = this;
		self.options.upButton.click(function (){
			self._sendVote(1);
		});
		self.options.downButton.click(function (){
			self._sendVote(0);
		});
	},
	_sendVote: function (myvote) {
		var self = this;
		this._setLoadingBox();
		$.ajax({
			url:"/codedepot/submissions/vote",
			data:{"vote": myvote, "submissionId" : self.options.submissionId},
			type:"POST",
			success: function(data) {
				self._removeLoadingBox();
				self.element.html(data);
				if(self.options.callback){
					self.options.callback();
				}
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
		}
	}
});
$.widget("ui.vote_bar", {
	options:{
		submissionId:"",
		submission_rating:"",
		user_rating:"",
		voteUp_element:null,
		voteDown_element:null,
		voteMeter_element:null,
		loadingDiv: "<div class='loading'></div>",
		loadingObj:""
	},
	_create: function() {
		
	},
	_init: function() {
		var self = this;
		this._setLoadingBox();
		$.ajax({
			url:"/codedepot/submissions/display_votes"	,
			data: {"submissionId" : self.options.submissionId},
			dataType: "json",
			success: function(data){
				self.options.submission_rating = data.submission_rating;
				self.options.user_rating = data.user_rating;
				self._removeLoadingBox();
				self.element.html(data.html);
				//save html elements
				self.options.voteUp_element = self.element.find(".vote-numbers .vote-up");
				self.options.voteDown_element = self.element.find(".vote-numbers .vote-down");
				self.options.voteMeter_element = self.element.find(".vote-meter");
				self._adjustVoteBar();
				self.element.find(".ui-vote-buttons").vote_buttons({
					"submissionId":self.options.submissionId,
					"callback": function(){
						self._init();
					}
				});
			}
		});
	},
	_adjustVoteBar: function() {
		var voteUp = parseInt(this.options.submission_rating.voteUp);
		var voteDown = parseInt(this.options.submission_rating.voteDown);
		var voteMeterElement = this.options.voteMeter_element;
		if((voteUp + voteDown) != 0) {
			var likeWidth = (voteUp / (voteUp + voteDown)) * 100;
			var dislikeWidth = 100 - likeWidth;
			voteMeterElement.find(".likes").css({width:likeWidth + "%"});
			voteMeterElement.find(".dislikes").css({width:dislikeWidth + "%"});
		}
		this.options.voteUp_element.html(voteUp);
		this.options.voteDown_element.html(voteDown);
		
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