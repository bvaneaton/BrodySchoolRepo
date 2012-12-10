<?php
class Languages_model extends CI_Model {

    var $langID = '';  

    function __construct()
    {
        parent::__construct();
    }
    
    function get_all_languages(){
    	 $query = $this->db->get('language_categories');
    	 if ($query->num_rows() > 0) {
    	 	return $query->result_array();
    	 }else {
    	 	return false;
    	 }
    }
}
?>