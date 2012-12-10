<?php
class Subtopics_model extends CI_Model {

    var $subtopicID = '';  

    function __construct()
    {
        parent::__construct();
    }
    
    function get_subtopics() {
    	$query = $this->db->get("subtopics_categories");
    	if ($query->num_rows() > 0) {
    		return $query->result_array();
    	}else {
    		return false;
    	}
    }
	
	function get_subtopic($subtopicID = "") { 
		$this->db->where('subtopicID', $subtopicID); 
        $query = $this->db->get("subtopics_categories");
		if ($query->num_rows() > 0) {
            return $query->row_array();
		}else {
			return false;
		}
	}
}
?>