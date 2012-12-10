<script>
    $(function() {
        $( "#accordion" ).accordion({heightStyle:"content"});
    });
</script>
<div id='accordion'>
	<?php 
	if(!empty($submissions)){
		foreach ($submissions as $key=>$entry){
			echo "<h3>"."<strong style='margin-right:5px;border-right:solid 1px;padding-right:5px; color:red;'>".$entry["score"]."</strong>".$entry["submissionName"] ."</h3>";
			echo "<div>";
			echo "<table id='submission_table'>";
			echo "<tr>";
			echo "<td>".$entry["score"] ."</td>";
			echo "<td> <a href=".base_url()."users/detailedSubmit/".$entry["submissionID"].">" .$entry["submissionName"] ."</a></td>";
			echo "<td>".$entry["username"] ."</td>";
			echo "<td>".$entry["dateSubmitted"] ."</td>";
			echo "</tr>";
			echo "</table>";
			echo "<p>".$entry["description"]."</p>";
			echo "</div>";
		}
	}else{
		echo "<h3>No Results Found</h3>";
	}
	?>
</div>