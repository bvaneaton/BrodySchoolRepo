<div class="float_left">

	<ul id="navigation">
		<li><a href="<?php echo base_url(); ?>home" class="first">Home</a>
		</li>
		<li class="browse_menu"><a href="<?php echo base_url(); ?>categories/browse">Browse</a>
        	<div class="category_menu_dropdown">
        		<div class="container_menu">
            	<?php foreach($cat_tree as $key => $category){
            		if (!empty($category["topics"])){ ?>
            			<div id="<?php echo $category["lang_name"]; ?>">
            				<a href="<?php echo base_url(); ?>categories/browse/<?php echo rawurlencode($category["lang_name"]); ?>"><?php echo $category["lang_name"]; ?></a>
            		

                        
                      <div class="right_menu">  
                        <?php 
                        if (!empty($category["topics"])){
							echo "<div class='topics_menu'>";
                            foreach($category["topics"] as $topics) { 
                            ?>
                            	<a href="<?php echo base_url(); ?>categories/browse/<?php echo rawurlencode($category["lang_name"]); ?>/<?php echo rawurlencode($topics["topic_name"]); ?>"><?php echo $topics["topic_name"] ?></a>
                            <?php 
                            } 
                            echo "</div>";
                        }
                        
                        ?>
                        <div class="top_hit_title">
	                        <h3> Top <?php echo $category["lang_name"]; ?> submission:</h3>
	                    </div>
                        <div class="top_hits">
	                        
	                        <div class="top_hit_box">
	                        	<img src="<?php echo base_url(); ?>images/img1.jpeg" height="120" width="120 " />
	                        </div>
	                        <div class="top_hit_box">
	                        	<img src="<?php echo base_url(); ?>images/img2.jpeg" height="120" width="120 " />
	                        </div>
	                        <div class="top_hit_box">
	                        	<img src="<?php echo base_url(); ?>images/img3.jpeg" height="120" width="120 " />
	                        </div>
	                      </div>
                        </div>
                    </div>
                <?php } }?>
            </div>
            </div>
		</li>
		<li><a class="projects-link" href="<?php ?>">My Projects</a>
		</li>
		<li><a class="link-submit" href="">Submit A Project</a>
		</li>
	</ul>
	<div class="clear"></div>
</div>
<div class="float_right">
	<ul id="navigation_right">
		<li><a href="">Help</a>
		</li>
		<script>
			$(document).ready(function() {
				$(".login-link").colorbox({
					speed: 500
				});
				$(".register-link").colorbox({
					speed: 500
				});
				$(".inbox-link").click(function() {
					$(".ui-inbox").inbox();
				});
				$(".projects-link").colorbox({
					speed: 500,
					 <?php if($this->session->userdata('logged_in')) { ?>
						href: "<?php echo base_url(); ?>submissions/view_submissions/<?php echo $this->session->userdata('username') ?>"
					<?php } else { ?>
						href: "<?php echo base_url(); ?>users/signup"
					<?php } ?>
				});
				$(".link-submit").colorbox({
					speed: 500,
					 <?php if($this->session->userdata('logged_in')) { ?>
						href: "<?php echo base_url(); ?>submissions/index"
					<?php } else { ?>
						href: "<?php echo base_url(); ?>users/signup"
					<?php } ?>
				});
			});

		</script>
        <?php if($this->session->userdata('logged_in')) { ?>
        	<li><a href="<?php echo base_url(); ?>users/index/<?php echo $this->session->userdata('username') ?>/"><?php echo $this->session->userdata('username') ?></a></li>
			<li><a href="<?php echo base_url(); ?>users/logout">Log out</a></li>
		<?php }else { ?>
        	<li><a class="login-link" href="<?php echo base_url(); ?>users/login">Login</a></li>
        	<li><a class="register-link" href="<?php echo base_url(); ?>users/signup">Sign Up</a></li>
        <?php } ?>
		<li><a class="inbox-link" href="javascript:void(0);">Inbox</a>
		</li>
	</ul>
</div>
<div class="ui-inbox ui-widget"></div>