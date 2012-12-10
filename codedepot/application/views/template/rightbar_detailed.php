
<div class="<?php echo $color; ?>">
	<?php echo $title; ?>
</div>
<div class="rightlgray_bg">
	<div class="teaser">
            <div class="ui-vote-bar ui-widget"></div>
            <br>
            <p style="margin-top: 6px;"> <strong> Files: </strong> </p>
            <form class="download_submission_form" method="post" action="<?php echo base_url(); ?>submissions/download_submission/<?php echo $submissions['submissionID']?>">
	     		<input id="download_submission_button" class="butt_style" type="submit" value="Download submission" />
	    	</form>	
	</div>
</div>
