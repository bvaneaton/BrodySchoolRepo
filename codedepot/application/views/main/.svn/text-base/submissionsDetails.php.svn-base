<script>
	 $(document).ready(function() {
	 	$("#submit_screenshot").colorbox({
	 		speed: 500,
	 		href: "<?php echo base_url(); ?>submissions/show_form/<?php echo $submissions['submissionID']?>/screenshots_form"
		});
	 	$("#submit_comment").colorbox({
			speed: 500,
			href: "<?php echo base_url(); ?>submissions/show_form/<?php echo $submissions['submissionID']?>/comments_form"
		});
	 	$("#submit_description").colorbox({
	 		speed: 500,
	 		href: "<?php echo base_url(); ?>submissions/show_form/<?php echo $submissions['submissionID'] ?>/description_form"
		});
	 	$("#delete_submission").colorbox({
	 		speed: 500,
	 		href: "<?php echo base_url(); ?>submissions/show_form/<?php echo $submissions['submissionID'] ?>/delete_form"
		});
	 	$(".ui-vote-bar").vote_bar({submissionId:<?php echo $submissions['submissionID']; ?>});
	 	$(function() {
	 	    $(".submission-screenshots-images img")
	 	        .mouseover(function() { 
	 	        	var clone = $(".submission-image img").clone();
	 	        	clone.css("position","absolute");
	 	        	clone.css("top","0px");
	 	            $(".submission-image img").attr("src", $(this).attr("src"));
	 	            clone.appendTo(".submission-image");
	 	            clone.fadeOut(250, function(){
	 	            	clone.remove();
	 	            });
	 	           
	 	        });
	 	});
	});
</script>
<div class="submission-content">
	<div class="header_border_bottom" style="margin-bottom:10px;">
		    <h1><?php echo $submissions['submissionName'] ?></h1>
	</div>

	<?php	if (($submissions['screenshots'])) { 
			 $temp = json_decode($submissions['screenshots']);
			 $img_url = base_url().$temp[0];
			 } else {
			 $img_url = base_url()."images/no_pic.png";
		 	} ?>
		 	
    <div class="submission-box">
    	<div class="submission-image">
    		<img src="<?php echo $img_url ?>" width="250" height="250" />
        </div>
        <div class="submission-info">
        	<h3>Details</h3>
        	<table id="profile_table">
			<tr>
        		<th class="heading"> Name: </th> <td><p> <strong><?php echo $submissions['submissionName']; ?></strong> </p></td>
        	</tr>
			 <tr>
                <th class="heading"> Submitter: </th> <td><p> <strong><?php echo $submissions['username']; ?></strong> </p></td>
            </tr>
            <tr>
                <th class="heading"> Submitted: </th> <td><p> <strong><?php echo $submissions['dateSubmitted']; ?></strong> </p></a></td>
            </tr>
            <tr>
                <th class="heading"> Score: </th> <td><p> <strong><?php echo $submissions['score']; ?></strong> </p></a></td>
            </tr>
    		</table>
        </div>
		<div class="clearing"></div>
		<?php if ($this->session->userdata('username') == $submissions['username']) {?>
			<div class="screenshot-box user-submissions">
				<p>
	
					<input id="submit_screenshot" type="button"
						value="Upload a screenshot" style="float: left;" />				
				
					<input id="delete_submission" type="button"
						value="Delete submission" class="cboxElement" style="float: right;" />
			</div>
		<?php } ?> 

		<div class="submission-screenshots" >
		<?php $imageArray = json_decode($submissions['screenshots'])?>
		<?php if (!(empty($imageArray))) { ?>
			<?php foreach ($imageArray as $key => $image){ ?>
			<div class="submission-screenshots-images" style="float: left; margin-top:5px; margin-left:3px;">
	    		<img src="<?php echo base_url().$image ?>" width="52" height="51"/>
	        </div>
			<?php }?>
		<?php } ?>
		</div>
		
		<div class="profile-description" style="clear:both" >      
		<fieldset id="Welcome">     
            <legend> Description:</legend>
            
           <p><?php echo $submissions['description']; ?></p>  
        </fieldset> 
        <?php if ($this->session->userdata('username') == $submissions['username']) {?>
		        <div class="description-box user-submissions">
		        <p>

				<input id="submit_description" type="button" value="Add a description" />
				</p>
				</div>
		<?php } ?>	 
		</div>     
                       

	</div> 
</div>