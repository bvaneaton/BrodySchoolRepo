    <form class="submit_form" method="post" enctype="multipart/form-data" action="<?php echo base_url(); ?>submissions/submit_content">
        <table class="submissions_table">
            <tr>
                <td><span class="submission_label">Project Title</span></td>
                <td><input type="text" value="" name="submission_data[title]"/></td>
            </tr>
            <tr>
                <td><span class="submission_label">Select General Language</span></td>
                <td>
                    <select name="submission_data[language]">                    	
                        <?php foreach ($languages as $key => $value) { ?>                        
                        <option value="<?php echo $value['langID'] ?>"><?php echo $value['lang_name'] ?></option>      
                        <?php } ?> 
                    </select>
                </td>
            </tr>
            <tr>
                <td><span class="submission_label">Select Topic</span></td>
                <td>
                    <select name="submission_data[topic]">
                        <option>----Select a topic----</option>                        
                        <?php foreach ($topics as $key => $value) { ?> 
                        <option value="<?php echo $value['topicID'] ?>"><?php echo $value['topic_name'] ?></option>      
                        <?php } ?>           
                    </select>
                </td>
            </tr>
            <tr>
                <td><span class="submission_label">Select up to 3 Subtopics</span></td>
                <td>
                    <select multiple="multiple" name="submission_data[subtopics][]">
                        <option>----Select a subtopic----</option>
                        <?php foreach ($subtopics as $key => $value) { ?>
                        <option value="<?php echo $value['subtopicID'] ?>"><?php echo $value['subtopicName'] ?></option>      
                        <?php } ?>
                    </select>
                </td>
            </tr>
        </table>
		<input type="checkbox" name="submission_data[overwrite]" value="true" checked="checked">Overwrite existing file 
		<br /><br />
		<p>Currently accepted file types: .rar</p>
		<input type="file" name="userfile" size="20" />
		<br /><br />
		<input type="submit" value="Upload" />
		
    </form>