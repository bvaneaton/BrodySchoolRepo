<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Home extends MY_Controller {
	function index()
	{

		$rightbar_data['color'] = "grn_heading";
		$rightbar_data['title'] = "Example box";
		///$rightbar_data['submissions'] = $this->Submissions_model->voteUp(1);
		$data['right_bar'] = $this->load->view('template/rightbar', $rightbar_data,TRUE);
		
		$content_data['title'] = "Welcome to CodeDepot";
		$content_data['page_title'] = "About us";
		$content_data["content"] = $this->load->view('main/dummy', array("title"=>"Welcome"), TRUE);
		$data['content'] = $this->load->view('template/content', $content_data,TRUE);
		
		$this->set_template('default_template_home');
		$this->func_display($data);
	}
	
	function get_news() {
		$this->set_template('default_template_home');
		$content_data['title'] = "Welcome to CodeDepot";
		$content_data['page_title'] = "NEWS";
		$content_data["content"] = $this->load->view('main/dummy', array("title"=>"Welcome"), TRUE);
		$this->load->view('template/content', $content_data);
	}
	
	function get_top() {
		$this->set_template('default_template_home');
		$content_data['title'] = "Top Submissions";
		$content_data['page_title'] = "RESULTS";
		$content_data['submissions'] = $this->Submissions_model->get_top_ranked_entries();

		$this->load->view('main/browse', $content_data);

	}
	
	function get_recent() {
		$this->set_template('default_template_home');
		$content_data['title'] = "Recent Submissions";
		$content_data['page_title'] = "RESULTS";
		$content_data['submissions'] = $this->Submissions_model->get_top_recent_ranked_entries();
		$this->load->view('main/browse', $content_data);
		
	}
}
?>