<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed'); 

class Login {
	var $CI;
    var $user_table = 'users';

	public function login($username = "", $password = "") {
		$this->CI = & get_instance();
		$this->CI->load->model('Users_model');
		
		if($username == "" || $password == ""){
			return false;
		}
		
		if($this->CI->session->userdata('username') == $username) {
            return false;
        }
		
		$row = $this->CI->Users_model->get_user($username);
		
		if ($row) {
            
            if($password != $this->CI->encrypt->decode($row['password'])) {
                return false;
            }
            
            $this->CI->session->sess_destroy();
            
            $this->CI->session->sess_create();
            
            unset($row['password']);
            
            $this->CI->session->set_userdata($row);
            
            $this->CI->session->set_userdata(array('logged_in' => true));            
            
            return true;
        } else {
            return false;
        }  
	}
	function logout() {
		$this->CI =& get_instance();		
		$this->CI->session->sess_destroy();
	}

}

?>