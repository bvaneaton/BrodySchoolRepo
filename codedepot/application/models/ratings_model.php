<?php
class Ratings_model extends CI_Model {
	
    var $username   = '';
    var $password = '';

    function __construct()
    {
        parent::__construct();
    }
    
	function insert_user_rating($submissionId = "", $vote = "") {
		$data = array(
			"submissionId" => $submissionId,
			"username" => $this->session->userdata("username"),
			"liked" => $vote
		);
		return $this->db->insert("ratings",$data);
	}
	
	function get_user_rating($submissionId = "") {
		$this->db->where("username",$this->session->userdata("username"));
		$this->db->where("submissionId",$submissionId);
		$this->db->select("liked");
		$query = $this->db->get("ratings");
		if ($query->num_rows() > 0) {
            return $query->result_array();
		}else {
			return false;
		}
	}
}
?>