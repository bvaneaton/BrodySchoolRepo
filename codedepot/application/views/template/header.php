<div id="header_top">
	<div class="wrapper">
		<?php $this->load->view('template/nav_top.php'); ?>
	</div>
</div>
<div class="header_bottom">
	<div class="wrapper">
		<div class="site_logo">
			<a href="<?php echo base_url(); ?>"><img
				src="<?php echo base_url(); ?>/images/logo.png" width="429"
				height="71" alt="CodeDepot Logo" /> </a>
		</div>	
		<!-- Search bar -->
		<?php $this->load->view('template/search_bar.php');?>
		<div class="clear"></div>
	</div>
</div>
