<?php if(!isset($success)) { ?>
        <table class="submission_description">
            <tr>
                <td><span class="delete_submission_form">Do you really want to delete this submission?</span></td>
            </tr>
            <tr>
				<div class="delete-form user-submissions">
				<p>	
					<form class="delete_submission_form" method="post"
					action="<?php echo base_url(); ?>submissions/delete_submission/<?php echo $submissionID?>/0">
					<input id="delete_submission_button_no" type="submit"
						value="No" style="float: right;" />
					</form>
						
					<form class="delete_submission_form" method="post"
					action="<?php echo base_url(); ?>submissions/delete_submission/<?php echo $submissionID?>/1">
					<input id="delete_submission_button_yes" type="submit"
						value="Yes" style="float: right;" />
					</form>												
			</div>	
            </tr>
         </table>
<?php } ?>