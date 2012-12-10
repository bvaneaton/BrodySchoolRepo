
<div class="<?php echo $color; ?>">
	<?php echo $title; ?>
</div>
<div class="rightlgray_bg">
	<div class="teaser">
    <script>
	$(document).ready(function(){
		$(".filter-links").click(function() {
			$("#accordion").html("<div class='loading'></div>");
			$(".filter-selected").remove();
			$(this).after("<span class='filter-selected'><=</span>");
			$.ajax({
				url:"<?php echo current_url(); ?>",
				type:"post",
				data: {"tagid": $(this).attr("id")},
				success: function(data) {
					$("#accordion").replaceWith(data);
				}
			});
		});
	});
	</script>
    	<ul>
		<?php 
		if(!empty($tags_list)){
			foreach($tags_list as $key => $tag){ ?>
			<li>
            	
            	<a id="<?php echo $tag["subtopic1"]; ?>" class="filter-links" href="javascript:void(0)"><?php echo $tag["subtopicName"]; ?></a>
            </li>
		<?php } 
		} else {
			echo "No subtopics to filter with!";	
		}?>
        </ul>
	</div>
</div>
