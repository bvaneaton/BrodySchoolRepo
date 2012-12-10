<?php
class Submissions_model extends CI_Model {

    var $username   = '';
    var $categoryid = '';
    var $submissionName = '';
    var $voteUp ='';
    var $submissionID = '';
    var $categoryID = '';
    var $location = '';

    function __construct()
    {
        parent::__construct();
        $this->load->helper('directory');
        $this->load->helper('file');
        $this->load->helper('submissions_helper');
    }
    
    function submit_image($submissionID, $json){
    	$data['screenshots'] = $json;
    	$this->db->where('submissionID', $submissionID);
    	$this->db->update('submissions', $data);
    }
    
    function get_screenshots($submissionName, $username){    
    	$this->db->select('screenshots');
    	$this->db->where('username', $username);
    	$this->db->where('submissionName', $submissionName);
    	$query = $this->db->get('submissions');
    	if ($query->num_rows() > 0){
    		$result = $query->row_array();
    		$results_json = json_decode($result['screenshots']);
    	}
    	return false;
    }
    
    function get_top_ranked_entries()
    {
    	$this->db->order_by("score", "desc"); 
        $this->db->limit(10);
    	$query = $this->db->get('submissions');
        
    	if ($query->num_rows() > 0) {
            return $query->result_array(); 
		}else {
			return false;
		}
    }
 
    function get_top_ranked_entries_with_language($langID ='')
    {
    	$query = $this->db->query('SELECT language_categories.langID, categories.categoryID, submissions.categoryID, submissions.submissionName, submissions.score,
    					submissions.username, submissions.screenshots
						FROM (categories, language_categories, submissions)
						WHERE categories.langID = language_categories.langID
    					AND categories.categoryID = submissions.categoryID
    					AND categories.langID = ' .$langID.' 
    					AND language_categories.langID = ' .$langID.'
    					ORDER BY submissions.score DESC	
    					LIMIT 0, 3'
    	);
    	return $query->result_array();
    }
    
    function get_top_ranked_entries_with_topic($topicID ='')
    {
    	$query = $this->db->query('SELECT language_categories.topicID, categories.categoryID, submissions.categoryID, submissions.submissionName, submissions.score,
    					submissions.username,  submissions.screenshots
						FROM (categories, language_categories, submissions)
						WHERE categories.topicID = language_categories.topicID
    					AND categories.categoryID = submissions.categoryID
    					AND categories.topicID = ' .$topicID.' 
    					AND language_categories.topicID = ' .$topicID.'
    					ORDER BY submissions.score DESC	
    					LIMIT 0, 3'
    	);
    	return $query->result_array();
    } 
    
    function get_top_recent_ranked_entries()
    {
		$this->db->order_by('dateSubmitted', "desc");
		$this->db->limit(10);
    	$query = $this->db->get('submissions');
    	if ($query->num_rows() > 0) {
            $result = $query->result_array();           
            usort($result, 'sortArray');  
            return $result;          
		}else {
			return false;
		}
    }
	
	function get_submission_given_submissionName($submissionName = "") { 
		$this->db->where('submissionName', $submissionName); 
        $query = $this->db->get("submissions");
		if ($query->num_rows() > 0) {
            return $query->row_array(); 
		}else {
			return false;
		}
	}
	
	function get_submission_given_submissionID($submissionID = "") {
		$this->db->where('submissionID', $submissionID);
		$query = $this->db->get("submissions");
		if ($query->num_rows() > 0) {
			return $query->row_array();
		}else {
			return false;
		}
	}
	
	function get_submission_given_categoryID($categoryID = "") {
		$this->db->where('categoryID', $categoryID);
		$query = $this->db->get("submissions");
		if ($query->num_rows() > 0) {
			return $query->result_array();
		}else {
			return false;
		}
	}
	
	function get_submission_given_username($username = "") {
		$this->db->where('username', $username);
		$this->db->order_by("dateSubmitted", "desc");
		$query = $this->db->get("submissions", 3);
		if ($query->num_rows() > 0) {
			return $query->result_array();
		}else {
			return false;
		}
	}
	
	function get_submission_given_username_all($username = "") {
		$this->db->where('username', $username);
		$this->db->order_by("dateSubmitted", "desc");
		$query = $this->db->get("submissions");
		if ($query->num_rows() > 0) {
			return $query->result_array();
		}else {
			return false;
		}
	}
	
	function get_submission_given_user_subname($username = '', $submissionName = ''){
		$this->db->where('username', $username);
		$this->db->where('submissionName', $submissionName);
		$query = $this->db->get('submissions');
		if ($query->num_rows() > 0){
			return $query->row_array();
		}
		else{
			return false;
		}
	}
	
	function check_if_exists($username = '', $submissionName = ''){
		$this->db->where('username', $username);
		$this->db->where('submissionName', $submissionName);
    	$query = $this->db->get('submissions');
    	if ($query->num_rows() > 0){
    		return true;
    	}
    	else{
    		return false;
    	}
	}
	
	function insert_description($data){
		$this->db->where('submissionID', $data['submissionID']);
		$description['description'] = $data['description'];
		$this->db->update('submissions', $description);
	}
	
	function insert_entry($sub_data)
    {
       $data = array(
			'submissionName' => $sub_data["submissionName"],
			'categoryID' => $sub_data["categoryID"],
       		'username' => $sub_data["username"]
		);
        return $this->db->insert('submissions', $data);
    }
    
    function remove_entry($submissionID){
    	$this->db->delete('submissions', array('submissionID' => $submissionID));
    }
    
    function insert_vote($submissionID = '', $vote = ''){    
		$this->load->model("Ratings_model");
		if($vote == "voteUp"){
			$success = $this->Ratings_model->insert_user_rating($submissionID, true);
		} elseif ($vote == "voteDown"){
			$success = $this->Ratings_model->insert_user_rating($submissionID, false);
		} else {
			return false;	
		}
		
    	$this->db->select('voteUp, voteDown');
    	$this->db->where('submissionID', $submissionID);
    	$query = $this->db->get('submissions');
    	$result = $query->row_array();
    	$result[$vote] += 1;
    	
    	$data = array(
    			'submissionID' => $submissionID,
    			$vote => $result[$vote],
				'score' => $result['voteUp'] - $result['voteDown']
    	);
    	
    	$this->db->where('submissionID', $submissionID);
		$this->db->update('submissions', $data);    	
    }
    
    function getScore($submissionID = ''){
		if($this->session->userdata("username")){
			$this->load->model("Ratings_model");
			$result["user_rating"] = $this->Ratings_model->get_user_rating($submissionID);
		}
    	$this->db->select('voteUp, voteDown, score');
    	$this->db->where('submissionID', $submissionID);
    	$query = $this->db->get('submissions');
    	
    	if ($query->num_rows() > 0){
    		$result["submission_rating"] = $query->row_array();
			return $result;
    	}
    	else{
    		return false;
    	}
    }

    function update_entry($username = '', $submissionName = '', $categoryID = '')
    {
    	$date = new DateTime();
		$date->getTimestamp();
        $data = array(
               'username' => $username,
               'submissionName' => $submissionName,
        		'categoryID' => $categoryID,
        		'dateSubmitted' => ($date->format('Y-m-d H:i:s'))
            );
	
		$this->db->where('username', $username);
		$this->db->where('submissionName', $submissionName);
		$this->db->update('submissions', $data); 
    }
}
?>