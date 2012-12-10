<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed'); 

class Submit {
	var $CI;
    var $submission_table = 'submissions';
    
    public function submit($submissionName = "", $username = "", $categoryid ="" ) {
    	$this->CI = & get_instance();    	
    	$this->CI->load->model('Submissions_model');
    	
    	if($username == "" || $submissionName == "" || $categoryid == ""){
    		return false;
    	}
    	
    	$this->CI->Submissions_model->instert_entry($submissionName, $username, $categoryid);
    	
    }
	
	
}

?>