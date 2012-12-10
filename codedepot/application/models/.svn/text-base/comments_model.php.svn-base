<?php
class Comments_model extends CI_Model {     
    
    function __construct()
    {    	
        parent::__construct();
    }
    
    function insert_comment($comment_data)
    {
    	$data = array(
    			'submissionID' => $comment_data["submissionID"],
    			'comment' => $comment_data["comment"],
    			'username' => $comment_data["username"]
    	);
    	$this->db->insert('comments', $data);
    } 
    
    function remove_comment($commentID){
    	$this->db->delete('comments', array('commentID' => $commentID));
    }

    function retrieve_comments($submissionID)
    {
    	$this->db->where('submissionID', $submissionID);
    	$this->db->order_by('dateTimestamp', 'desc', 5);
		$query = $this->db->get("comments");
		if ($query->num_rows() > 0) {
			return $query->result_array();
		}else {
			return false;
		}
    }
}
?>