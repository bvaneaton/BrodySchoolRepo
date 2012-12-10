<?php 
function check_file_name($username, $submissionName, $names, $count = 0){
	if ($count == 0){
		foreach ($names as $key => $value){
			if ($value == $username.'_'.$submissionName.'.jpeg' || $value == $username.'_'.$submissionName.'.jpg'
					 || $value == $username.'_'.$submissionName.'.jpg' || $value == $username.'_'.$submissionName.'.JPEG'){
				return $value;
			}
		}
	}
	else{
		foreach ($names as $key => $value){
			if ($value == $username.'_'.$submissionName.$count.'.jpeg' || $value == $username.'_'.$submissionName.$count.'.jpg'
					|| $value == $username.'_'.$submissionName.$count.'.JPG'|| $value == $username.'_'.$submissionName.$count.'.JPEG'){
				return $value;
			}
		}
	}
	return false;
}

function sortArray($a, $b)
{
	if ($a['score'] == $b['score']) return 0;
	return ($a['score'] > $b['score']) ? -1 : 1;
}
?>