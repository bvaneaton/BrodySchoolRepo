<?php
class Topics_model extends CI_Model {

	var $topicID = '';

    function __construct()
    {
        parent::__construct();
    }
    
    function get_all_topics(){
    	$query = $this->db->get("topics_categories");
    	if ($query->num_rows() > 0) {
    		return $query->result_array();
    	}else {
    		return false;
    	}
    }
}
?>