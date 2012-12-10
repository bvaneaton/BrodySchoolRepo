<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Categories extends MY_Controller {
	function index()
	{
		$content_data['page_title'] = "Select a category to begin";
		$content_data["content"] = $this->load->view('main/categories', array("title"=>"Categories"), TRUE);
		$data['content'] = $this->load->view('template/content', $content_data,TRUE);
		$this->func_display($data);
	}
	
	function browse($lang_en=NULL, $topic_en=NULL)
	{
		$where = array();
		if($lang_en) {
			$where["language_categories.lang_name"] = ($lang_en) ? rawurldecode($lang_en) : "";
		}
		if($topic_en) {
			$where["topics_categories.topic_name"] = ($topic_en) ? rawurldecode($topic_en) : "";
		}
		$submissions = $this->Categories_model->get_submissions($where);
		$title = (($lang_en) ? rawurldecode($lang_en)." > " : "") . (($topic_en) ? rawurldecode($topic_en) : "");
		if($title == ""){
			$title = "Browse Submissions";
		}
		$this->display_results($submissions,$title);
	}
	function search() {
		$request = $this->input->get();
		$keyword_temp = $this->session->flashdata('keyword');
		$keyword = ($request["searchterm"]) ? $request["searchterm"] : "";
		if($keyword == "") {
			$keyword = $keyword_temp;
		}
		$this->session->set_flashdata('keyword', $keyword);
		$where = array(
			"submissionName LIKE" => "%".$keyword."%"
		);
		$submissions = $this->Categories_model->get_submissions($where);
		$this->display_results($submissions,"Search for: " . $keyword);
	}
	private function display_results($submissions = array(),$title = "Results") {
		if($this->is_ajax()){
			$this->load->view('main/print_submission_array', array( "submissions"=>$submissions["submissions"]));
		}else {
			$content_data['page_title'] = $title;
			$browse_data['submissions'] = $submissions["submissions"];
			$browse_data['title'] = "Results:";
			$content_data["content"] = $this->load->view('main/browse', $browse_data, TRUE);
			$data['content'] = $this->load->view('template/content', $content_data,TRUE);
			
			$rightbar_catMenu['tags_list'] = $submissions["tags_list"];
			$rightbar_catMenu['color'] = "red_heading";
			$rightbar_catMenu['title'] = "Refine Results";		
			$data['right_bar'] = $this->load->view('template/rightbar_browse', $rightbar_catMenu,TRUE);
			$this->func_display($data);
		}
	}
}
?>