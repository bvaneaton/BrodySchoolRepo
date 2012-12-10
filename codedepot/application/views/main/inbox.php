<div class="inbox-container"> 
	<h2>Inbox</h2><div class="compose-button-row"><a href="javascript:void(0);" class="compose-link">Compose</a></div>
    <?php 
		if (empty($inbox)) { 
	?>
    	<p>Your inbox is empty</p>
    <?php 
		} else {
	?>
    <table class="message-table">
    	<tr>
        	<th></th>
        	<th>From:</th>
            <th>Subject:</th>
            <th>Date:</th>
        </tr>
    <?php
		foreach($inbox as $key => $messages){
	?>
        <tr id="row_<?php echo $key ?>" class="message-row <?php if ($messages["unread"]){ ?>unread<?php }?>">
            <td>
            	<a id="<?php echo $messages["messageId"]; ?>" class="message-delete-link" href="javascript:void(0)">[X]</a>
                <script language="javascript" type="text/javascript">
				 	$("#<?php echo $messages["messageId"]; ?>").click(function(){
						$(this).prevAll('.ui-inbox').prevAll('.ui-inbox').inbox({idToDelete:<?php echo $messages["messageId"]; ?>});
						$(this).prevAll('.ui-inbox').prevAll('.ui-inbox').inbox('deleteMessage');
					});
				</script>
            </td>
        	<td id="column_<?php echo $key ?>" class="td-message" colspan="3">
                <div class="inbox-field inbox-from"><?php echo $messages["from"]; ?></div>
                <div class="inbox-field inbox-subject"><?php echo $messages["subject"]; ?></div>
                <div class="inbox-field inbox-date"><?php echo $messages["date"]; ?></div>
                <div class="message-field"><?php echo $messages["message"]; ?></div>
            </td>
        </tr>
    <?php 	
		}
	?>
    </table>
    	
    <?php } ?>
</div>