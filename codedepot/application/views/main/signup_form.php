<?php if(!isset($success)) { ?>
    <form class="signup_form" name="signup_form" method="post" action="<?php echo base_url(); ?>users/register">
        Username: <br />
        <input type="text" value="<?php echo set_value('posted_data[username]'); ?>" name="posted_data[username]" /><?php echo form_error('posted_data[username]'); ?><br />
        Password: <br />
        <input type="password" value="<?php echo set_value('posted_data[password]'); ?>" name="posted_data[password]" /><?php echo form_error('posted_data[password]'); ?><br />
        Re-type Password: <br />
        <input type="password" name="posted_data[passconf]" value="<?php echo set_value('posted_data[passconf]'); ?>" /><?php echo form_error('posted_data[passconf]'); ?><br />
        E-Mail address: <br />
        <input type="text" value="<?php echo set_value('posted_data[email]'); ?>" name="posted_data[email]" /><?php echo form_error('posted_data[email]'); ?><br />
    
        Profile Info: <br />
        <hr />
        Name:<br />
        <input type="text" value="<?php echo set_value('posted_data[name]'); ?>" name="posted_data[name]" /><?php echo form_error('posted_data[name]'); ?><br />
        Your Website:<br />
        <input type="text" value="<?php echo set_value('posted_data[webAddress]'); ?>" name="posted_data[webAddress]" /><?php echo form_error('posted_data[webAddress]'); ?><br />
        <input id="signup_submit" type="submit" value="Sign up" />
    </form>
<?php }else { ?>
	<p>You have successfully created an account!!</p>
    <div class="loading"></div>
    <script>
		setTimeout(window.location.href = "<?php echo base_url(); ?>users/login",3000);
	</script>
<?php } ?>