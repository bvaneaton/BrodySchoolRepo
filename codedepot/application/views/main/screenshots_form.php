<?php if(!isset($success)) { ?>
     <form class="submit_form" method="post" enctype="multipart/form-data" action="<?php echo base_url(); ?>submissions/submit_screenshots/<?php echo $submissionID?>"
		    <div class="screen-shot Upload">
		    	<p> Upload a screenshot</p>
		    	<p> Currently accepted file types: jpg</p>
		        <input type="file" name="userfile" size="20" />
				<br /><br />
				<input type="submit" value="Upload" /> 
			</div>
   		</form>  
<?php } ?>