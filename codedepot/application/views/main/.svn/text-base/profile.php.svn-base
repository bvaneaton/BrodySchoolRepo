<div class="profile-content">
    <h2><?php echo $this->session->userdata('username')."'s profile page" ?></h2>
    <div class="profile-box profile-info">
        <div class="blk_heading"> <h3>My Profile Info</h3> </div>
        <table id="profile_table">
			<tr>
        		<th class="heading"> Name: </th> <td><input type="text" value="<?php echo $this->session->userdata('name'); ?>" /></td>
        	</tr>
			 <tr>
                <th class="heading"> Email: </th> <td><input type="text" value="<?php echo $this->session->userdata('email'); ?>" /></td>
            </tr>
            <tr>
                <th class="heading"> Website: </th> <td><input type="text" value="<?php echo $this->session->userdata('webAddress'); ?>" /></a></td>
            </tr>
        </table>
    </div>
    <div class="profile-box user-submissions">
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
        <script>
			$(document).ready(function() {
				$("#submit_project").colorbox({
					speed: 500,
					href: "<?php echo base_url(); ?>submissions/index"
				});
			});
		</script>
        <input id="submit_project" type="button" value="Submit your project" />
        
        <script>
			$(document).ready(function() {
				$("#more_projects").colorbox({
					speed: 500,
					href: "<?php echo base_url(); ?>submissions/view_submissions/<?php echo $this->session->userdata('username') ?>"
				});
			});
		</script>
        <input id="more_projects" type="button" value="View All Projects" />
    </div>
</div>