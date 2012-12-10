<div class="votebar-container">
    <div class="vote-numbers">
    	<span class="vote-up"></span><span class="vote-down"></span>
    </div>
    <table class="vote-meter">
    	<tr>
    		<td class="likes">&nbsp;</td>
        	<td class="dislikes">&nbsp;</td>
        </tr>
    </table>
    <?php if (isset($vote_data["user_rating"])) { ?>
		<?php if (!$vote_data["user_rating"]){ ?>
            <div class="ui-vote-buttons"></div>
        <?php } else {?>
            <span class="vote-message">You have already voted</span>
        <?php } ?>
    <?php } ?>
    <p>Rating: <?php echo $vote_data["submission_rating"]["score"]; ?></p>
</div>