<?php
class Tags_model extends CI_Model {

    var $submissionID = '';  

    function __construct()
    {
        parent::__construct();
    }
	
	function get_tags($submissionID = "") { 
		$this->db->where('submissionID', $submissionID); 
        $query = $this->db->get("tags");
		if ($query->num_rows() > 0) {
            return $query->row_array();
		}else {
			return false;
		}
	}
	
	function insert_entry($submissionID, $tag_data) {
		$data = array(
			'submissionID' => $submissionID,
			'subtopic1' => $tag_data['subtopic1'],
       		'subtopic2' => $tag_data['subtopic2'],
        	'subtopic3' => $tag_data['subtopic3']
		);
        return $this->db->insert('tags', $data);
	}
}
?>