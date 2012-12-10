<?php
class Messages_model extends CI_Model {

    var $categoryid = '';
    var $langID = '';
    var $topicID = '';       
    
    function __construct()
    {    	
        parent::__construct();
    }
    function get_inbox($username = "")
    {
		if ($username == "") {
			return false;	
		}
		$this->db->order_by("date", "desc"); 
		$this->db->where('username',$username);
		$query = $this->db->get('messages');
        if ($query->num_rows() > 0) {
			return $query->result_array();
		}else {
			return false;
		}
    }
    function send_message($data)
    {
		$data["from"] = $this->session->userdata("username");
        return $this->db->insert('messages', $data);
    }
    function delete_message($id = "") 
	{
		if($id == "")return false;
		$this->db->where('username',$this->session->userdata("username"));
		$this->db->where('messageId',$id);
        return $this->db->delete('messages');
	}
}
?>