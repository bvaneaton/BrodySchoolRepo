 <div>
 <h2>My Submissions</h2>
        <div class="profile-box-user-submissions-submits"><p> 
        	<?php if (!($submissions)) {?>
        	There are no submissions for your account. You can upload code projects by clicking the button below.
        	<?php } else { ?>
	        	 <?php foreach ($submissions as $key => $value) { ?>  
	              <br><p><strong>Name:</strong> &nbsp; <a href="<?php echo base_url(); ?>users/detailedSubmit/<?php echo $value['submissionID'] ?>"><?php echo $value['submissionName'] ?></a>&nbsp;&nbsp;
	              <strong>Ranking:</strong> &nbsp;<?php echo $value['score'] ?>&nbsp;&nbsp;
	              <strong>Submitted:</strong> &nbsp;<?php echo $value['dateSubmitted'] ?></p> </br>
	             <?php } ?>  
             <?php }?>       	
        </p>
</div>