<?php if(!isset($success)){ ?>
    <form method="post" class="compose-form" action="<?php echo base_url(); ?>users/send_message">
        <input class="compose-to" type="text" value="" placeholder="To:" name="message_data[username]" /><br>
        <?php echo form_error('message_data[to]'); ?>
        <input class="compose-subject" type="text" value="" placeholder="Subject" name="message_data[subject]"><br>
        <?php echo form_error('message_data[subject]'); ?>
        <input class="compose-message" type="text" value="" name="message_data[message]" ><br>
        <?php echo form_error('message_data[message]'); ?>
        <input type="submit" value="Send">
    </form>
<?php } else { ?>
<script>
	$(".ui-compose-widget").compose_message("destroy");
</script>
<?php } ?>