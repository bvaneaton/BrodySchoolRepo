<?php if(!isset($success)) { ?>
    <form class="description_form" name="description_form" method="post" action="<?php echo base_url(); ?>submissions/submit_description/<?php echo $submissionID; ?>">
        <table class="submission_description">
            <tr>
                <td><span class="submission_description">Add a description</span></td>
            </tr>
            <tr>
            <td><textarea class="submission_description-input" type="text" value="" name="submission_description"></textarea></td>
            </tr>
         </table>
       <input id="description_submit" type="submit" value="Submit" />
    </form>
<?php } ?>