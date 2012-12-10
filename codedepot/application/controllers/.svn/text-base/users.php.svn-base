<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');
class Users extends MY_Controller{
	
	public function __construct() {
		parent::__construct();
	}
	
	function index($username = "") {
		if($username == "") {
			redirect('/home/', 'refresh');
		}
		//GET USER DATA
		$content["submissions"] = $this->Submissions_model->get_submission_given_username($username);
		$data["content"] = $this->load->view('main/profile', $content,TRUE);
		
		$this->func_display($data);
	}
	
	function login() {
		$this->load->library('login'); 
		$header = array();
		$posted_data = $this->input->post('posted_data');
		if(empty($posted_data)) {
			$posted_data["username"] = $this->session->flashdata("username");
			$posted_data["password"] = $this->session->flashdata("password");
		}
		if(!empty($posted_data)){
			$success = $this->login->login($posted_data["username"], $posted_data["password"]);
			if(!$success){
				$header["site_msg"] = "Invalid username and/or password";
			}
		}
		if($this->session->userdata('logged_in')) {
			redirect('/users/index/'.$this->session->userdata('username'), 'refresh');
		} else{
			$this->load->view('main/login', $header);
		}
	}

	function logout() {
		$this->load->library('login');
		$this->login->logout();
		redirect('/home/', 'refresh');
	}
	
	function signup () {
		$this->load->view('main/signup');
	}
	
	function register() {
		$user_data = $this->input->post('posted_data');
		if($this->form_validation->run() != FALSE){
			$this->load->model('Users_model');
			$created_user_dir = mkdir('./uploads/' . base64_encode($this->encrypt->encode($user_data["username"])), 0777);
			if(!$created_user_dir){
				echo "FATAL ERROR: USER FILE FOLDER CANNOT BE CREATED: " . './uploads/' . base64_encode($this->encrypt->encode($user_data["username"]));				
				die;
			}
			if($this->Users_model->insert_user($user_data)) {
				$this->session->set_flashdata('username', $user_data["username"]);
				$this->session->set_flashdata('password', $user_data["password"]);
				$this->load->view('main/signup_form',array("success" => true));
			}
			else {
				echo "FATAL ERROR: USER CANNOT BE INSERTED";
			}
			
		} else {
			$this->load->view('main/signup_form');
		}
	}
	
	function view_inbox() {
		$this->load->model('Messages_model');
		$inbox = $this->Messages_model->get_inbox($this->session->userdata("username"));
		$this->load->view('main/inbox',array("inbox" => $inbox));
	}
	
	function view_compose_box() {
		$this->load->view('main/inbox_compose');
	}
	
	function send_message() {
		$message_data = $this->input->post('message_data');
		if($this->form_validation->run() != FALSE){
			$this->load->model('Messages_model');
			$this->Messages_model->send_message($message_data);
			$this->load->view('main/inbox_compose', array("success" => true));
		}else {
			$this->load->view('main/inbox_compose');
		}
	}
	
	function delete_message($id = "") {
		if ($id == "")
			return false;
		$this->load->model('Messages_model');
		return $this->Messages_model->delete_message($id);
	}
	
	function detailedSubmit($submissionID =""){
		$content_data['title'] = "Welcome";
		$rightbar_data['color'] = "blk_heading";
		$rightbar_data['title'] = "Info";
		
		$right_bar_comments['color'] = "red_heading";
		$right_bar_comments['title'] = "Comments";
		
		
		$content_data['submissions'] = $this->Submissions_model->get_submission_given_submissionID($submissionID);	
		$content_data['screenshots'] = $this->Submissions_model->get_screenshots($content_data['submissions']['submissionName'], $content_data['submissions']['username']);
		
		$right_bar_comments['comments'] = $this->Comments_model->retrieve_comments($submissionID);
		$rightbar_data['submissions'] = $content_data['submissions'];
		$data['right_bar'] = $this->load->view('template/rightbar_detailed', $rightbar_data,TRUE);
		$data['right_bar'] .= $this->load->view('main/rightbar_comments', $right_bar_comments,TRUE);
		$data["content"] = $this->load->view('main/submissionsDetails', $content_data, TRUE);	
		$this->func_display($data);
	}
}
?>