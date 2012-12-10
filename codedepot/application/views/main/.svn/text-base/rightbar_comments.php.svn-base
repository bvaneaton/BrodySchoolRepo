
<div class="<?php echo $color; ?>">
	<?php echo $title; ?>
</div>
<div class="rightlgray_bg">
	<div class="teaser">
		<?php if (!$comments ) { ?>
			<p>There are no comments for this submission</p>
		<?php } else { ?>
			<?php for ($i = 0; $i < count($comments); $i++) { ?>
				<fieldset id="Welcome">
					<legend><?php echo $comments[$i]['username']; ?> says:</legend>
					<?php //echo $comments[$i]['dateTimestamp']; ?>
					<p>
						<?php echo $comments[$i]['comment']; ?>
					</p>
					<div class="comment-bar">
						<div style="position:relative; float:left; color:blue; margin-top:3px; margin-bottom:-7px;">
							<?php echo $comments[$i]['dateTimestamp']; ?>
						</div>
						<div style="position:relative; float:right; margin-bottom:-7px;"> 
							<?php if ($this->session->userdata('username') == $comments[$i]['username']) { ?>
							<form class="comments_form" method="post" action="<?php echo base_url(); ?>submissions/remove_comment/<?php echo $comments[$i]['commentID']?>/<?php echo $submissions['submissionID']?>">
								<input id="remove_comment_button" type="submit" value="[X] Delete" class="comment-but" />
							</form>
						</div>
					</div>
				<?php } ?>
				</fieldset>
				
			<?php } ?>
		<?php } ?>
		<?php if (!($this->session->userdata('username') == false)) {?>
		<div class="submission_comments_form" style="margin-top:5px; margin-left:2px; margin-bottom: -5px;">
			<p>
				<input id="submit_comment" type="button" value="Add a comment" />
			</p>
		</div>
		<?php } ?>
	</div>
</div>
